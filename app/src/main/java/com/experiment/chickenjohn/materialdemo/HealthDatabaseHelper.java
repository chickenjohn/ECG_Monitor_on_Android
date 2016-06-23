package com.experiment.chickenjohn.materialdemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
 * <p/>
 * Created by chickenjohn on 2016/3/11.
 * <p/>
 * This class is used by management of the database including inserting, creating and other basic
 * handling to the database.
 **/


/* This class is used to define,
 * initialize, and upgrade the
 * database of the data. When using
 * EcgDatabaseManager, this class is
 * employed to create or open a
 * database.
 */
public class HealthDatabaseHelper extends SQLiteOpenHelper {
    //set database name
    private static final String DATABASE_NAME = "ECG.db";

    public HealthDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    //the db.execSQL method can execute a SQLite
    //command. Create the database using this
    //method.
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS ecg" + "(_id INTEGER PRIMARY KEY " +
                "AUTOINCREMENT, value INTEGER, time REAL)");
        //empty the ecg table
        db.execSQL("DELETE FROM ecg");
    }

    //when the database is opened, this
    //method will be called.
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    //when the version of database is
    //changed, this method will be called.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
