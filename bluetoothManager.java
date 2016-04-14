package com.experiment.chickenjohn.materialdemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 *   Below is the copyright information.
 *
 *    Copyright (C) 2016 chickenjohn
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   You may contact the author by email:
 *   chickenjohn93@outlook.com
 * Created by chickenjohn on 2016/3/12.
 */

public class bluetoothManager {
    private boolean CONNECT_STATE = false;
    private static BluetoothAdapter myBtAdapter = BluetoothAdapter.getDefaultAdapter();
    private static BluetoothDevice myBtDevice;
    private clientThread myBtClientThread;
    private BluetoothSocket myBtSocket;
    public String btAddress;
    public bluetoothReceiver btReceiver = new bluetoothReceiver();
    private android.os.Handler uiRefreshHandler;
    private int receiveDataCounter = 0;

    public bluetoothManager(android.os.Handler handler) {
        uiRefreshHandler = handler;
    }

    public void enableBluetooth() {
        if (!myBtAdapter.isEnabled()) {
            myBtAdapter.enable();
        }
        myBtAdapter.startDiscovery();
    }

    public void disableBluetooth() {
        if (myBtAdapter.isEnabled()) {
            myBtAdapter.disable();
        }
    }

    public class bluetoothReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String targetName = "hc-bluetooth";
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice currentDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (currentDevice.getName().equalsIgnoreCase(targetName)) {
                    btAddress = currentDevice.getAddress();
                    myBtDevice = myBtAdapter.getRemoteDevice(btAddress);
                    Toast.makeText(context, "找到设备:" + myBtDevice.getName(), Toast.LENGTH_LONG).show();
                    CONNECT_STATE = true;
                    myBtClientThread = new clientThread();
                    myBtClientThread.start();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (!isConnected()) {
                    Toast.makeText(context, "搜索结束，没有找到设备", Toast.LENGTH_LONG).show();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Toast.makeText(context, "搜索开始", Toast.LENGTH_LONG).show();
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                CONNECT_STATE = false;
                Toast.makeText(context, "连接断开，正在重试连接", Toast.LENGTH_LONG).show();
                Message uiRefreshMessage = Message.obtain();
                uiRefreshMessage.what = 1;
                uiRefreshHandler.sendMessage(uiRefreshMessage);

            }
        }
    }

    private class clientThread extends Thread {
        public void run() {
            try {
                myBtAdapter.cancelDiscovery();
                myBtSocket = myBtDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                myBtSocket.connect();
                new connectThread().start();
                Message uiRefreshMessage = Message.obtain();
                uiRefreshMessage.what = 0;
                uiRefreshHandler.sendMessage(uiRefreshMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class connectThread extends Thread {
        public void run() {
            receiveDataCounter = 0;
            InputStream mmInStream = null;
            try {
                mmInStream = myBtSocket.getInputStream();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            while (true) {
                try {
                    if ((mmInStream.available()) >= 2) {
                        //Log.v("data in stream",Integer.toString(bytes));
                        byte[] buf_data = new byte[2];
                        mmInStream.read(buf_data);
                        handleBtData(buf_data);
                    }
                } catch (IOException e) {
                    try {
                        mmInStream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    //Handle received data here
    public void handleBtData(byte[] data) {
        int dataInInt;
        if (0x0 == (0x80 & data[1])) {
            dataInInt = ((0xff & ((int) data[1])) << 8) | (0xff & (int) data[0]);
        } else {
            dataInInt = ((0xff & (~data[1])) << 8) | (0xff & ((~data[0]) + 1));
            dataInInt = -dataInInt;
        }
        Message uiRefreshMessage = Message.obtain();
        uiRefreshMessage.what = 2;
        uiRefreshMessage.arg1 = dataInInt;
        uiRefreshMessage.arg2 = receiveDataCounter;
        uiRefreshHandler.sendMessage(uiRefreshMessage);
        receiveDataCounter += 1;
    }

    //Registration of Broadcast Receiver
    public IntentFilter regBtReceiver() {
        IntentFilter bluetoothBroadcastFilter = new IntentFilter();
        bluetoothBroadcastFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        bluetoothBroadcastFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        bluetoothBroadcastFilter.addAction(BluetoothDevice.ACTION_FOUND);
        bluetoothBroadcastFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        return bluetoothBroadcastFilter;
    }

    public boolean isConnected() {
        return CONNECT_STATE;
    }
}
