package com.sci.ycox.flink.util;

import com.sci.ycox.flink.bean.DbConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @Description 数据库连接工具类
 * @Author xyb
 * @Date 2019-07-30 14:59:05
 * @Version 1.0.0
 */
public class DbConnectUtil {

    private static DbConfig dbConfig = YamlConfigReader.getDbConfig();

    /** 获取数据库连接 自动提交
     * @Description
     * @Date 2019-07-30 14:59:20
     * @return java.sql.Connection
     * @throws
     */
    public static Connection defaultAutoConnection () throws SQLException {
        Connection connection = getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword());
        connection.setAutoCommit(true);
        return connection;
    }

    /**
     * @Description 数据库连接
     * @Date 2019-07-30 14:59:50
     * @return java.sql.Connection
     * @throws
     */
    public static Connection defaultConnection () throws SQLException {
        return getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword());
    }


    private static Connection getConnection(String url, String user, String password) throws SQLException {
        Connection conn = null;
        try {
            Class.forName(dbConfig.getDriverClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        conn = DriverManager.getConnection(url, user, password);
        //设置手动提交
        conn.setAutoCommit(false);
        return conn;
    }

    /**
     * @Description  提交事物
     * @Date 2019-07-30 15:01:03
     * @param conn
     * @return void
     * @throws
     */
    public static void commit(Connection conn) {
        if (conn != null) {
            try {
                conn.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                close(conn);
            }
        }
    }

    /**
     * @Description 事物回滚
     * @Date 2019-07-30 15:00:46
     * @param  conn
     * @return void
     * @throws
     */
    public static void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                close(conn);
            }
        }
    }

    /**
     * @Description 关闭连接
     * @Date 2019-07-30 15:00:23
     * @param conn 连接
     * @return void
     * @throws
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
