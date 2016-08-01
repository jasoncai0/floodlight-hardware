package net.floodlightcontroller.flowstatistics;

import org.projectfloodlight.openflow.types.IPv6Address;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.TransportPort;

/**
 * Created by zhensheng on 2016/7/20.
 */
public class FlowRecord {

    IPv6Address srcAddr ;
    IPv6Address dstAddr;
    TransportPort srcPort;
    TransportPort dstPort;
    IpProtocol prot;
    byte tos;
    int input;
    private int pkts;
    private int octs;
    private long first;
    private long last;
    private byte tcpflags;
    private int drops;
    private int type;//因为何种原因而不再活跃
    private long timestamp;//



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

    public int getPkts() {
        return pkts;
    }

    public void setPkts(int pkts) {
        this.pkts = pkts;
    }

    public int getOcts() {
        return octs;
    }

    public void setOcts(int octs) {
        this.octs = octs;
    }

    public long getFirst() {
        return first;
    }

    public void setFirst(long first) {
        this.first = first;
    }

    public long getLast() {
        return last;
    }

    public void setLast(long last) {
        this.last = last;
    }

    public byte getTcpflags() {
        return tcpflags;
    }

    public void setTcpflags(byte tcpflags) {
        this.tcpflags = tcpflags;
    }

    public int getDrops() {
        return drops;
    }

    public void setDrops(int drops) {
        this.drops = drops;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public FlowRecord(IPv6Address srcAddr, IPv6Address dstAddr, TransportPort srcPort, TransportPort dstPort, IpProtocol prot, byte tos, int input, int pkts, int octs, long first, long last, byte tcpflags, int drops, int type, long timestamp) {
        this.srcAddr = srcAddr;
        this.dstAddr = dstAddr;
        this.srcPort = srcPort;
        this.dstPort = dstPort;
        this.prot = prot;
        this.tos = tos;
        this.input = input;
        this.pkts = pkts;
        this.octs = octs;
        this.first = first;
        this.last = last;
        this.tcpflags = tcpflags;
        this.drops = drops;
        this.type = type;
        this.timestamp = timestamp;
    }
    public FlowRecord(FlowEntryTuple flowEntryTuple , SwitchPortStatistics statistics,int type, long timestamp){
        this(flowEntryTuple.getSrcAddr(),
                flowEntryTuple.getDstAddr(),
                flowEntryTuple.getSrcPort(),
                flowEntryTuple.getDstPort(),
                flowEntryTuple.getProt(),
                flowEntryTuple.getTos(),
                flowEntryTuple.getInput(),
                statistics.getPkts(),
                statistics.getOcts(),
                statistics.getFirst(),
                statistics.getLast(),
                statistics.getTcpflags(),
                statistics.getDrops(),
                type,timestamp);
    }
    @Override
    public String toString() {
        return "FlowRecord{" +
                "srcAddr=" + srcAddr +
                ", dstAddr=" + dstAddr +
                ", srcPort=" + srcPort +
                ", dstPort=" + dstPort +
                ", prot=" + prot +
                ", tos=" + tos +
                ", input=" + input +
                ", pkts=" + pkts +
                ", octs=" + octs +
                ", first=" + first +
                ", last=" + last +
                ", tcpflags=" + tcpflags +
                ", drops=" + drops +
                ", type=" + type +
                ", timestamp=" + timestamp +
                '}';
    }


}
