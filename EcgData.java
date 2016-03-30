package com.experiment.chickenjohn.materialdemo;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Created by chickenjohn on 2016/3/11.
 */
public class EcgData extends MeasuredData{
    private int recordTime;
    private int dataId;
    private final int RECORDRATE=1/500;

    //To insert current time automatically
    public EcgData(int data, int dataId){
        super("ECG",data);
        this.dataId = dataId;
        recordTime = dataId * RECORDRATE;
    }

    public int getRecordTime(){
        return recordTime;
    }

    public int getDataId(){
        return dataId;
    }
}
