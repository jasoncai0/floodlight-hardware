package net.floodlightcontroller.flowstatistics.web;

import net.floodlightcontroller.flowstatistics.IFlowStatisticsManagerService;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by zhensheng on 2016/5/23.
 */
public class FlowStatisticsResource extends ServerResource {
    protected static Logger log = LoggerFactory.getLogger(FlowStatisticsResource.class);

    @Get("json")
    public String retrieve(){
        log.info("FLOW_STATISTICS_RESOURCE:----------rest Get--------");
        System.out.println("FLOW_STATISTICS_RESOURCE:----------rest Get--------");
        IFlowStatisticsManagerService flowStatMgrService =
                (IFlowStatisticsManagerService) getContext().getAttributes().get(IFlowStatisticsManagerService.class.getCanonicalName());
        return flowStatMgrService.getAllActiveFlow();

    }
}
