package com.nianxy.hplex;

import org.junit.Ignore;
import org.junit.Test;

import java.sql.*;
import java.util.Calendar;
import java.util.TimeZone;

public class SQLDateTest {
    // MySQL 8.0 以下版本 - JDBC 驱动名及数据库 URL
    //static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    //static final String DB_URL = "jdbc:mysql://localhost:3306/RUNOOB";

    // MySQL 8.0 以上版本 - JDBC 驱动名及数据库 URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/test?useSSL=false";

    // 数据库的用户名与密码，需要根据自己的设置
    static final String USER = "root";
    static final String PASS = "";

    @Test
    @Ignore
    public void test1() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try{
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            java.sql.Date date = new java.sql.Date(1656604800000L);
            System.out.println("before insert:" + date);

            //TimeZone zone = TimeZone.getTimeZone("GMT+8");
            TimeZone zone = TimeZone.getDefault();
            System.out.println("time zone:" + zone);

            pstmt = conn.prepareStatement("insert into table1 (`date`)values(?)");
            pstmt.setDate(1, date, Calendar.getInstance(zone));
            pstmt.executeUpdate();

            // 执行查询
            String sql = "SELECT id, `date` FROM table1 order by id desc limit 1";
            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            // 展开结果集数据库
            while(rs.next()){
                // 通过字段检索
                int id = rs.getInt(1);
                //date = rs.getDate(2, Calendar.getInstance(zone));
                date = rs.getDate(2);

                // 输出数据
                System.out.println("id: " + id);
                System.out.println("date: " + date);
            }
            // 完成后关闭
            conn.close();
        }catch(SQLException se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }catch(Exception e){
            // 处理 Class.forName 错误
            e.printStackTrace();
        }finally{
            // 关闭资源
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
        System.out.println("Goodbye!");
    }
}
