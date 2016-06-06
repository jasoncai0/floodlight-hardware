package net.floodlightcontroller.flowstatistics;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by zhensheng on 2016/5/25.
 */
public class FlowRecordAge implements Comparable<FlowRecordAge> {
    private int age;
    private boolean fin;
    private int lastUpdate;
    public int getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(int lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    public void lastUpdateIncrement(){
        this.lastUpdate++;
    }
    public void ageIncrement(){
        this.age++;
    }


    public boolean isFin() {
        return fin;
    }

    public void setFin(boolean fin) {
        this.fin = fin;
    }

    public FlowRecordAge() {
        this(0,false,0 );

    }
    public FlowRecordAge(int age, boolean fin , int lastUpdate) {
        this.age = age;
        this.fin= fin ;
        this.lastUpdate =lastUpdate;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public int compareTo(FlowRecordAge o) {
        return o.age- this.age;
    }

    @Override
    public String toString() {
        return "FlowRecordAge{" +
                "age=" + age +
                ", fin=" + fin +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
