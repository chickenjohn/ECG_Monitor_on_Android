package com.experiment.chickenjohn.materialdemo;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Created by chickenjohn on 2016/3/11.
 */
public class EcgData extends MeasuredData{
    private int recordTime;

    //To receive time from device:
    public EcgData(int data,int recordTime){
        super("ECG",data);
        this.recordTime = recordTime;
    }

    //To insert current time automatically
    public EcgData(int data){
        super("ECG",data);
        SimpleDateFormat timeFormat = new SimpleDateFormat("ss");
        Date currentTime = new Date(System.currentTimeMillis());
        recordTime = Integer.parseInt(timeFormat.format(currentTime));
    }

    public int getRecordTime(){
        return recordTime;
    }
}
