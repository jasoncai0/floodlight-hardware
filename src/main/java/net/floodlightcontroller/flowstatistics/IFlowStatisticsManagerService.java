package net.floodlightcontroller.flowstatistics;

import net.floodlightcontroller.core.module.IFloodlightService;
import org.projectfloodlight.openflow.types.IPv6Address;
import org.projectfloodlight.openflow.types.U128;

/**
 * Created by zhensheng on 2016/5/16.
 *
 */


public interface IFlowStatisticsManagerService extends IFloodlightService {
    public String getAllFlow();
    public String getFlowBySrcAddr(IPv6Address srcAddr);
    public String getFlowByTuplle(FlowEntryTuple fet);


}
