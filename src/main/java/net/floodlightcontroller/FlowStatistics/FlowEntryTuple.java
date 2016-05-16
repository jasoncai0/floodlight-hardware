package net.floodlightcontroller.flowstatistics;


import org.projectfloodlight.openflow.types.IPv6Address;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.U128;

/**
 * Created by zhensheng on 2016/5/16.
 */
public class FlowEntryTuple {
    IPv6Address srcAddr ;
    IPv6Address dstAddr;
    OFPort srcPort;
    OFPort dstPort;
    byte port;
    byte tos;
    int input;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlowEntryTuple that = (FlowEntryTuple) o;

        if (port != that.port) return false;
        if (tos != that.tos) return false;
        if (input != that.input) return false;
        if (srcAddr != null ? !srcAddr.equals(that.srcAddr) : that.srcAddr != null) return false;
        if (dstAddr != null ? !dstAddr.equals(that.dstAddr) : that.dstAddr != null) return false;
        if (srcPort != null ? !srcPort.equals(that.srcPort) : that.srcPort != null) return false;
        return !(dstPort != null ? !dstPort.equals(that.dstPort) : that.dstPort != null);

    }

    @Override
    public int hashCode() {
        int result = srcAddr != null ? srcAddr.hashCode() : 0;
        result = 31 * result + (dstAddr != null ? dstAddr.hashCode() : 0);
        result = 31 * result + (srcPort != null ? srcPort.hashCode() : 0);
        result = 31 * result + (dstPort != null ? dstPort.hashCode() : 0);
        result = 31 * result + (int) port;
        result = 31 * result + (int) tos;
        result = 31 * result + input;
        return result;
    }
}
