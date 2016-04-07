package com.experiment.chickenjohn.materialdemo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;

/**
 * Created by chickenjohn on 2016/3/11.
 */
public class EcgDatabaseManager {
    private HealthDatabaseHelper healthDatabaseHelper;
    private SQLiteDatabase ecgDatabase;
    private EcgData[] ecgDataTemp = new EcgData[500];

    public EcgDatabaseManager(Context context) {
        healthDatabaseHelper = new HealthDatabaseHelper(context);
        ecgDatabase = healthDatabaseHelper.getWritableDatabase();
    }

    public void addRecord(EcgData ecgData) {
        int dataId;
        dataId = ecgData.getDataId();
        if ((dataId % 500 != 0) || (dataId == 0)) {
            ecgDataTemp[dataId % 500] = ecgData;
        } else {
            dataInsertingThread dataInsertingThread = new dataInsertingThread(ecgData);
            dataInsertingThread.start();
            Log.v("database inserting", "inserting " + Integer.toString(ecgData.getDataId()));
        }
    }

    public boolean outputRecord() {
        Cursor cursor = ecgDatabase.query("ecg", null, null, null, null, null, null);
        int databaseLength = cursor.getCount();
        if (databaseLength != 0) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                DataOutputThread dataOutputThread = new DataOutputThread(cursor, databaseLength);
                dataOutputThread.start();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean clearRecord() {
        ecgDatabase.execSQL("DELETE FROM ecg");
        Cursor cursor = ecgDatabase.query("ecg", null, null, null, null, null, null);
        if (cursor.getCount() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public void closeEcgDatabase() {
        ecgDatabase.close();
    }

    private class dataInsertingThread extends Thread {
        EcgData newEcgData;

        public dataInsertingThread(EcgData ecgData) {
            newEcgData = ecgData;
        }

        @Override
        public void run() {
            ecgDatabase.beginTransaction();
            try {
                for (int cnt = 0; cnt < 500; cnt++) {
                    ecgDatabase.execSQL("INSERT INTO ecg VALUES(null, ?, ?)", new Object[]{ecgDataTemp[cnt].getValue(), ecgDataTemp[cnt].getRecordTime()});
                }
                ecgDatabase.setTransactionSuccessful();
            } finally {
                ecgDatabase.endTransaction();
                ecgDataTemp[0] = newEcgData;
            }
        }
    }

    //why this thread blocks the main thread?
    private class DataOutputThread extends Thread {
        Cursor cursor;
        int databaseLength;

        public DataOutputThread(Cursor cursor, int databaseLength) {
            this.cursor = cursor;
            this.databaseLength = databaseLength;
        }

        @Override
        public void run() {
            super.run();
            try {
                File dataOutputFile = new File(Environment.getExternalStorageDirectory(), "/ecg.txt");
                if (!dataOutputFile.exists()) {
                    dataOutputFile.createNewFile();
                }
                FileWriter fileWriter = new FileWriter(dataOutputFile, false);
                BufferedWriter bufferedFileWriter = new BufferedWriter(fileWriter);
                if (cursor.moveToFirst()) {
                    while (cursor.moveToNext()) {
                        int ID = cursor.getInt(0);
                        int value = cursor.getInt(1);
                        double dataTime = cursor.getShort(2);
                        fileWriter.write(Integer.toString(ID) + "\t" +
                                new DecimalFormat("0.000").format(dataTime) + "\t" +
                                Integer.toString(value) + "\r\n");
                        bufferedFileWriter.flush();
                    }
                }
                bufferedFileWriter.close();
                fileWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
    }
}
