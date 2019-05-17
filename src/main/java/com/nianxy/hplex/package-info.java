/**
 * hplex (Hibernate is too comPLEX)
 * 目标是实现一个非常简单的、纯朴的数据操作中间类，没有太多花哨的东西，当然功能目前也不是很完备~
 *
 * 用法举例：
 *
 * 首先在初始化时，设置数据源，同时注册所有Table对象：
 * HPlexConfigure configure = new HPlexConfigure();
 * configure.setDataSource(DataSource.getInstance())
 *      .registTable(DocumentTable.class)
 *      .registTable(SearchTextTable.class);
 * HPlex.init(configure);
 *
 * 之后，就可以快速使用HPlexTable对象，来创建Query/Delete/...等操作了，比如：
 * FeedbackList data = new HPlexTable(DocumentTable.class).query()
 *      .addField("id")
 *      .addField("name")
 *      .addCond(Cond.compare(CondCompare.compare.LTE, "id", 2))
 *      .addCond(Cond.in("doctype").addValue("wog").addValue("newsclips"))
 *      .addOrder(Order.ASC("id"))
 *      .setLimit(Limit.limit().setMaxSize(10))
 *      .execute();
 *
 * 另外，有个快速的方法可以获取到数据，比如：
 * DocumentTable doc = new HPlexTable(DocumentTable.class).fetchOne("id",1);
 *
 * 关于Table对象：
 * @Table("table_name")
 * class DocumentTable {
 *     @Column(autoinc = true) // 标记为自增，则在插入数据时该字段将被忽略
 *     private int id; // 默认以字段名做为数据表的列名
 *
 *     @Column("column_name")
 *     private String docname; // 如果字段名与数据表名不同，则需要手动指定
 * }
 *
 */
package com.nianxy.hplex;