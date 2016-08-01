package net.floodlightcontroller.flowstatistics.dao;

import net.floodlightcontroller.flowstatistics.FlowEntryTuple;
import net.floodlightcontroller.flowstatistics.FlowRecord;
import net.floodlightcontroller.flowstatistics.util.FlowPersistence;

import java.util.List;

/**
 * Created by zhensheng on 2016/7/21.
 */
public interface FlowRecordDao {
    public void insertFlow(FlowRecord flowRecord);
    public void updateFlow(FlowRecord flowRecord);
    public List<FlowRecord> findByTuple(FlowEntryTuple flowEntryTuple);
    public List<FlowRecord> findAll();
}
