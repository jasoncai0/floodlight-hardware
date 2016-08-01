package net.floodlightcontroller.flowstatistics;


import net.floodlightcontroller.core.*;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.flowstatistics.dao.FlowRecordDao;
import net.floodlightcontroller.flowstatistics.dao.impl.FlowRecordDaoImpl;
import net.floodlightcontroller.flowstatistics.util.FlowPersistence;
import net.floodlightcontroller.flowstatistics.util.FlowStatisticsDAO;
import net.floodlightcontroller.flowstatistics.util.FlowStatisticsDAOImpl;
import net.floodlightcontroller.flowstatistics.util.SqlConnection;
import net.floodlightcontroller.flowstatistics.web.FlowStatisticsWebRoutable;

import net.floodlightcontroller.packet.PacketParsingException;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.routing.ForwardingBase;
import net.floodlightcontroller.routing.IRoutingDecision;

import net.floodlightcontroller.threadpool.IThreadPoolService;

import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.types.IPv6Address;
import org.projectfloodlight.openflow.types.OFPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;


/**
 * Created by zhensheng on 2016/5/16.
 */
public class FlowStatisticsManager extends ForwardingBase implements IFlowStatisticsManagerService,IOFMessageListener,IFloodlightModule{


    /**
     *DUFAULT PARAMETER
     */
    private static final int FLOW_CACHE_SIZE = 1024;
    private static final int FLOW_RECORD_LASTUPDATE_MAX =15;
    //private static final int FLOW_RECORD_AGE_MAX = 1800;//30min
    private static final int FLOW_RECORD_AGE_MAX = 60;

    private static final int FLOW_PKT_SUMMARY_LENGTH = 68;
    private static final int FLOW_PKTIN_DATA_PADDING = 16;
    //TODO: 流量自适应抽样模块，需要根据网络中流量调节抽样比，使得网络中吞吐量占据一定的比。
    private static int PKT_SAMPLING_RATIO = 1; //抽样比，根据网络中流量大小自适应调节。
    /**
     * flowStats store the map of FlowEntry & SwitchPortStatistics
     */
    private static final HashMap<FlowEntryTuple,SwitchPortStatistics> flowStats= new HashMap<>();
    private static final HashMap<FlowEntryTuple,SwitchPortStatistics> tentativeflowStats =  new HashMap<>();
    /**
     *flowages store the ages of flow records
     */
    private static final HashMap<FlowEntryTuple,FlowRecordAge> flowAges =new HashMap<>();
    /**
     * portStats store the pkts statistics
     */
    private static final PktCounter pktCounter = new PktCounter();
    private static final PiCounter piCounter = new PiCounter();

    private static boolean isEnabled = true ;
    private static ScheduledFuture<?> flowRecordUpdater;
    private static  int flowRecordInterval =1 ;
    protected IFloodlightProviderService floodlightProvider;
    protected IRestApiService restApi;
    protected IThreadPoolService threadPoolService;
    protected Logger logger;
    //private static Connection connection;
    protected static final Logger log = LoggerFactory.getLogger(FlowStatisticsManager.class);

    /**
     * get flow ages for Updater Runnable to get the recorder older;
     * @return the map of flow ages
     */
    public static HashMap<FlowEntryTuple,FlowRecordAge> getFlowAges() {
        return flowAges;
    }

    /**
     *
     * @throws ClassNotFoundException
     * @throws SQLException
     */



    /**
     * implements of the service interface
     * @return
     */
    @Override
    public String getAllActiveFlow() {


        Map<FlowEntryTuple,SwitchPortStatistics> flowStats = FlowStatisticsManager.getUnmodifiableStatistics();
        Map<FlowEntryTuple,FlowRecordAge> flowAges = FlowStatisticsManager.getFlowAges();
        StringBuilder sb = new StringBuilder();
        sb.append("{\"data\":");
        sb.append("[");
        boolean isFirst  =true;
        for(Map.Entry<FlowEntryTuple,SwitchPortStatistics> flowStatsEntry : flowStats.entrySet()){

            if(!isFirst){
                sb.append(",");
            }else{
                isFirst = false ;
            }
            sb.append("{");
            sb.append("\"FlowEntryTuple\":\"");
            sb.append(flowStatsEntry.getKey().toString()).append("\",");
            sb.append("\"SwitchPortStatistics\":\"");
            sb.append(flowStatsEntry.getValue().toString()).append("\",");
            sb.append("\"FlowRecordAge\":\"");
            sb.append(flowAges.get(flowStatsEntry.getKey()).toString()).append("\"");
            sb.append("}");

        }
        sb.append("]}");
        return sb.toString();

    }

    @Override
    public String getRealtimePPS() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"pipps\":");
        sb.append(piCounter.getPPS()).append(",");
        sb.append("\"piops\":").append(piCounter.getOPS()).append(",");
        sb.append("\"pktpps\":").append(pktCounter.getPPS()).append(",");
        sb.append("\"pktops\":").append(pktCounter.getOPS());
        sb.append("}");

        return sb.toString();
    }

    @Override
    public  String getFlowBySrcAddr(IPv6Address srcAddr) {
        return null;
    }


    @Override
    public  String getFlowByTuple(FlowEntryTuple fet) {
        return String.valueOf(flowStats.get(fet));

    }

    /**
     * the impl of the service interface
     * @param update
     */
    @Override
    public synchronized void updateFlowRecord(boolean update) {

        if(update &&!isEnabled) {
            startFlowRecordUpdater();
            isEnabled =true;
        }else if(!update && isEnabled) {
            stopFlowRecordUpdater();
            isEnabled = false;
        }

    }


    /**
     * it cannot be modified
     * @return unmodifiable map of portState
     */
    public static Map<FlowEntryTuple, SwitchPortStatistics> getUnmodifiableStatistics() {
        return Collections.unmodifiableMap(flowStats);
    }

    /**
     *
     * @return piCounter & pktCounter
     */
    public static PiCounter getPiCounter(){
        return piCounter;
    }
    public static PktCounter getPktCounter(){
        return pktCounter;
    }

    /**
     * the portstate contain the statistics info
     * it can be modified
     * @returnt map of portState
     */
    public static Map<FlowEntryTuple, SwitchPortStatistics> getStatistics() {
        return flowStats;
    }

    @Override
    public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
        switch (msg.getType()){
            case PACKET_IN:
                log.info("FLOW_STATISTICS_MODULE:------receive packet_in--------");
                return handlePakcetInMessage(sw,(OFPacketIn) msg, cntx);
            default:
                break;

        }
        return Command.CONTINUE;
    }


    /**
     * 分析报文摘要，解析字段，进行统计,暂未考虑同步
     * @param sw
     * @param msg
     * @param cntx
     * @return Command.CONTINUE
     */
    private Command handlePakcetInMessage(IOFSwitch sw, OFPacketIn msg, FloodlightContext cntx) {
        long cur = System.currentTimeMillis();
        if(msg.getMatch().supports(MatchField.IPV6_SRC) && msg.getMatch().isExact(MatchField.IPV6_SRC)){
            log.info("This Packet_in NOT work in FLOW_STATISTICS_MODULE");
            return Command.CONTINUE;
        }
        for (byte b : msg.getData()) {
            System.out.print(b + " ");
        }
        log.info("This Packet_in CAN work in FLOW_STATISTICS_MODULE");
        System.out.println();
        log.info(Arrays.toString(msg.getData()));
        //byte[] pktSummary = Arrays.copyOf(msg.getData() , FLOW_PKT_SUMMARY_LENGTH);
        //log.info(Arrays.toString(pktSummary));


        /**
         * tianjia flag to mark the packet of mine
         *
         */
        byte[] data = msg.getData();
        int len = data.length;
        log.info("len:" + data.length);
        /**
         * TODO:增加对Pktin报文的统计和pktin报文中正常流量报文的统计
         */

        log.info("piCounter increment");
        piCounter.increment(1,len,System.currentTimeMillis());


        int offset = FLOW_PKTIN_DATA_PADDING;

        while(offset  <len &&  offset+FLOW_PKT_SUMMARY_LENGTH <= len){
            log.info("deserializing......current offset : "+ offset );
            PktSummary summary = new PktSummary();
            try {
                  summary.deserialize(data, offset, FLOW_PKT_SUMMARY_LENGTH);

            } catch (PacketParsingException e) {
                e.printStackTrace();
                log.warn("deserialize error");
                return Command.CONTINUE;
            }
            /**
             *  TODO:pktCounter 增加对携带的报文的信息统计
             *
             */
            log.info("PktCounter increment");
            pktCounter.increment(1,summary.getPayloadLength(),System.currentTimeMillis());

            /**
             * TODO:当前pps对应的抽样比
             */
            PKT_SAMPLING_RATIO = PktSampling.getInstance().getSamplingRate((int) pktCounter.getPPS());
            log.info("当前抽样比: " + PKT_SAMPLING_RATIO);


            FlowEntryTuple tuple = new FlowEntryTuple(summary.getSourceAddress(),summary.getDestinationAddress(),
                    summary.getSourcePort(),summary.getDestinationPort(),summary.getNextHeader(), summary.getTrafficClass(),summary.getInput() );
            log.info("tuple " + tuple.toString());
            //TODO :update the statistics
            Map<FlowEntryTuple,SwitchPortStatistics> flowStats = FlowStatisticsManager.getStatistics();

            if(!flowStats.containsKey(tuple)){
                //TODO:insert the record
                log.info("insert new flow record");
                SwitchPortStatistics curStatistics = new SwitchPortStatistics();
                curStatistics.setFirst(summary.timeStamp);
                synchronized (flowStats){
                    flowStats.put(tuple,curStatistics);
                }
                synchronized (flowAges){
                    flowAges.put(tuple,new FlowRecordAge());
                }

            }
            if((summary.getFlags()&5 )  != 0  ){
                synchronized (flowAges){
                    flowAges.get(tuple).setFin(true);//FIN RST set true;
                    log.info("flowAges  record fin" );
                }
            }

            SwitchPortStatistics curStatistics = flowStats.get(tuple);
            log.info("Update flow record " + tuple.toString() + curStatistics.toString());

            curStatistics.setTcpflags((byte) (curStatistics.getTcpflags() | (byte) summary.getFlags()));//tcpflags?  remove to fragment header
            curStatistics.setDrops(curStatistics.getDrops());//drops?
            curStatistics.setOcts(curStatistics.getOcts()+ summary.getPayloadLength());
            curStatistics.setPkts(curStatistics.getPkts()+1 );
            curStatistics.setLast((long)summary.getTimeStamp());
            synchronized (flowAges){
                flowAges.get(tuple).setLastUpdate(0);

            }
            offset += FLOW_PKT_SUMMARY_LENGTH;
        }
        log.info("报文解析时间开销："+(System.currentTimeMillis() - cur) +"ms");
        return Command.CONTINUE;

    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
        log.info("FLOW STATISTICS GET_MODULE_SERVICE");
        Collection<Class<? extends IFloodlightService>> l =
                new ArrayList<Class<? extends IFloodlightService>>();
        l.add(IFlowStatisticsManagerService.class);
        return l;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
        log.info("FLOW STATISTICS GET_SERVICE_IMPLS");
        Map<Class<? extends IFloodlightService>, IFloodlightService> m =
                new HashMap<>();
        m.put(IFlowStatisticsManagerService.class, this);
        return m;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
        log.info("FLOW STATISTICS GET_MODULE_DEPENDENCIES");
        Collection<Class<? extends IFloodlightService>> l =
                new ArrayList<>();
        l.add(IFloodlightProviderService.class);
        l.add(IRestApiService.class);
        l.add(IThreadPoolService.class);
        log.info("FLOW STATISTICS GET_MODULE_DEPENDENCIES FINISH");
        return l;
    }

    @Override
    public void init(FloodlightModuleContext context) throws FloodlightModuleException {
        floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
        restApi = context.getServiceImpl(IRestApiService.class);
        threadPoolService = context.getServiceImpl(IThreadPoolService.class);
        logger= LoggerFactory.getLogger(FlowStatisticsManager.class);



    }

    @Override
    public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
        floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
        restApi.addRestletRoutable(new FlowStatisticsWebRoutable());
        if(isEnabled){
            startFlowRecordUpdater();
        }
    }

    @Override
    public String getName() {
        return "FLOW_STATISTICS_MODULE";
    }

    @Override
    public Command processPacketInMessage(IOFSwitch sw, OFPacketIn pi, IRoutingDecision decision, FloodlightContext cntx) {
        return null;
    }

    @Override
    public boolean isCallbackOrderingPrereq(OFType type, String name) {
        return false;
    }

    @Override
    public boolean isCallbackOrderingPostreq(OFType type, String name) {
        return false;
    }


    //TODO: 用堆对map进行维护，使得得到最老的流记录，能够更快的调整缓存空间大小。
    public synchronized FlowEntryTuple getOldestFlowRecord() {
        //return flowAges.firstEntry().getKey();
        FlowEntryTuple key = null;
        int maxAge  = 0 ;
        for(Map.Entry<FlowEntryTuple,FlowRecordAge> entry : flowAges.entrySet()){
            if(entry.getValue().getAge()> maxAge){
                maxAge=entry.getValue().getAge();
                key = entry.getKey();
            }
        }
        return key;

    }

    /**
     * start all flow record updater thread
     */
    private void startFlowRecordUpdater(){
        flowRecordUpdater = threadPoolService.getScheduledExecutor().scheduleAtFixedRate(
                new FlowRecordUpdaterRunable(), flowRecordInterval,flowRecordInterval, TimeUnit.SECONDS);
        log.warn("FLOW RECORD UPDATER START ");

    }

    /**
     * stop the thread of FlowRecordUpdater
     */
    private void stopFlowRecordUpdater(){
        if (!flowRecordUpdater.cancel(false)) {
            log.error("Could not cancel port stats thread");
        } else {
            log.warn("FlowRecordUpdater thread(s) stopped");
        }
    }

    /**
     * thread to update the age of the flow record
     */
    private class FlowRecordUpdaterRunable implements Runnable{

        @Override
        public void run() {

                log.info("FLOW RECORD UPDATER RUNNING");

                Map<FlowEntryTuple, FlowRecordAge > flowAges = FlowStatisticsManager.getFlowAges();
                Map<FlowEntryTuple,SwitchPortStatistics> flowStats = FlowStatisticsManager.getStatistics();
                synchronized (flowAges  ){
                    synchronized (flowStats){
                        while(flowAges.size() > FLOW_CACHE_SIZE) {
                            //TODO: condition 2 flow size to large
                            synchronized (flowStats) {
                                //TODO:save to database ;
                                log.info("flow size too large");

                                FlowEntryTuple key = getOldestFlowRecord();

                                //TODO:insert the flow record into db
                                FlowRecord fr = new FlowRecord(key,flowStats.get(key),2,System.currentTimeMillis());
                                FlowRecordDao frdao = new FlowRecordDaoImpl();
                                frdao.insertFlow(fr);

                                log.info("the oldest flow record info:" + flowAges.get(key).toString() + flowStats.get(key).toString());
                                flowStats.remove(key);
                                flowAges.remove(key);

                            }
                        }



                        Iterator<Map.Entry<FlowEntryTuple,FlowRecordAge> > it = flowAges.entrySet().iterator();
                        while(it.hasNext()){
                            Map.Entry<FlowEntryTuple,FlowRecordAge> entry = it.next();
                            if(entry.getValue().isFin()){
                                //TODO: condition 1 fin rst
                                //flowAges.remove(entry.getKey());

                                //TODO: save to database ;
                                log.info("flow record fin");
                                //TODO:insert the flow record into db
                                FlowRecord fr = new FlowRecord(entry.getKey(),flowStats.get(entry.getKey()),1,System.currentTimeMillis());
                                FlowRecordDao frdao = new FlowRecordDaoImpl();
                                frdao.insertFlow(fr);;
                                log.info("flow statistics info: "+ entry.getKey().toString()+entry.getValue().toString() + flowStats.get(entry.getKey()).toString());
                                flowStats.remove(entry.getKey());
                                it.remove();


                            }
                            else if ( entry.getValue().getLastUpdate() > FLOW_RECORD_LASTUPDATE_MAX ){
                                //TODO: codition 3 last update before 15 sec;
                                log.info("flow record is not active ");
                                //TODO:insert the flow record into db
                                FlowRecord fr = new FlowRecord(entry.getKey(),flowStats.get(entry.getKey()),3,System.currentTimeMillis());
                                FlowRecordDao frdao = new FlowRecordDaoImpl();
                                frdao.insertFlow(fr);
                                log.info("flow statistics info: "+ entry.getKey().toString() +entry.getValue().toString()+ flowStats.get(entry.getKey()).toString());
                                flowStats.remove(entry.getKey());
                                it.remove();
                            }
                            else if( entry.getValue().getAge() >FLOW_RECORD_AGE_MAX){
                                //TODO: conditioon 4 last over 30 min ;
                                log.info("flow record last over "+FLOW_RECORD_AGE_MAX/60 +" min");

                                //TODO:insert the flow record into db
                                FlowRecord fr = new FlowRecord(entry.getKey(),flowStats.get(entry.getKey()),4,System.currentTimeMillis());
                                FlowRecordDao frdao = new FlowRecordDaoImpl();
                                frdao.insertFlow(fr);

                                log.info("flow statistics info: "+ entry.getKey().toString() +entry.getValue().toString()+ flowStats.get(entry.getKey()).toString());
                                flowStats.remove(entry.getKey());
                                it.remove();

                            }
                            else {
                                //TODO :update the age;
                                entry.getValue().lastUpdateIncrement();;
                                entry.getValue().ageIncrement();

                                log.info("flow record get older");
                                log.info("flow statistics info: "+ entry.getKey().toString()+entry.getValue().toString() + flowStats.get(entry.getKey()).toString());

                            }

                        }
                    }

                }

                /*
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                */

        }
    }
}
