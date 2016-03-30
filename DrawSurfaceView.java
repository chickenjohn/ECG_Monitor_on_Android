package com.experiment.chickenjohn.materialdemo;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by chickenjohn on 2016/3/24.
 */
public class DrawSurfaceView {
    private SurfaceView drawViewPort;
    private SurfaceView drawViewLand;
    private SurfaceHolder drawViewHolderPort;
    private SurfaceHolder drawViewHolderLand;
    private DrawThread drawThread;

    final int PORT=0,LAND=1;
    private int PORTORLAND = PORT;
    private int portHeight,portWidth;
    private int landHeight,landWidth;
    private boolean shouldRefresh = false;
    private boolean RESET = false;
    private int deltaX=0;

    private int x=0,y,lastX=0,lastY;

    private Timer viewRefreshTimer = new Timer();

    public DrawSurfaceView(){
        x = 1;
        viewRefreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                shouldRefresh = true;
            }
        },0,50);
    }

    public void setSurfaceView(SurfaceView currentSurfaceView, int PORTORLAND){
        this.PORTORLAND = PORTORLAND;
        switch (PORTORLAND){
            case PORT :
                drawViewPort = currentSurfaceView;
                drawViewHolderPort = drawViewPort.getHolder();
                y = portHeight/2;
                lastY = y;
                break;
            case LAND :
                drawViewLand = currentSurfaceView;
                drawViewHolderLand = drawViewLand.getHolder();
                y = landHeight/2;
                lastY = y;
            default:
                break;
        }
    }

    public void drawPoint(int drawX, int drawY){
        if(shouldRefresh) {
            switch (PORTORLAND) {
                case PORT:
                    portHeight = drawViewPort.getHeight();
                    portWidth = drawViewPort.getWidth();
                    y = (int) ((portHeight / 2) - (((float) drawY) / 27000.0 * (portHeight / 2)));
                    break;
                case LAND:
                    landHeight = drawViewLand.getHeight();
                    landWidth = drawViewLand.getWidth();
                    y = (int) ((landHeight / 2) - (((float) drawY) / 27000.0 * (landHeight / 2)));
                    break;
                default:
                    break;
            }
            deltaX = drawX - lastX;
            lastX = drawX;
            x += deltaX;
            shouldRefresh = false;
            drawThread = new DrawThread();
            drawThread.start();
        }
    }

    private class DrawThread extends Thread{

        @Override
        public void run(){
            Canvas canvas = null;
            Paint pen = new Paint();
            pen.setColor(Color.GREEN);
            pen.setStrokeWidth(4);
            pen.setAntiAlias(false);
            pen.setMaskFilter(new BlurMaskFilter(12, BlurMaskFilter.Blur.SOLID));
            try{
                switch (PORTORLAND){
                    case PORT :
                        if(x > portWidth){
                            x = 0;
                        }
                        canvas = drawViewHolderPort.lockCanvas(new Rect(x-deltaX,0,x+50,portHeight));
                        canvas.drawColor(Color.BLACK);
                        canvas.drawLine(x-deltaX,lastY,x,y,pen);
                        break;
                    case LAND :
                        if(x > landWidth){
                            x = 0;
                        }
                        canvas = drawViewHolderLand.lockCanvas(new Rect(x-deltaX,0,x+50,landHeight));
                        canvas.drawColor(Color.BLACK);
                        canvas.drawLine(x-deltaX,lastY,x,y,pen);
                        break;
                    default:
                        break;
                }
                lastY = y;
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(canvas != null){
                   switch (PORTORLAND){
                       case PORT :
                           drawViewHolderPort.unlockCanvasAndPost(canvas);
                           break;
                       case LAND :
                           drawViewHolderLand.unlockCanvasAndPost(canvas);
                           break;
                       default:
                           break;
                   }
                }
            }
        }
    }

    public void resetSurfaceViewX(){
        x = 0;
    }
}
