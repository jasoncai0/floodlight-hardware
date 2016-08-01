package net.floodlightcontroller.flowstatistics.web;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import net.floodlightcontroller.flowstatistics.FlowEntryTuple;
import net.floodlightcontroller.flowstatistics.SwitchPortStatistics;

import java.io.IOException;
import java.util.Map;

/**
 * Created by zhensheng on 2016/5/23.
 */
public class FlowStatisticsSerializer extends JsonSerializer<Map<FlowEntryTuple,SwitchPortStatistics>> {


    @Override
    public void serialize(Map<FlowEntryTuple, SwitchPortStatistics> portStats, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.configure(Feature.WRITE_NUMBERS_AS_STRINGS,true);

        jsonGenerator.writeStartObject();


        for(Map.Entry<FlowEntryTuple,SwitchPortStatistics> portStatsEntry : portStats.entrySet()){
            jsonGenerator.writeStringField("srcAddr",portStatsEntry.getKey().getSrcAddr().toString());
            jsonGenerator.writeStringField("dstAddr",portStatsEntry.getKey().getDstAddr().toString());
            jsonGenerator.writeStringField("srcOFPort",portStatsEntry.getKey().getSrcPort().toString());
            jsonGenerator.writeStringField("dstOFPort",portStatsEntry.getKey().getDstPort().toString());
            jsonGenerator.writeStringField("prot", String.valueOf(portStatsEntry.getKey().getProt()));
            jsonGenerator.writeStringField("tos", String.valueOf(portStatsEntry.getKey().getTos()));
            jsonGenerator.writeStringField("input",String.valueOf(portStatsEntry.getKey().getInput()));
            jsonGenerator.writeStringField("pkts", String.valueOf(portStatsEntry.getValue().getPkts()));
            jsonGenerator.writeStringField("octs", String.valueOf(portStatsEntry.getValue().getOcts()));
            jsonGenerator.writeStringField("first", String.valueOf(portStatsEntry.getValue().getFirst()));
            jsonGenerator.writeStringField("last", String.valueOf(portStatsEntry.getValue().getLast()));
            jsonGenerator.writeStringField("tcpFlags", String.valueOf(portStatsEntry.getValue().getTcpflags()));
            jsonGenerator.writeStringField("drops", String.valueOf(portStatsEntry.getValue().getDrops()));
        }
        jsonGenerator.writeEndObject();
    }
}
