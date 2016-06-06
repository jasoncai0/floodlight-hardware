package net.floodlightcontroller.flowstatistics.web;

import net.floodlightcontroller.flowstatistics.IFlowStatisticsManagerService;
import net.floodlightcontroller.statistics.IStatisticsService;
import net.floodlightcontroller.statistics.web.SwitchStatisticsWebRoutable;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import java.util.Collections;

/**
 * Created by zhensheng on 2016/5/25.
 */
public class ConfigResource extends ServerResource {
    @Post
    @Put
    public Object config() {
        IFlowStatisticsManagerService flowStatisticsManagerService = (IFlowStatisticsManagerService) getContext().getAttributes().get(IStatisticsService.class.getCanonicalName());

        if (getReference().getPath().contains(FlowStatisticsWebRoutable.ENABLE_STR)) {
            flowStatisticsManagerService.updateFlowRecord(true);
            return Collections.singletonMap("statistics-collection", "enabled");
        }

        if (getReference().getPath().contains(FlowStatisticsWebRoutable.DISABLE_STR)) {
            flowStatisticsManagerService.updateFlowRecord(false);
            return Collections.singletonMap("statistics-collection", "disabled");
        }

        return Collections.singletonMap("ERROR", "Unimplemented configuration option");
    }
}
