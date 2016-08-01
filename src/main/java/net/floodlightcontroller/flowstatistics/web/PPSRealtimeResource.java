package net.floodlightcontroller.flowstatistics.web;

import net.floodlightcontroller.flowstatistics.IFlowStatisticsManagerService;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhensheng on 2016/7/27.
 */
public class PPSRealtimeResource extends ServerResource {

    protected static Logger log = LoggerFactory.getLogger(PPSRealtimeResource.class);

    /**
     * TODO:用于添加实时流量显示的模块，增加实时动态曲线
     * @return
     */
    @Get("json")
    public String pps(){
        log.info("FLOW_STATISTICS_RESOURCE:----------rest Get--------");
        System.out.println("FLOW_STATISTICS_RESOURCE:----------rest Get--------");
        IFlowStatisticsManagerService flowStatMgrService =
                (IFlowStatisticsManagerService) getContext().getAttributes().get(IFlowStatisticsManagerService.class.getCanonicalName());
        return flowStatMgrService.getRealtimePPS();

    }
}
