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
 * Below is the copyright information.
 * <p/>
 * Copyright (C) 2016 chickenjohn
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * You may contact the author by email:
 * chickenjohn93@outlook.com
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

    public boolean outputRecord(boolean startOutput) {
        Cursor cursor = ecgDatabase.query("ecg", null, null, null, null, null, null);
        int databaseLength = cursor.getCount();
        if (databaseLength != 0) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && startOutput) {
                DataOutputThread dataOutputThread = new DataOutputThread(cursor, databaseLength);
                dataOutputThread.start();
                return true;
            } else {
                cursor.close();
                return false;
            }
        } else {
            cursor.close();
            return false;
        }
    }

    public boolean clearRecord() {
        ecgDatabase.execSQL("DELETE FROM ecg");
        Cursor cursor = ecgDatabase.query("ecg", null, null, null, null, null, null);
        if (cursor.getCount() == 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
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
            super.run();
            ecgDatabase.beginTransaction();
            try {
                for (int cnt = 0; cnt < 500; cnt++) {
                    ecgDatabase.execSQL("INSERT INTO ecg VALUES(null, ?, ?)",
                            new Object[]{ecgDataTemp[cnt].getValueInString(),
                                    ecgDataTemp[cnt].getRecordTime()});
                }
                ecgDatabase.setTransactionSuccessful();
            } finally {
                ecgDatabase.endTransaction();
                ecgDataTemp[0] = newEcgData;
            }
        }
    }

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
