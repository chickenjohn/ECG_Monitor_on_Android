package com.experiment.chickenjohn.materialdemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by chickenjohn on 2016/3/11.
 */
public class EcgDatabaseManager {
    private HealthDatabaseHelper healthDatabaseHelper;
    private SQLiteDatabase ecgDatabase;

    public EcgDatabaseManager(Context context){
        healthDatabaseHelper = new HealthDatabaseHelper(context);
        ecgDatabase = healthDatabaseHelper.getWritableDatabase();
    }

    public void addRecord(EcgData ecgData){
        ecgDatabase.beginTransaction();
        try{
            ecgDatabase.execSQL("INSERT INTO ecg VALUES(null, ?, ?)", new Object[]{ecgData.getValue(),ecgData.getRecordTime()});
            ecgDatabase.setTransactionSuccessful();
        }finally {
            ecgDatabase.endTransaction();
        }
    }

    public void closeEcgDatabase(){
        ecgDatabase.close();
    }


}
