package com.experiment.chickenjohn.materialdemo;

import java.math.BigDecimal;
import java.util.Date;
import java.text.SimpleDateFormat;

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
 */

public class EcgData extends MeasuredData {
    private double recordTime = 0.000;
    private int dataId;
    private static double RECORDRATE = 1.0/500.0;

    //To insert current time automatically
    //the record time only changes when a second passed, why?
    public EcgData(int data, int dataId) {
        super("ECG", data);
        this.dataId = dataId;
        recordTime = (double) dataId * RECORDRATE;
    }

    public double getRecordTime() {
        return recordTime;
    }

    public int getDataId() {
        return dataId;
    }

    public static void setRecordRate (double herz){
        RECORDRATE = 1.0/herz;
    }

    public static double getRECORDRATE (){
        return RECORDRATE;
    }
}
