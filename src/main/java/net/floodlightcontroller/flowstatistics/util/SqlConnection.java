package net.floodlightcontroller.flowstatistics.util;

import java.sql.*;
import java.util.ResourceBundle;

/**
 * Created by zhensheng on 2016/7/18.
 */
public class SqlConnection {
    /*
    public static String URL ;
    public static String USERNAME ;
    public static String PASSWORD ;
    public static String DRIVER;*/
    public static String URL ="jdbc:mysql://localhost:3306/xtrcontroller";
    public static String USERNAME = "root";
    public static String PASSWORD = "wjcai376";
    public static String DRIVER = "com.mysql.jdbc.Driver";
    //private static ResourceBundle rb = ResourceBundle.getBundle("com.util.db.db-config");
    public static Connection conn =null ;
    public SqlConnection(){
    }

    static{
        /*
        URL = rb.getString("jdbc.url");
        USERNAME = rb.getString("jdbc.username");
        PASSWORD = rb.getString("jdbc.password");
        DRIVER = rb.getString("jdbc.driver");
        */

        URL ="jdbc:mysql://localhost:3306/xtrcontroller";
        USERNAME = "root";
        PASSWORD = "wjcai376";
        DRIVER = "com.mysql.jdbc.Driver";
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static Connection getConnection(){
        /*
        Connection conn = null;
        try{
            Class.forName(DRIVER);
            System.out.println("DRIVER SUCCESS");

        } catch (ClassNotFoundException e) {
            System.out.println("Sorry,cant't  find  the  Driver!!");
            e.printStackTrace();
        }
        try{
            conn = DriverManager.getConnection(URL,USERNAME,PASSWORD);

        } catch (SQLException e) {
            System.out.println("DB CONNNECT FAILED");
            e.printStackTrace();
        }
        return conn;
        */

        try {
            //Class.forName("com.mysql.jdbc.Driver");
            String  url = "jdbc:mysql://127.0.0.1:3306/xtrcontroller";
            String user = "root";
            String password = "wjcai376";
            conn =  DriverManager.getConnection(url, user, password);
            System.out.println("CONNECT SUCC!");
        }  catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;

    }

    public static void close(ResultSet rs, Statement stmt, Connection conn){
        try {
            if(rs!=null) rs.close();
            if(stmt!=null )stmt.close();
            if(conn!= null ) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
