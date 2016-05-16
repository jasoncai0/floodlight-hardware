package net.floodlightcontroller.flowstatistics;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhensheng on 2016/5/16.
 */

/**
 * @param
 */
public class FlowTable {
    private static volatile FlowTable flowTable = new FlowTable();

    private static Map<FlowEntryTuple,FlowCounter> theMap ;
    private FlowTable(){
        theMap = new HashMap<>();
    }
    public  static  FlowTable getInstance() {
        return flowTable;
    }
    public static Map<FlowEntryTuple,FlowCounter> getMap(){
        return theMap;
    }


}
