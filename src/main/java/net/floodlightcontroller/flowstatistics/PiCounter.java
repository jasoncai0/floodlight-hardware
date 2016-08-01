package net.floodlightcontroller.flowstatistics;


import java.util.Date;



/**
 * packetin报文计数器，统计接受的packetin报文数目，报文摘要的大小，对应的时间戳
 * 该统计基于报文级别
 * Created by zhensheng on 2016/7/19.
 */
public class PiCounter {
    public static long DefaultInterval = 5000 ;//1000ms;
    public static long DefaultSumPkts = 8;
    public static long DefaultSumOcts= 800;
    public static long DefaultCurTime ;
    //public static   DefaultTimeInterval = 1000 ;


    long totalPkts;
    long totalOcts;
    long lastPPSTime;
    long lastOPSTime;
    long curTime;

    public long getSumOcts() {
        return sumOcts;
    }

    public long getTotalPkts() {
        return totalPkts;
    }

    public long getTotalOcts() {
        return totalOcts;
    }

    public long getLastPPSTime() {
        return lastPPSTime;
    }

    public long getLastOPSTime() {
        return lastOPSTime;
    }

    public long getCurTime() {
        return curTime;
    }

    public long getPPS() {
        return PPS;
    }

    public long getOPS() {
        return OPS;
    }

    public long getRecentPkts() {
        return recentPkts;
    }

    public long getRecentOcts() {
        return recentOcts;
    }

    public long getSumPkts() {
        return sumPkts;
    }

    //long lastTime;
    //long interval ;
    long PPS;
    long OPS;
    long recentPkts;
    long recentOcts;
    long sumPkts;
    long sumOcts;


    /*
        public void totalPktsIncrement(){
            this.totalPkts ++;
        }*/
    static{
        DefaultCurTime= System.currentTimeMillis();
    }

    public PiCounter(){
        this(DefaultSumOcts,0,DefaultCurTime,DefaultCurTime, DefaultCurTime,0,0,0,0,DefaultSumPkts,0 );

    }

    public PiCounter(long sumOcts, long totalOcts, long lastPPSTime, long lastOPSTime, long curTime, long PPS, long OPS, long recentPkts, long recentOcts, long sumPkts, long totalPkts) {
        this.sumOcts = sumOcts;
        this.totalOcts = totalOcts;
        this.lastPPSTime = lastPPSTime;
        this.lastOPSTime = lastOPSTime;
        this.curTime = curTime;
        this.PPS = PPS;
        this.OPS = OPS;
        this.recentPkts = recentPkts;
        this.recentOcts = recentOcts;
        this.sumPkts = sumPkts;
        this.totalPkts = totalPkts;
    }

    @Override
    public String toString() {
        return "PiCounter{" +
                "totalPkts=" + totalPkts +
                ", totalOcts=" + totalOcts +
                ", lastPPSTime=" + new Date(lastPPSTime).toString() +' '+ lastPPSTime +
                ", lastOPSTime=" + new Date(lastOPSTime).toString() +' ' + lastOPSTime +
                ", curTime=" + new Date(curTime).toString()+ ' ' + curTime+
                ", PPS=" + PPS +
                ", OPS=" + OPS +
                ", recentPkts=" + recentPkts +
                ", recentOcts=" + recentOcts +
                ", sumPkts=" + sumPkts +
                ", sumOcts=" + sumOcts +
                '}';
    }

    public void totalIncrement(long pkts , long octs){
        this.totalPkts += pkts;
        this.totalOcts += octs;


    }

    public void recentIncrement(long pkts, long octs){
        this.recentOcts += octs;
        this.recentPkts += pkts;
    }


    public void increment(long pkts, long octs ,long curTime){
        System.out.println(this.toString());

        totalIncrement(pkts,octs);
        recentIncrement(pkts,octs);

        if(recentPkts >=  sumPkts){
            long curPPS = calPPS(curTime);
            System.out.println("当前每秒Pkt_in报文数:"+curPPS);
        }
        if(recentOcts >= sumOcts){
            long curOPS = calOPS(curTime);
            System.out.println("当前每秒Pkt_in字节数："+curOPS);
        }
        this.curTime = curTime;

        System.out.println(this.toString());

    }
    public void increment(long pkts, long octs ){
        long curTime = System.currentTimeMillis();
        increment(pkts,octs,curTime);
    }

    public long  calPPS (long curTime){

        long interval = curTime - lastPPSTime;
        System.out.println("current interval :"+ interval  );
        if(interval != 0 ){
            PPS = recentPkts * 1000 / interval;
            System.out.println("current Pkt_in PPS :"+ PPS  );
            System.out.println("recent Pkt_in Pkts :"+ PPS  );
            System.out.println("lastTime :"+ new Date(lastPPSTime).toString());
            System.out.println("curTime :"+ new Date(curTime).toString());
            sumPkts = PPS * DefaultInterval / 1000 ;
            recentPkts = 0 ;
            lastPPSTime = curTime ;
        }else{
            System.out.println("interval = 0 , cal PPS error!");
        }
        return PPS;
    }

    public long calOPS(long curTime){
        long interval = curTime - lastOPSTime;
        if(interval !=0 ) {
            OPS = recentOcts * 1000 / interval;
            sumOcts = OPS * DefaultInterval / 1000;
            recentOcts = 0;
            lastOPSTime = curTime;
        }
        else {
            System.out.println("interval = 0 , cal OPS error!");
        }
        return OPS;
    }



}
