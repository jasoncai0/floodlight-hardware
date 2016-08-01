package net.floodlightcontroller.flowstatistics;

/**
 * Created by zhensheng on 2016/7/26.
 */
public class PktSampling {
    public static int PPS_CPU = 10000;
    public static int K = 10;
    public static int D =10 ;
    //int ratec;
    int ratecl=1 ;
    int ratel =1  ;
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
        int ratec = Math.max(ppsCur/ PPS_CPU , 1 ) ;
        int d = ratec - ratel ;

        if(curtime== ltime ) return 0 ;
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
        }
        if(ctrend != 0 ){
            ltrend = ctrend;
        }
        System.out.println("ratel :" + ratel);
        return ratel;
    }

}
