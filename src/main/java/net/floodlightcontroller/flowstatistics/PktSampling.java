package net.floodlightcontroller.flowstatistics;

import net.floodlightcontroller.core.IListener;
import net.floodlightcontroller.packet.PacketParsingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by zhensheng on 2016/7/26.
 */
public class PktSampling {

    /**
     * 用于抽样的报文解析
     */
    private static final int FLOW_CACHE_SIZE = 1024;
    private static final int FLOW_RECORD_LASTUPDATE_MAX =15;
    //private static final int FLOW_RECORD_AGE_MAX = 1800;//30min
    private static final int FLOW_RECORD_AGE_MAX = 60;
    private static final int FLOW_PKT_SUMMARY_LENGTH = 68;
    private static final int FLOW_PKTIN_DATA_PADDING = 16;
    //TODO: 流量自适应抽样模块，需要根据网络中流量调节抽样比，使得网络中吞吐量占据一定的比。
    private static int PKT_SAMPLING_RATIO = 1; //抽样比，根据网络中流量大小自适应调节。

    protected static final Logger log = LoggerFactory.getLogger(PktSampling.class);

    /**
     * 用于实时报文流量测量
     */
    public static int PPS_CPU = 10000;
    public static int K = 10;
    public static int D =10 ;
    //int ratec;
    int ratecl=1 ;//上次计算得到的抽样比
    int ratel =1 ;//当前正在采用的抽样比
    int ltrend;
    long ltime ;




    /*
            int k ;
            int d ;
            int cTrend;
            */
    //int lTrend;
    //int ppsCur;
    private volatile static PktSampling pktSampling = new PktSampling();

    private PktSampling(){
        int ratecl=1 ;
        int ratel =1  ;
        ltime= System.currentTimeMillis();
        this.isChanged= false ;
        this.curRatio =1;
        this.curCount =0 ;


    }
    public static PktSampling getInstance(){
        return pktSampling;
    }

    /*
    private int calCTrend(int ppsCur){
        int ctrend;
        long curtime = System.currentTimeMillis();
        int ratec = Math.max(ppsCur/ PPS_CPU , 1 ) ;
        int d = ratec - ratel ;
        int k = (int) ((ratec - ratecl) / (curtime - ltime));

        if(Math.abs(d)>D  || Math.abs(k) > k  ){
            if(d>0 ){
                ctrend = 1;
            }else {
                ctrend =-1 ;
            }
        }else {
            ctrend = 0 ;
        }
        System.out.println("ctrend : " + ctrend);
        return ctrend;
    }*/

    public int getSamplingRate(int ppsCur){
        int ctrend ;
        long curtime = System.currentTimeMillis();
        int ratec = Math.max(ppsCur/ PPS_CPU , 1 ) ;//计算得到的抽样比
        int d = ratec - ratel ;

        if(curtime== ltime ) return ratel ;
        int k = (int) ((ratec - ratecl) * 1000 / (curtime - ltime));

        if(Math.abs(d)>D  || Math.abs(k) > k  ){
            if(d>0 ){
                ctrend = 1;
            }else {
                ctrend =-1 ;
            }
        }else {
            ctrend = 0 ;
        }
        //TODO: 记录上次PPS 计算的ratecl
        ratecl = ratec ;
        ltime = curtime;
        System.out.println("ctrend : " + ctrend);
        //TODO:当趋势一致的时候采用这次的抽样比
        if(ctrend* ltrend > 0  ){
            ratel = ratec;

            //调整抽样比
            isChanged= true;
        }
        if(ctrend != 0 ){
            ltrend = ctrend;
        }
        System.out.println("ratel :" + ratel);
        return ratel;




    }

    private static final PktCounter pktCounter = new PktCounter();
    private static final PiCounter piCounter = new PiCounter();
    int curRatio;//当前正在抽样中的抽样比，一旦这个抽样比完成抽样，则替换为ratel

    int curCount;
    boolean isChanged;
    int sampleIndex ;

    public void curCountIncrement(){
        this.curCount++;
    }

    public int getSampleIndex() {
        return sampleIndex;
    }

    public void setSampleIndex(int sampleIndex) {
        this.sampleIndex = sampleIndex;
    }
    public void setSampleIndex() {
        this.sampleIndex =(int)( Math.random()*curRatio);
    }
    public int getCurRatio() {
        return curRatio;
    }
    public void setCurRatio(int curRatio) {
        this.curRatio = curRatio;
    }
    public void setCurRatio() {
        if(isChanged){
            this.curRatio= this.ratel;
        }

    }

    public int getCurCount() {
        return curCount;
    }

    public void setCurCount(int curCount) {
        this.curCount = curCount;
    }


    public void handleData(byte[] data){}



}
