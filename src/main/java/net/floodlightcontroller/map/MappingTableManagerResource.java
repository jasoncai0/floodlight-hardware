/**
 *    Copyright   2016,  network  research  laboratory  662,INC.
 *    Originally   created  by  sun  xiao  tian
 */
package net.floodlightcontroller.map;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MappingTableManagerResource   extends ServerResource {
	protected static Logger log = LoggerFactory.getLogger(MappingTableManagerResource.class);
	@Get("json")
	public   String   retrieve() {
		System.out.println("test  AAAAAAAA !!!!!!!!!!!!!!");
		IMappingTableManagerService  map =
				(IMappingTableManagerService)getContext().getAttributes().
				get(IMappingTableManagerService.class.getCanonicalName());
		return   map.getAllRules();
	}

}
