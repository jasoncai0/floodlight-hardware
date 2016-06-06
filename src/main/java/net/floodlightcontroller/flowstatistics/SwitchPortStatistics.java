package net.floodlightcontroller.flowstatistics;

/**
 * Created by zhensheng on 2016/5/22.
 */
public class SwitchPortStatistics {
    private int pkts;
    private int octs;
    private long timestamp;
    private byte tcpflags;
    private int drops;

    public SwitchPortStatistics() {

    }

    public SwitchPortStatistics(int pkts, int octs, long timestamp, byte tcpflags, int drops) {
        this.pkts = pkts;
        this.octs = octs;
        this.timestamp = timestamp;
        this.tcpflags = tcpflags;
        this.drops = drops;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SwitchPortStatistics that = (SwitchPortStatistics) o;

        if (pkts != that.pkts) return false;
        if (octs != that.octs) return false;
        if (timestamp != that.timestamp) return false;
        if (tcpflags != that.tcpflags) return false;
        return drops == that.drops;

    }

    @Override
    public int hashCode() {
        int result = pkts;
        result = 31 * result + octs;
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (int) tcpflags;
        result = 31 * result + drops;
        return result;
    }

    @Override
    public String toString() {
        return "SwitchPortStatistics{" +
                "pkts=" + pkts +
                ", octs=" + octs +
                ", timestamp=" + timestamp +
                ", tcpflags=" + tcpflags +
                ", drops=" + drops +
                '}';
    }
}
