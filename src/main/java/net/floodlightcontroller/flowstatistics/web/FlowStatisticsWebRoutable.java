package net.floodlightcontroller.flowstatistics.web;

import net.floodlightcontroller.restserver.RestletRoutable;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

/**
 * Created by zhensheng on 2016/5/16.
 */
public class FlowStatisticsWebRoutable implements RestletRoutable{
    protected static final String ENABLE_STR = "enable";
    protected static final String DISABLE_STR = "disable";
    @Override
    public Restlet getRestlet(Context context) {
        Router router = new Router(context);
        router.attach("/json",FlowStatisticsResource.class);
        router.attach("/pps",PPSRealtimeResource.class);
        return router;
    }

    /**
     * the rest api for active flow statistics
     * @return thr url of api for active flow statistics
     */
    @Override
    public String basePath() {
            return "/wm/flowstatistics";
    }
}
