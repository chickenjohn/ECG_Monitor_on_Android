package com.experiment.chickenjohn.materialdemo;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.SurfaceView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final int PORT = 0;
    private final int LAND = 1;
    private int PORTORLAND = 0;

    public android.os.Handler uiRefreshHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        naviHeaderSet(btManager.isConnected(), 0);
                    }
                    break;
                case 1:
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        naviHeaderSet(btManager.isConnected(), 0);
                    }
                    btManager.enableBluetooth();
                    break;
                case 2:
                    drawSurfaceView.drawPoint(msg.arg2, msg.arg1);
                    ecgDatabaseManager.addRecord(new EcgData(msg.arg1, msg.arg2));
                    ecgDataAnalyzer.beatRateAndRpeakDetection(new EcgData(msg.arg1, msg.arg2));
                    break;
                case 3:
                    refreshList(0, msg.arg1);
                    refreshList(1, msg.arg2);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private bluetoothManager btManager = new bluetoothManager(uiRefreshHandler);
    private EcgDatabaseManager ecgDatabaseManager;
    private EcgDataAnalyzer ecgDataAnalyzer = new EcgDataAnalyzer(uiRefreshHandler);
    private DrawSurfaceView drawSurfaceView = new DrawSurfaceView();

    private TextView beatRateText;
    private TextView rpeakValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            portLoading();
        } else {
            landLoading();
        }
        ecgDatabaseManager = new EcgDatabaseManager(this);
    }

    private void portLoading() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.portToolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawSurfaceView.setSurfaceViewPort((SurfaceView) findViewById(R.id.surfaceView), PORT);

        beatRateText = (TextView) findViewById(R.id.beat_rate);
        rpeakValue = (TextView) findViewById(R.id.RR_interval);
        PORTORLAND = PORT;
    }

    private void landLoading() {
        setContentView(R.layout.activity_main_land);

        drawSurfaceView.setSurfaceViewLand(
                (SurfaceView) findViewById(R.id.surfaceView_land),
                (SurfaceView) findViewById(R.id.surfaceView_land_tags),
                LAND);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        PORTORLAND = LAND;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int currentOrientation = this.getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            portLoading();
            Message uiRefreshMessage = Message.obtain();
            uiRefreshMessage.what = 0;
            uiRefreshHandler.sendMessage(uiRefreshMessage);
        } else if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            landLoading();
        }
        drawSurfaceView.resetSurfaceViewX();
    }

    @Override
    protected void onResume() {
        this.registerReceiver(btManager.btReceiver, btManager.regBtReceiver());
        btManager.enableBluetooth();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        btManager.disableBluetooth();
        unregisterReceiver(btManager.btReceiver);
        ecgDatabaseManager.closeEcgDatabase();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.wave_output:
                break;
            case R.id.data_output:
                if (ecgDatabaseManager.outputRecord()) {
                    Toast.makeText(this, "导出数据成功", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "没有可以导出的数据！", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.data_clear:
                if (ecgDatabaseManager.clearRecord()) {
                    Toast.makeText(this, "清除数据成功", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "清除数据失败", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.data_send:
                break;
            case R.id.btconnection:
                btManager.enableBluetooth();
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean naviHeaderSet(boolean isConnected, int transNumber) {
        TextView btAddressTextView = (TextView) this.findViewById(R.id.bt_address_textview);
        TextView connectTextView = (TextView) this.findViewById(R.id.connecttextview);
        TextView transNumberTextView = (TextView) this.findViewById(R.id.trans_number_textview);
        if (btAddressTextView != null) {
            if (isConnected) {
                btAddressTextView.setText("蓝牙设备地址：" + btManager.btAddress);
                connectTextView.setText("设备已连接");
                transNumberTextView.setText("接收数据：" + Integer.toString(transNumber));
            } else {
                btAddressTextView.setText("蓝牙设备地址：");
                connectTextView.setText("设备未连接");
                transNumberTextView.setText("接收数据：");
            }
            return true;
        } else {
            return false;
        }
    }

    private void refreshList(int listId, int refreshedData) {
        if (PORTORLAND == PORT) {
            switch (listId) {
                case 0:
                    beatRateText.setText("心率：" + Integer.toString(refreshedData));
                    break;
                case 1:
                    rpeakValue.setText("RR间期：" + Double.toString(((double)refreshedData)/100) + "s");
                    break;
                default:
                    break;
            }
        }
    }
}
