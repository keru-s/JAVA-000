package com.ins.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author 01387005
 * @since 2020-11-19 09:52
 **/
public class JdbcStatementCase {
    private static Connection connection;

    static {
        try {
            InputStream inStream = JdbcStatementCase.class.getClassLoader().getResourceAsStream("application.properties");
            Properties properties = new Properties();
            properties.load(inStream);

            String url = properties.getProperty("url");
            String user = properties.getProperty("user");
            String password = properties.getProperty("password");

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * cccpmain-m.dbsit.sfdc.com.cn:3306
     * cccpmainetl、Az1_j5SJHzBJ5g
     * test
     * */

    public static void main(String[] args) throws SQLException {
        query();
        insert();
        query();
        update();
        query();
        delete();
        query();
    }

    private static void query() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("select * from student;");
            while (resultSet.next()) {
                System.out.println("resultSet = " + resultSet.getString("name"));
            }
            System.out.println("----------------------------------");
        }
    }

    private static void insert(){
        try(Statement statement = connection.createStatement()) {
            String sql = "insert into student(name) value ('张无忌')";
            final int result = statement.executeUpdate(sql);
            System.out.println("result = " + result);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static void update(){
        try(Statement statement = connection.createStatement()) {
            String sql = "update student set name='赵敏' where id=3";
            final int result = statement.executeUpdate(sql);
            System.out.println("result = " + result);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static void delete(){
        try(Statement statement = connection.createStatement()) {
            String sql = "delete from student where id=3";
            final int result = statement.executeUpdate(sql);
            System.out.println("result = " + result);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
