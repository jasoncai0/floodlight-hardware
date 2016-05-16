package net.floodlightcontroller.flowstatistics.web;

import net.floodlightcontroller.restserver.RestletRoutable;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

/**
 * Created by zhensheng on 2016/5/16.
 */
public class FlowStatisticsWebRoutable implements RestletRoutable{

    @Override
    public Restlet getRestlet(Context context) {
        Router router = new Router(context);
        //router.attach("/json",)
        return null;
    }

    @Override
    public String basePath() {
        return "wm/flowstatistics";
    }
}
