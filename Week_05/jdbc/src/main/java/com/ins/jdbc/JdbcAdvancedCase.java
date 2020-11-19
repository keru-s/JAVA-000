package com.ins.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author 01387005
 * @since 2020-11-19 09:52
 **/
public class JdbcAdvancedCase {
    private static Connection connection;

    static {
        try {
            InputStream inStream = JdbcAdvancedCase.class.getClassLoader().getResourceAsStream("application.properties");
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

    public static void main(String[] args) throws SQLException {
        query();
        update();
        query();
        delete();
        query();
        insertWithTransaction();
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

    /**
     * 使用事务
     */
    private static void insertWithTransaction() {
        String sql = "insert into student(name) value (?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            statement.setString(1, "张无忌");
            statement.executeUpdate();
            statement.setString(1, "赵敏");
            //制造异常
            int tmp = 1 / 0;
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static void update() {
        String sql = "update student set name=? where id=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "赵敏");
            statement.setInt(2, 7);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static void delete() {
        String sql = "delete from student where id=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, 7);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
