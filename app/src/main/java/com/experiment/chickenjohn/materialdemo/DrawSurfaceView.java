package com.experiment.chickenjohn.materialdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.text.DecimalFormat;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import static com.experiment.chickenjohn.materialdemo.R.color.cardview_light_background;

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
    private SurfaceView drawViewLandRuler;
    private SurfaceHolder drawViewHolderPort;
    private SurfaceHolder drawViewHolderLand;
    private SurfaceHolder drawViewHolderLandTag;
    private SurfaceHolder drawViewHolderLandRuler;
    private DrawThread drawThread;

    final int PORT = 0, LAND = 1;
    private int PORTORLAND = PORT;
    private int portHeight, portWidth;
    private int landHeight, landWidth;
    private boolean shouldRefresh = false;
    private boolean RESET = false;
    private int deltaX = 0;
    private int ecgValue = 0;
    private int touchingState = 0;

    private int x = 0, y, lastX = 0, lastY;
    private int rulerX = 0, rulerY = 0, rulerStartX = 0, rulerStartY = 0;
    private Context context;

    private Timer viewRefreshTimer = new Timer();

    public DrawSurfaceView() {
        x = 1;
        viewRefreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                shouldRefresh = true;
            }
        }, 0, 80);
    }

    public void setSurfaceViewPort(SurfaceView currentSurfaceView, int PORTORLAND) {
        this.PORTORLAND = PORTORLAND;
        if (PORTORLAND == PORT) {
            drawViewPort = currentSurfaceView;
            drawViewHolderPort = drawViewPort.getHolder();
            drawViewHolderPort.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    Canvas canvas = holder.lockCanvas();
                    canvas.drawColor(Color.rgb(255,255,255));
                    holder.unlockCanvasAndPost(canvas);
                    portWidth = drawViewPort.getWidth();
                    portHeight = drawViewPort.getHeight();
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {

                }
            });

            y = portHeight / 2;
            lastY = y;
        }
    }

    public void setSurfaceViewLand(SurfaceView currentSurfaceView,
                                   SurfaceView currentSurfaceViewTag,
                                   SurfaceView currentSurfaceViewRuler,
                                   int PORTORLAND,
                                   Context context) {
        this.PORTORLAND = PORTORLAND;
        if (PORTORLAND == LAND) {
            drawViewLand = currentSurfaceView;
            drawViewHolderLand = drawViewLand.getHolder();

            drawViewLandTag = currentSurfaceViewTag;
            drawViewHolderLandTag = drawViewLandTag.getHolder();
            drawViewLandTag.setZOrderOnTop(true);
            drawViewHolderLandTag.setFormat(PixelFormat.TRANSPARENT);

            drawViewLandRuler = currentSurfaceViewRuler;
            drawViewHolderLandRuler = drawViewLandRuler.getHolder();
            drawViewLandRuler.setZOrderOnTop(true);
            drawViewHolderLandRuler.setFormat(PixelFormat.TRANSPARENT);

            drawViewLandRuler.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            touchingState = 0;
                            rulerX = (int) event.getX();
                            rulerY = (int) event.getY();
                            rulerStartX = rulerX;
                            rulerStartY = rulerY;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            rulerX = (int) event.getX();
                            rulerY = (int) event.getY();
                            touchingState = 1;
                            drawRulerThreadStarter();
                            break;
                        case MotionEvent.ACTION_UP:
                            rulerX = (int) event.getX();
                            rulerY = (int) event.getY();
                            if (Math.abs(rulerX - rulerStartX) < 5 && Math.abs(rulerY - rulerStartY) < 5)
                                touchingState = 3;
                            else
                                touchingState = 2;
                            drawRulerThreadStarter();
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });

            drawViewHolderLand.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    landHeight = drawViewLand.getHeight();
                    landWidth = drawViewLand.getWidth();
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {

                }
            });

            y = landHeight / 2;
            lastY = y;

            this.context = context;
        }
    }

    public void drawPoint(int drawX, int drawY) {
        if (shouldRefresh) {
            switch (PORTORLAND) {
                case PORT:
                    y = (int) ((portHeight / 2) - (((float) drawY) / 280.0 * (portHeight / 2)));
                    break;
                case LAND:
                    y = (int) ((landHeight / 2) - (((float) drawY) / 260.0 * (landHeight / 2)));
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

    private void drawRulerThreadStarter() {
        landHeight = drawViewLand.getHeight();
        landWidth = drawViewLand.getWidth();
        DrawRulerThread drawRulerThread = new DrawRulerThread(drawViewHolderLandRuler);
        drawRulerThread.start();
    }

    private class DrawRulerThread extends Thread {
        private SurfaceHolder surfaceHolder = null;

        public DrawRulerThread(SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
        }

        public void run() {
            Canvas canvas = null;
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.tag);
            Paint pen = new Paint();
            pen.setColor(Color.GRAY);
            pen.setStrokeWidth(2);
            pen.setTextSize(60);
            Paint dotPen = new Paint();
            dotPen.setColor(Color.GRAY);
            dotPen.setStrokeWidth(2);
            PathEffect effect = new DashPathEffect(new float[]{2, 4, 2, 4,}, 1);
            dotPen.setPathEffect(effect);

            try {
                switch (touchingState) {
                    case 0:
                        canvas = surfaceHolder.lockCanvas();
                        break;
                    case 1:
                        canvas = surfaceHolder.lockCanvas();
                        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                        canvas.drawLine(rulerStartX, rulerStartY, rulerX, rulerY, dotPen);
                        break;
                    case 2:
                        canvas = surfaceHolder.lockCanvas();
                        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                        canvas.drawLine(rulerStartX, rulerStartY, rulerX, rulerY, pen);
                        canvas.drawBitmap(bitmap, null, new Rect(rulerStartX - 20, rulerStartY - 40,
                                rulerStartX + 20, rulerStartY), pen);
                        canvas.drawBitmap(bitmap, null, new Rect(rulerX - 20, rulerY - 40,
                                rulerX + 20, rulerY), pen);
                        canvas.drawLine(rulerStartX, rulerStartY, rulerX, rulerStartY, dotPen);
                        canvas.drawLine(rulerX, rulerStartY, rulerX, rulerY, dotPen);
                        double deltaTime = Math.abs(rulerStartX - rulerX) * EcgData.getRECORDRATE();
                        String timeInFormat = new DecimalFormat("0.##").format(deltaTime) + "s";
                        canvas.drawText(timeInFormat,
                                (float) (rulerX - rulerStartX) / 2 + rulerStartX, rulerStartY, pen);
                        double deltaVolt = Math.abs(rulerStartY - rulerY) / (double) (landHeight) * 320;
                        String voltInFormat = new DecimalFormat("0.##").format(deltaVolt) + "mV";
                        canvas.drawText(voltInFormat,
                                rulerX, (float) (rulerY - rulerStartY) / 2 + rulerStartY, pen);
                        break;
                    case 3 :
                        canvas = surfaceHolder.lockCanvas();
                        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    default:
                        break;
                }
            } finally {
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    private class DrawThread extends Thread {

        @Override
        public void run() {
            Canvas canvas = null;
            Canvas tagCanvas = null;
            Paint pen = new Paint();
            pen.setColor(Color.rgb(63, 81, 181));
            pen.setStrokeWidth(5);
            pen.setAntiAlias(true);
            Paint tagPen = new Paint();
            tagPen.setColor(Color.GRAY);
            tagPen.setStrokeWidth(1);
            tagPen.setAntiAlias(true);
            tagPen.setTextSize(60);
            try {
                switch (PORTORLAND) {
                    case PORT:
                        if (x > portWidth) {
                            x = 0;
                        }
                        canvas = drawViewHolderPort.lockCanvas(new Rect(x - deltaX, 0, x + 50, portHeight));

                        canvas.drawColor(Color.rgb(255, 255, 255));
                        canvas.drawLine(x - deltaX, lastY, x, y, pen);
                        break;
                    case LAND:
                        if (x > landWidth) {
                            x = 0;
                        }
                        pen.setColor(Color.GREEN);
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

    public void resetCanvas(){
        SurfaceHolder holder;
        if(PORTORLAND == PORT) {
            holder = drawViewHolderPort;
            Canvas canvas = holder.lockCanvas();
            canvas.drawColor(Color.rgb(255, 255, 255));
            holder.unlockCanvasAndPost(canvas);
        }
    }
}
