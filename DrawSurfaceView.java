package com.experiment.chickenjohn.materialdemo;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.lang.reflect.Type;
import java.util.Timer;
import java.util.TimerTask;

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

public class DrawSurfaceView {
    private SurfaceView drawViewPort;
    private SurfaceView drawViewLand;
    private SurfaceView drawViewLandTag;
    private SurfaceHolder drawViewHolderPort;
    private SurfaceHolder drawViewHolderLand;
    private SurfaceHolder drawViewHolderLandTag;
    private DrawThread drawThread;

    final int PORT = 0, LAND = 1;
    private int PORTORLAND = PORT;
    private int portHeight, portWidth;
    private int landHeight, landWidth;
    private boolean shouldRefresh = false;
    private boolean RESET = false;
    private int deltaX = 0;
    private int ecgValue = 0;

    private int x = 0, y, lastX = 0, lastY;

    private Timer viewRefreshTimer = new Timer();

    public DrawSurfaceView() {
        x = 1;
        viewRefreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                shouldRefresh = true;
            }
        }, 0, 50);
    }

    public void setSurfaceViewPort(SurfaceView currentSurfaceView, int PORTORLAND) {
        this.PORTORLAND = PORTORLAND;
        if (PORTORLAND == PORT) {
            drawViewPort = currentSurfaceView;
            drawViewHolderPort = drawViewPort.getHolder();
            y = portHeight / 2;
            lastY = y;
        }
    }

    public void setSurfaceViewLand(SurfaceView currentSurfaceView, SurfaceView currentSurfaceViewTag, int PORTORLAND) {
        this.PORTORLAND = PORTORLAND;
        if (PORTORLAND == LAND) {
            drawViewLand = currentSurfaceView;
            drawViewHolderLand = drawViewLand.getHolder();
            drawViewLandTag = currentSurfaceViewTag;
            drawViewHolderLandTag = drawViewLandTag.getHolder();
            drawViewLandTag.setZOrderOnTop(true);
            drawViewHolderLandTag.setFormat(PixelFormat.TRANSPARENT);
            y = landHeight / 2;
            lastY = y;
        }
    }

    public void drawPoint(int drawX, int drawY) {
        if (shouldRefresh) {
            switch (PORTORLAND) {
                case PORT:
                    portHeight = drawViewPort.getHeight();
                    portWidth = drawViewPort.getWidth();
                    y = (int) ((portHeight / 2) - (((float) drawY) / 220.0 * (portHeight / 2)));
                    break;
                case LAND:
                    landHeight = drawViewLand.getHeight();
                    landWidth = drawViewLand.getWidth();
                    y = (int) ((landHeight / 2) - (((float) drawY) / 220.0 * (landHeight / 2)));
                    break;
                default:
                    break;
            }
            deltaX = drawX - lastX;
            lastX = drawX;
            x += deltaX;
            shouldRefresh = false;
            ecgValue = drawY;
            drawThread = new DrawThread();
            drawThread.start();
        }
    }

    private class DrawThread extends Thread {

        @Override
        public void run() {
            Canvas canvas = null;
            Canvas tagCanvas = null;
            Paint pen = new Paint();
            pen.setColor(Color.GREEN);
            pen.setStrokeWidth(4);
            pen.setAntiAlias(false);
            Paint tagPen = new Paint();
            tagPen.setColor(Color.GRAY);
            tagPen.setStrokeWidth(1);
            tagPen.setAntiAlias(false);
            tagPen.setTextSize(40);
            try {
                switch (PORTORLAND) {
                    case PORT:
                        if (x > portWidth) {
                            x = 0;
                        }
                        canvas = drawViewHolderPort.lockCanvas(new Rect(x - deltaX, 0, x + 50, portHeight));
                        canvas.drawColor(Color.BLACK);
                        canvas.drawLine(x - deltaX, lastY, x, y, pen);
                        break;
                    case LAND:
                        if (x > landWidth) {
                            x = 0;
                        }
                        canvas = drawViewHolderLand.lockCanvas(new Rect(x - deltaX, 0, x + 100, landHeight));
                        tagCanvas = drawViewHolderLandTag.lockCanvas();
                        canvas.drawColor(Color.BLACK);
                        canvas.drawLine(x - deltaX, lastY, x, y, pen);
                        tagCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                        tagCanvas.drawLine(x, 0, x, landHeight, tagPen);
                        tagCanvas.drawText(Integer.toString(ecgValue) + "mV", x, y, tagPen);
                        break;
                    default:
                        break;
                }
                lastY = y;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    switch (PORTORLAND) {
                        case PORT:
                            drawViewHolderPort.unlockCanvasAndPost(canvas);
                            break;
                        case LAND:
                            drawViewHolderLand.unlockCanvasAndPost(canvas);
                            drawViewHolderLandTag.unlockCanvasAndPost(tagCanvas);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    public void resetSurfaceViewX() {
        x = 0;
    }
}
