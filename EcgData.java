package com.experiment.chickenjohn.materialdemo;

import java.math.BigDecimal;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Created by chickenjohn on 2016/3/11.
 */
public class EcgData extends MeasuredData{
    private double recordTime=0.000;
    private int dataId;
    private final double RECORDRATE=1.000/500.000;

    //To insert current time automatically
    //the record time only changes when a second passed, why?
    public EcgData(int data, int dataId){
        super("ECG",data);
        this.dataId = dataId;
        recordTime = (double)dataId * RECORDRATE;
    }

    public double getRecordTime(){
        return recordTime;
    }

    public int getDataId(){
        return dataId;
    }
}
