package net.floodlightcontroller.flowstatistics.util;

import net.floodlightcontroller.devicemanager.SwitchPort;
import net.floodlightcontroller.flowstatistics.FlowEntryTuple;
import net.floodlightcontroller.flowstatistics.FlowRecordAge;
import net.floodlightcontroller.flowstatistics.SwitchPortStatistics;

import java.util.List;

/**
 * Created by zhensheng on 2016/7/18.
 */
public interface FlowStatisticsDAO {
    public void insertFlow(FlowPersistence flowPersistence);
    public void updateFlow(FlowPersistence flowPersistence);
    public List<FlowPersistence> findByTuple(FlowEntryTuple flowEntryTuple);
    public List<FlowPersistence> findAll();
}
