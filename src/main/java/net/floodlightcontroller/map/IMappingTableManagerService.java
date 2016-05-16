/**
 *    Copyright   2016,  network  research  laboratory  662,INC.
 *    Originally   created  by  sun  xiao  tian
 */
package net.floodlightcontroller.map;


import   net.floodlightcontroller.core.module.IFloodlightService;

public interface IMappingTableManagerService   extends   IFloodlightService {
	/**
	 *  获取数据库中的所有表项.
	 */
    public   String  getAllRules();
    /**
     * 根据EID，获取数据库中对应的表项
     */
    public  String  getRules(String  eid);
}
