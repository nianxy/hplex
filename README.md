# 使用说明

##初始化
首先需要实现*SimpleDataSoruce*接口，提供获取DB连接的方法
```java
public class HPlexDataSource implements SimpleDataSoruce {
    // You can add an init method to write some initial code
    @Override
    public Connection getConnection() {
        // TODO: return a db connection
    }
}
```

接下来创建*HPlexConfigure*，并使用*HPlexConfigure*对象来初始化HPlex：
```java
HPlexConfigure configure = new HPlexConfigure();
try {
    // 注册数据表对象，后面说明
    // registTable()可以进行链式调用
    configure.registTable(TestTable.class);
    // 设置数据源
    configure.setDataSource(new HPlexDataSource());
    // 使用HPlexConfigure对象初始化HPlex
    HPlex.init(configure);
} catch (InvalidAutoincFieldTypeException e) {
    e.printStackTrace();
} catch (UnsupportedFieldTypeException e) {
    e.printStackTrace();
}
```

##数据表对象
数据库中的每张表对象一个数据表对象
```java
@Table("test")
public class TestTable {
    @Column(autoinc = true)
    private int id;

    @Column
    private String name;

    @Column("bvalue")
    private Boolean boolValue;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getBoolValue() {
        return boolValue;
    }

    public void setBoolValue(Boolean boolValue) {
        this.boolValue = boolValue;
    }
}
```

@Table注解用于标识这是一个数据表对象类，注解的值对应数据库中的表名
@Column注解用于标识这是一个数据表中的字段，默认将字段名做为数据库表中的字段名称，如果不同的话可以通过注解的值进行指定。autoinc属性用来标识这是一个自增字段 。
Boolean类型的字段映射到数据库中的数值型字段，比如int/byte等，值为0是为false，1为true

另外，支持JSON格式的字段，数据库中表现为字段串类型，HPlex在读取数据库时将字符串自动解析为对象，或在更新数据库时自动将对象转换为JSON字符串。要实现这个功能，需要做两件事：
- 实现IJSONConvert接口，定义好JSON转换相关方法
- 调用HPlexConfigure对象的setJSONConvert方法
- 数据表对象中相应字段的类型，需要实现IJSONColumn接口

以Jackson为例，代码如下：
```java
public class JSONConvert implements IJSONConvert {
    @Override
    public <T extends IJSONColumn> T toObject(String s, Class<T> clz) throws JSONConvertException {
        try {
            return new ObjectMapper().readValue(s, clz);
        } catch (IOException e) {
            throw new JSONConvertException(e);
        }
    }

    @Override
    public <T extends IJSONColumn> String toJSONString(Object o, Class<T> clz) throws JSONConvertException {
        try {
            return new ObjectMapper().writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new JSONConvertException(e);
        }
    }
}

// 调用HPlexConfigure对象的setJSONConvert方法
configure.setJSONConvert(new JSONConvert());
// ...

// 数据表字段类型
public class JsonField implements IJSONColumn {
    private String msg;
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public String getMsg() {
        return msg;
    }
}

// 数据表定义
@Table("test")
public class TestTable {
    @Column(autoinc = true)
    private int id;
    
    @Column
    private JsonField json_field;
    // ...
}
```

##查询
```java
// 获取查询对象
Query query = new HPlexTable(CertificateTable.class).query();
// 增加查询条件
query.addCond(Cond.compare(CondCompare.Compare.EQ, "id", 10));
query.addCond(Cond.compare(CondCompare.Compare.GT, "size", 462));
// 设置排序
query.addOrder(Order.ASC("timestamp"));
// 设置Limit
query.addLimit(Limit.limit().setOffset(100).setMaxSize(20));
// 查询
query.execute();
```
以上也可通过链式调用来完成：
```java
new HPlexTable(CertificateTable.class).query();
    .addCond(Cond.compare(CondCompare.Compare.EQ, "id", 10));
    .addCond(Cond.compare(CondCompare.Compare.GT, "size", 462));
    .addOrder(Order.ASC("timestamp"));
    .addLimit(Limit.limit().setOffset(100).setMaxSize(20));
    .execute();
```
更多Query对象相关方法可以参考源码。

##其它操作
可以通过HPlexTable对象的insert()/update()/delete()来获取得相应操作的对象，使用方法与Query对象类似

##事务
HPlex在每次操作DB时都通过*SimpleDataSoruce*对象获取一个链接，如果想使用DB事务，需要为HPlex一系列操作指定一个DB连接，并在外层控制事务。这些操作可以通过*HPlexTraansaction*对象来完成。
```java
HPlexTransaction transaction = new HPlexTransaction();
try {
    // 开启事务
    transaction.start();
    // 不要自己创建HPlexTable对象
    transaction.hPlexTable(BusinessTable.class);
    // do what you want to do ...
    // ...
    // 提交事务
    transaction.commit();
} final {
    if (transaction.isInTransaction()) {
        try {
            transaction.rollback();
        } catch (TransactionNotStartedException|SQLException ex) {
            ex.printStackTrace();
        }
    }
}
```

