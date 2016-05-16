package net.floodlightcontroller.flowstatistics;

/**
 * Created by zhensheng on 2016/5/16.
 */
public class FlowCounter {
    int pkts;
    int octs;
    int timestamp;
    byte tcpflags;
    int drops;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlowCounter that = (FlowCounter) o;

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
        result = 31 * result + timestamp;
        result = 31 * result + (int) tcpflags;
        result = 31 * result + drops;
        return result;
    }
}
