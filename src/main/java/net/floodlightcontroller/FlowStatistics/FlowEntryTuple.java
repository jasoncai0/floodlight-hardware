package net.floodlightcontroller.flowstatistics;


import org.projectfloodlight.openflow.types.*;

import java.util.Comparator;

/**
 * Created by zhensheng on 2016/5/16.
 */
public class FlowEntryTuple   {
    IPv6Address srcAddr ;

    IPv6Address dstAddr;
    TransportPort srcPort;
    TransportPort dstPort;
    IpProtocol prot;
    byte tos;
    int input;

    public IPv6Address getSrcAddr() {
        return srcAddr;
    }

    public void setSrcAddr(IPv6Address srcAddr) {
        this.srcAddr = srcAddr;
    }

    public IPv6Address getDstAddr() {
        return dstAddr;
    }

    public void setDstAddr(IPv6Address dstAddr) {
        this.dstAddr = dstAddr;
    }

    public TransportPort getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(TransportPort srcPort) {
        this.srcPort = srcPort;
    }

    public TransportPort getDstPort() {
        return dstPort;
    }

    public void setDstPort(TransportPort dstPort) {
        this.dstPort = dstPort;
    }

    public IpProtocol getProt() {
        return prot;
    }

    public void setProt(IpProtocol prot) {
        this.prot = prot;
    }

    public byte getTos() {
        return tos;
    }

    public void setTos(byte tos) {
        this.tos = tos;
    }

    public int getInput() {
        return input;
    }

    public void setInput(int input) {
        this.input = input;
    }

    @Override
    public String toString() {
        return "FlowEntryTuple{" +
                "srcAddr=" + srcAddr +
                ", dstAddr=" + dstAddr +
                ", srcPort=" + srcPort +
                ", dstPort=" + dstPort +
                ", prot=" + prot +
                ", tos=" + tos +
                ", input=" + input +
                '}';
    }

    public FlowEntryTuple(IPv6Address srcAddr, IPv6Address dstAddr, TransportPort srcPort,
                          TransportPort dstPort, IpProtocol prot, byte tos, int input) {
        this.srcAddr = srcAddr;
        this.dstAddr = dstAddr;
        this.srcPort = srcPort;
        this.dstPort = dstPort;
        this.prot = prot;
        this.tos = tos;
        this.input = input;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlowEntryTuple that = (FlowEntryTuple) o;

        if (tos != that.tos) return false;
        if (input != that.input) return false;
        if (srcAddr != null ? !srcAddr.equals(that.srcAddr) : that.srcAddr != null) return false;
        if (dstAddr != null ? !dstAddr.equals(that.dstAddr) : that.dstAddr != null) return false;
        if (srcPort != null ? !srcPort.equals(that.srcPort) : that.srcPort != null) return false;
        if (dstPort != null ? !dstPort.equals(that.dstPort) : that.dstPort != null) return false;
        return !(prot != null ? !prot.equals(that.prot) : that.prot != null);

    }

    @Override
    public int hashCode() {
        int result = srcAddr != null ? srcAddr.hashCode() : 0;
        result = 31 * result + (dstAddr != null ? dstAddr.hashCode() : 0);
        result = 31 * result + (srcPort != null ? srcPort.hashCode() : 0);
        result = 31 * result + (dstPort != null ? dstPort.hashCode() : 0);
        result = 31 * result + (prot != null ? prot.hashCode() : 0);
        result = 31 * result + (int) tos;
        result = 31 * result + input;
        return result;
    }
}
