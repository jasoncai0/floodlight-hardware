package net.floodlightcontroller.flowstatistics;


import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IListener;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.routing.ForwardingBase;
import net.floodlightcontroller.routing.IRoutingDecision;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.types.IPv6Address;

import java.util.HashMap;


/**
 * Created by zhensheng on 2016/5/16.
 */
public class FlowStatisticsManager extends ForwardingBase implements IFlowStatisticsManagerService,IOFMessageListener{
    @Override
    public Command processPacketInMessage(IOFSwitch sw, OFPacketIn pi, IRoutingDecision decision, FloodlightContext cntx) {
        return null;
    }

    @Override
    protected void startUp() {
        super.startUp();
    }

    @Override
    public String getAllFlow() {
        return null;
    }

    @Override
    public String getFlowBySrcAddr(IPv6Address srcAddr) {
        return null;
    }


    @Override
    public String getFlowByTuplle(FlowEntryTuple fet) {
        return null;
    }

    @Override
    public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {

        return super.receive(sw, msg, cntx);

    }
}
