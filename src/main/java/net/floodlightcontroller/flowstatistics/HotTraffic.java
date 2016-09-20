package net.floodlightcontroller.flowstatistics;

import java.util.Map;
import java.util.PriorityQueue;

/**
 *
 * 用于维护热点流量表，热点流量表用于显示TOPN流量，用于维护
 * Created by zhensheng on 2016/9/19.
 */
public class HotTraffic {

    private static PriorityQueue<FlowEntryTuple> hotTraffics = new PriorityQueue<>();

    public static PriorityQueue<FlowEntryTuple> getHotTraffics() {
        return hotTraffics;
    }

    public void insertFlow(FlowEntryTuple f){

    }

}
