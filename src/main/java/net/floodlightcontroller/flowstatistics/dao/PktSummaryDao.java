package net.floodlightcontroller.flowstatistics.dao;

import net.floodlightcontroller.flowstatistics.PktSummary;

/**
 * Created by zhensheng on 2016/7/21.
 */
public interface PktSummaryDao {
    public void insertPktInfo(PktSummary pktSummary);
}
