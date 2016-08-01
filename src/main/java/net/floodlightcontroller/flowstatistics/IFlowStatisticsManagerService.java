package net.floodlightcontroller.flowstatistics;

import net.floodlightcontroller.core.module.IFloodlightService;
import org.projectfloodlight.openflow.types.IPv6Address;
import org.projectfloodlight.openflow.types.U128;

import java.util.Map;

/**
 * Created by zhensheng on 2016/5/16.
 *
 */


public interface IFlowStatisticsManagerService extends IFloodlightService {
    String getAllActiveFlow();
    String getRealtimePPS();
    String getFlowBySrcAddr(IPv6Address srcAddr);
    String getFlowByTuple(FlowEntryTuple fet);
    void updateFlowRecord(boolean update);

    //public Map<FlowEntryTuple,SwitchPortStatistics> getStatistics();

}
