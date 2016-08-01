package net.floodlightcontroller.flowstatistics.util;


import net.floodlightcontroller.flowstatistics.FlowEntryTuple;
import net.floodlightcontroller.flowstatistics.util.SqlConnection;
import org.projectfloodlight.openflow.types.IPv6Address;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.TransportPort;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Created by zhensheng on 2016/7/18.
 */
public class FlowStatisticsDAOImpl implements FlowStatisticsDAO {

    @Override
    public void insertFlow(FlowPersistence flowPersistence) {
        System.out.println("START INSERT FLOW ");
        Connection conn =null;
        PreparedStatement stmt = null;
        String preSQL= "insert into controller_flow_statistics(src_addr  , dst_addr," +
                " src_port,dst_port, prot,tos , input_id, pkts,octs, first_time,last_time , " +
                "tcp_flags, drops,fin_type, time_stamp ) " +
                "value(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
        String sql= "INSERT INTO controller_flow_statistics(src_addr, dst_addr, src_port,dst_port, prot,tos ,input_id, pkts,octs, first_time,last_time , tcp_flags, drops,fin_type, time_stamp )" +
                " value( '?','?','?','?','?','?','?','?','?','?','?','?','?','?','?');";
        try {
            System.out.println("start get connection!");
            conn = SqlConnection.getConnection();
            if(!conn.isClosed()) System.out.println("Connection established ");
            stmt = conn.prepareStatement(preSQL);

            /*
            Statement stmt2= conn.createStatement();
            String update ="insert into controller_flow_statistics(src_addr  , dst_addr, src_port,dst_port, prot,tos , input_id, pkts,octs, first_time,last_time , tcp_flags, drops,fin_type, time_stamp ) value(1,2,5,6,7,8,9,10,11,12,13,14,15,16,17);";
            stmt2.executeUpdate(update);
            System.out.println("insert succ");
            */

            stmt.setBytes(1 ,flowPersistence.getSrcAddr().getBytes());
            stmt.setBytes(2,flowPersistence.getDstAddr().getBytes());
            stmt.setInt(3,flowPersistence.getSrcPort().getPort());
            stmt.setInt(4,flowPersistence.getDstPort().getPort());
            stmt.setInt(5,flowPersistence.getProt().getIpProtocolNumber());
            stmt.setInt(6,flowPersistence.getTos());
            stmt.setInt(7,flowPersistence.getInput());
            stmt.setInt(8,flowPersistence.getPkts());
            stmt.setInt(9,flowPersistence.getOcts());
            stmt.setLong(10,flowPersistence.getFirst());
            stmt.setLong(11,flowPersistence.getLast());
            stmt.setInt(12,flowPersistence.getTcpflags());
            stmt.setInt(13,flowPersistence.getDrops());
            stmt.setInt(14,flowPersistence.getType());
            stmt.setLong(15,flowPersistence.getTimestamp());
            stmt.executeUpdate();
            System.out.println("insert into the db!");



        } catch (SQLException e) {
            System.out.println("insert failed!");
            e.printStackTrace();
        }finally {
            SqlConnection.close(null, stmt, conn);
        }


    }

    @Override
    public void updateFlow(FlowPersistence flowPersistence) {

    }

    @Override
    public List<FlowPersistence> findByTuple(FlowEntryTuple flowEntryTuple) {
        return null;
    }

    @Override
    public List<FlowPersistence> findAll() {
        return null;
    }

    public static void main(String []args){
        IPv6Address srcAddr = IPv6Address.of(11L, 11L);
        IPv6Address dstAddr = IPv6Address.of(12L,12L);
        TransportPort srcPort = TransportPort.of(11);
        TransportPort dstPort = TransportPort.of(12);
        //int prot = 67;
        IpProtocol prot = IpProtocol.of((byte)67);
        byte tos = 1;
        int input= 1 ;
        int pkts=1212;
        int octs=123123;
        long first=System.currentTimeMillis();
        long last= System.currentTimeMillis();
        byte tcpflags= 11;
        int drops= 1223;
        int type =2;//因为何种原因而不再活跃
        long timestamp= System.currentTimeMillis();//
        FlowPersistence f = new FlowPersistence(srcAddr,dstAddr,srcPort,dstPort,prot,tos,input,pkts
        ,octs,first,last,tcpflags,drops,type,timestamp);
        FlowStatisticsDAO fsDAO = new FlowStatisticsDAOImpl();
        fsDAO.insertFlow(f);




    }


}
