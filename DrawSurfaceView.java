package com.experiment.chickenjohn.materialdemo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

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
    int PORTORLAND = PORT;
    int portHeight,portWidth;
    int landHeight,landWidth;

    int x=1,y,lastY;

    public DrawSurfaceView(){
        x = 1;
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

    public void drawPoint(int drawY){
        switch (PORTORLAND) {
            case PORT :
                portHeight = drawViewPort.getHeight();
                portWidth = drawViewPort.getWidth();
                y = (int)((portHeight/2) - (((float)drawY)/22000.0 * (portHeight/2)));
                break;
            case LAND :
                landHeight = drawViewLand.getHeight();
                landWidth = drawViewLand.getWidth();
                y = (int)((landHeight/2) - (((float)drawY)/22000.0 * (landHeight/2)));
                break;
            default:
                break;
        }

        drawThread = new DrawThread();
        drawThread.start();
    }

    private class DrawThread extends Thread{

        @Override
        public void run(){
            Canvas canvas = null;
            Paint pen = new Paint();
            pen.setColor(Color.GREEN);
            pen.setStrokeWidth(2);
            try{
                switch (PORTORLAND){
                    case PORT :
                        if(x >= portWidth){
                            x = 1;
                            ClearDraw();
                        }
                        canvas = drawViewHolderPort.lockCanvas(new Rect(x-1,0,x+2,portHeight));
                        canvas.drawColor(Color.BLACK);
                        canvas.drawLine(x-1,lastY,x,y,pen);
                        break;
                    case LAND :
                        if(x >= landWidth){
                            x = 1;
                            ClearDraw();
                        }
                        canvas = drawViewHolderLand.lockCanvas(new Rect(x-1,0,x+2,landHeight));
                        canvas.drawColor(Color.BLACK);
                        canvas.drawLine(x-1,lastY,x,y,pen);
                        break;
                    default:
                        break;
                }
                x += 1;
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

    private void ClearDraw(){
        Canvas canvas;
        switch (PORTORLAND){
            case PORT :
                canvas = drawViewHolderPort.lockCanvas(null);
                canvas.drawColor(Color.BLACK);
                drawViewHolderPort.unlockCanvasAndPost(canvas);
                break;
            case LAND :
                canvas = drawViewHolderLand.lockCanvas(null);
                canvas.drawColor(Color.BLACK);
                drawViewHolderLand.unlockCanvasAndPost(canvas);
                break;
            default:
                break;
        }
        x = 1;
    }

    public void resetSurfaceViewX(){
        x = 1;
    }
}
