package com.experiment.chickenjohn.materialdemo;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

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
public class EcgDataAnalyzer {

    private Handler uiRefreshHandler;
    public double beatRate = 0;
    private double lastAvgValue = 0;
    DataTemp[] peakDetectionDataTemp = new DataTemp[80];
    private boolean ifFirstRpeakDetected = false;
    private DataTemp rpeakLocalMax = new DataTemp(0, 0);
    private DataTemp rpeakMaxTemp = new DataTemp(0, 0);
    private DataTemp rpeak = new DataTemp(0, 0);
    private DataTemp lastRpeak = new DataTemp(0, 0);

    private class DataTemp {
        private double data;
        private int dataId;

        public DataTemp(int data, int dataId) {
            this.data = (double) data;
            this.dataId = dataId;
        }

        public DataTemp() {

        }

        public synchronized double getData() {
            return data;
        }

        public int getDataId() {
            return dataId;
        }

        public synchronized void setData(double data) {
            this.data = data;
        }
    }

    private class BeatRateAndRpeakDetectionThread extends Thread {

        DataTemp[] dataTemp = new DataTemp[80];

        public BeatRateAndRpeakDetectionThread() {
            int cnt;
            for (cnt = 0; cnt < 80; cnt++) {
                dataTemp[cnt] = peakDetectionDataTemp[cnt];
            }
        }

        @Override
        public void run() {
            super.run();
            int cnt;
            double dataAvgValue = 0;
            for (cnt = 5; cnt < 76; cnt++) {
                dataTemp[cnt].setData(dataTemp[cnt - 5].getData() +
                        dataTemp[cnt - 4].getData() + dataTemp[cnt - 3].getData() +
                        dataTemp[cnt - 2].getData() + dataTemp[cnt - 1].getData() +
                        dataTemp[cnt].getData() + dataTemp[cnt + 1].getData() +
                        dataTemp[cnt + 2].getData() + dataTemp[cnt + 3].getData() +
                        dataTemp[cnt + 4].getData());
                dataTemp[cnt].setData(dataTemp[cnt].getData() / 10);
            }
            rpeakLocalMax = dataTemp[0];
            for (cnt = 0; cnt < 80; cnt++) {
                dataAvgValue += dataTemp[cnt].getData();
                if (rpeakLocalMax.getData() < dataTemp[cnt].getData()) {
                    rpeakLocalMax = dataTemp[cnt];
                }
            }
            dataAvgValue = dataAvgValue / 80;

            if (ifFirstRpeakDetected) {
                if ((Math.abs((rpeakMaxTemp.getData() - lastAvgValue) /
                        (rpeakLocalMax.getData() - dataAvgValue) - 1) < 0.5)
                        &&
                        ((rpeakLocalMax.getDataId() - rpeakMaxTemp.getDataId()) > 60)) {
                    rpeakMaxTemp = rpeakLocalMax;
                    lastAvgValue = dataAvgValue;
                    Log.v("hit", Double.toString(rpeakMaxTemp.getData()));
                    rpeaksHandling(rpeakMaxTemp);
                }
            } else {
                if (rpeakMaxTemp.getData() < rpeakLocalMax.getData()) {
                    rpeakMaxTemp = rpeakLocalMax;
                    lastAvgValue = dataAvgValue;
                }
                if ((dataTemp[0].getDataId() > 200) && ((rpeakMaxTemp.getData() / Math.abs(dataAvgValue)) > 1.1)) {
                    Log.v("get first", Double.toString(rpeakMaxTemp.getData()));
                    ifFirstRpeakDetected = true;
                    lastRpeak = rpeakMaxTemp;
                }
            }
        }
    }

    public EcgDataAnalyzer(Handler handler) {
        uiRefreshHandler = handler;
    }

    public void beatRateAndRpeakDetection(EcgData ecgData) {

        int numberInTemp = (ecgData.getDataId() + 1) % 80;
        if (numberInTemp == 0) {
            DataTemp dataTemp = new DataTemp(ecgData.getValueInInt(), ecgData.getDataId());
            peakDetectionDataTemp[79] = dataTemp;
            BeatRateAndRpeakDetectionThread beatRateAndRpeakDetectionThread
                    = new BeatRateAndRpeakDetectionThread();
            beatRateAndRpeakDetectionThread.start();
        } else {
            DataTemp dataTemp = new DataTemp(ecgData.getValueInInt(), ecgData.getDataId());
            peakDetectionDataTemp[numberInTemp - 1] = dataTemp;
        }
    }

    public void rpeaksHandling(DataTemp recentRpeak) {
        if (ifFirstRpeakDetected) {
            double RRinterval = (recentRpeak.getDataId() - lastRpeak.getDataId()) * EcgData.getRECORDRATE();
            beatRate = 60 / RRinterval;
            lastRpeak = recentRpeak;
            Message uiRefreshMessage = Message.obtain();
            uiRefreshMessage.what = 3;
            uiRefreshMessage.arg1 = (int) beatRate;
            uiRefreshMessage.arg2 = (int) (RRinterval*100);
            uiRefreshHandler.sendMessage(uiRefreshMessage);
        } else {
            lastRpeak = recentRpeak;
        }
    }
}
