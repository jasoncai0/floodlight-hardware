/**
 *    Copyright   2016,  network  research  laboratory  662,INC.
 *    Originally   created  by  sun  xiao  tian
 */
package net.floodlightcontroller.map;

import  net.floodlightcontroller.map.MappingTableManagerResource;
import  net.floodlightcontroller.restserver.RestletRoutable;

import  org.restlet.Context;
import  org.restlet.routing.Router;



public class MappingTableManagerWebRoutable  implements  RestletRoutable  {
	/**
	 * Create   the   Restlet  router    and   bind   to  the  proper   resource.
	 */
	@Override
	public      Router   getRestlet(Context  context)		{
		Router    router	=	new    Router(context);
		router.attach("/json",MappingTableManagerResource.class);

		return router;
	}

	/**
	 * Set  the  base  path  for  the  TestBed
	 */
	@Override
	public    String  basePath()	{
		return  "/wm/mappingtablemanager";
	}

}
