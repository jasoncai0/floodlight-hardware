package net.floodlightcontroller.flowstatistics.dao.impl;

import net.floodlightcontroller.flowstatistics.FlowEntryTuple;
import net.floodlightcontroller.flowstatistics.FlowRecord;
import net.floodlightcontroller.flowstatistics.dao.FlowRecordDao;
import net.floodlightcontroller.flowstatistics.util.SqlConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by zhensheng on 2016/7/21.
 */
public class FlowRecordDaoImpl implements FlowRecordDao {


    @Override
    public void insertFlow(FlowRecord flowRecord) {
        long cur= System.currentTimeMillis();
        System.out.println("START INSERT FLOW ");
        Connection conn =null;
        PreparedStatement stmt = null;
        String preSQL= "insert into controller_flow_statistics(src_addr  , dst_addr," +
                " src_port,dst_port, prot,tos , input_id, pkts,octs, first_time,last_time , " +
                "tcp_flags, drops,fin_type, time_stamp ) " +
                "value(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
        /*
        String sql= "INSERT INTO controller_flow_statistics(src_addr, dst_addr, src_port,dst_port, prot,tos ,input_id, pkts,octs, first_time,last_time , tcp_flags, drops,fin_type, time_stamp )" +
                " value( '?','?','?','?','?','?','?','?','?','?','?','?','?','?','?');";
        */
        try {
            System.out.println("start get connection!");
            conn = SqlConnection.getConnection();
            if(!conn.isClosed()) System.out.println("Connection established ");
            stmt = conn.prepareStatement(preSQL);

            stmt.setBytes(1 ,flowRecord.getSrcAddr().getBytes());
            stmt.setBytes(2,flowRecord.getDstAddr().getBytes());
            stmt.setInt(3,flowRecord.getSrcPort().getPort());
            stmt.setInt(4,flowRecord.getDstPort().getPort());
            stmt.setInt(5,flowRecord.getProt().getIpProtocolNumber());
            stmt.setInt(6,flowRecord.getTos());
            stmt.setInt(7,flowRecord.getInput());
            stmt.setInt(8,flowRecord.getPkts());
            stmt.setInt(9,flowRecord.getOcts());
            stmt.setLong(10,flowRecord.getFirst());
            stmt.setLong(11,flowRecord.getLast());
            stmt.setInt(12,flowRecord.getTcpflags());
            stmt.setInt(13,flowRecord.getDrops());
            stmt.setInt(14,flowRecord.getType());
            stmt.setLong(15,flowRecord.getTimestamp());
            stmt.executeUpdate();
            System.out.println("insert into the db!");

        } catch (SQLException e) {
            System.out.println("insert failed!");
            e.printStackTrace();
        }finally {
            SqlConnection.close(null, stmt, conn);
        }
        System.out.println("数据库时间开销:"+(System.currentTimeMillis()-cur) +"ms");
    }

    @Override
    public void updateFlow(FlowRecord flowRecord) {

    }

    @Override
    public List<FlowRecord> findByTuple(FlowEntryTuple flowEntryTuple) {
        return null;
    }

    @Override
    public List<FlowRecord> findAll() {
        return null;
    }
}
