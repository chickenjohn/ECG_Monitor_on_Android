package com.experiment.chickenjohn.materialdemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by chickenjohn on 2016/3/11.
 */
public class EcgDatabaseManager {
    private HealthDatabaseHelper healthDatabaseHelper;
    private SQLiteDatabase ecgDatabase;
    private EcgData[] ecgDataTemp = new EcgData[500];

    public EcgDatabaseManager(Context context){
        healthDatabaseHelper = new HealthDatabaseHelper(context);
        ecgDatabase = healthDatabaseHelper.getWritableDatabase();
    }

    public void addRecord(EcgData ecgData){
        int dataId;
        dataId = ecgData.getDataId();
        if ( (dataId%500 != 0) || (dataId == 0) ){
            ecgDataTemp[dataId%500] = ecgData;
        }else{
            dataInsertingThread dataInsertingThread = new dataInsertingThread();
            dataInsertingThread.run(ecgData);
            Log.v("database inserting","inserting "+Integer.toString(ecgData.getDataId()));
        }
    }

    public void closeEcgDatabase(){
        ecgDatabase.close();
    }

    private class dataInsertingThread extends Thread{
        public void run(EcgData newEcgData){
            ecgDatabase.beginTransaction();
            try{
                for(int cnt=0;cnt<500;cnt++) {
                    ecgDatabase.execSQL("INSERT INTO ecg VALUES(null, ?, ?)", new Object[]{ecgDataTemp[cnt].getValue(), ecgDataTemp[cnt].getRecordTime()});
                }
                ecgDatabase.setTransactionSuccessful();
            }finally {
                ecgDatabase.endTransaction();
                ecgDataTemp[0] = newEcgData;
            }
        }
    }
}
