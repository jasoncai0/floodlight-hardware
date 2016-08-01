package net.floodlightcontroller.flowstatistics.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * Created by zhensheng on 2016/7/20.
 */
public class JDBC {
    protected static Logger log;
    protected static Connection connection;
    public void connectDatabase(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String  url = "jdbc:mysql://127.0.0.1:3306/xtrcontroller";
            String user = "root";
            String password = "wjcai376";
            connection  =  DriverManager.getConnection(url, user, password);
            if(!connection.isClosed())
                System.out.println("SUCCESS!");
            Statement stmt= connection.createStatement();
            String update ="insert into controller_flow_statistics(src_addr  , dst_addr, src_port,dst_port, prot,tos , input_id, pkts,octs, first_time,last_time , tcp_flags, drops,fin_type, time_stamp ) value(1,2,5,6,7,8,9,10,11,12,13,14,15,16,17);";
            stmt.executeUpdate(update);

            /*
            String query = "";
            ResultSet resultSet = stmt.executeQuery(query);
            while (resultSet.next()){
                resultSet.getString("dddd");
            }**/
            connection.close();

        }catch(ClassNotFoundException e) {
            System.out.println("Sorry,cant't  find  the  Driver!!");

            e.printStackTrace();
        }catch(SQLException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }

    }
    public static void main(String []args){
        JDBC s = new JDBC();
        s.connectDatabase();

    }
}
