package com.example.cargame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class GameCanvas extends View {

    private Canvas c;
    private float startX = 0, startY = 0, stopX = 0, stopY = 0, touchX = 0, touchY = 0;
    private PathMeasure pathMeasure;
    int bm_offsetX, bm_offsetY;
    float iCurStep = 0;
    float distance;
    float pathLength;

    float[] pos;
    float[] tan;

    Matrix matrix;
    private Paint blackPaint, whitePaint, greenPaint, redPaint;
    private Path touchPath;
    private float width, height;
    private Bitmap b, imageBitmap;

    public GameCanvas(Context context) {
        super(context);
        init();

    }

    public GameCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public GameCanvas(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    public void init(){
        blackPaint = new Paint();
        redPaint = new Paint();
        touchPath = new Path();
        whitePaint = new Paint();
        greenPaint = new Paint();

        blackPaint.setColor(Color.BLACK);
        blackPaint.setStrokeWidth(10);
        blackPaint.setAntiAlias(true);
        blackPaint.setStyle(Paint.Style.STROKE);

        whitePaint.setColor(Color.WHITE);

        greenPaint.setColor(Color.GREEN);

        redPaint.setColor(Color.RED);

        imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.car_image);
        bm_offsetX = imageBitmap.getWidth()/2;
        bm_offsetY = imageBitmap.getHeight()/2;

        pathMeasure = new PathMeasure(touchPath, false);
        pathLength = pathMeasure.getLength();
        iCurStep = 1;
        distance = 0;
        pos = new float[2];
        tan = new float[2];


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        b = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        c = new Canvas(b);
    }

    @Override
    protected void onDraw(android.graphics.Canvas canvas) {
        width = getWidth();
        height = getHeight();

        canvas.drawCircle(width/2, height/6,width/10,blackPaint);
        canvas.drawCircle(width/2, 5*height/6,width/10,blackPaint);

        canvas.drawRect(0, height/6, width,height/6, blackPaint);
        canvas.drawLine(0, 5*height/6, width,5*height/6, blackPaint);

        Log.e("check touch",String.valueOf(checkStartTouchPoint(startX, startY))+" "+String.valueOf(checkStopTouchPoint(stopX, stopY)));
        Log.e("touch points", startX +" "+ startY+" "+stopX+" "+stopY);

        canvas.drawBitmap(b, 0, 0, whitePaint);
        canvas.drawPath(touchPath, blackPaint);

        canvas.drawCircle(width/2, height/6,80,redPaint);
        canvas.drawCircle(width/2, 5*height/6,100,greenPaint);

        if(distance < pathLength){
            pathMeasure.getPosTan(distance, pos, tan);

            matrix.reset();
            float degrees = (float)(Math.atan2(tan[1], tan[0])*180.0/Math.PI);
            matrix.postRotate(degrees, bm_offsetX, bm_offsetY);
            matrix.postTranslate(pos[0]-bm_offsetX, pos[1]-bm_offsetY);

            canvas.drawBitmap(imageBitmap, matrix, null);

            distance += iCurStep;
        }else{
            distance = 0;
        }
//        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchX = event.getX();
        touchY = event.getY();
            if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                 startX = touchX; startY = touchY;
                if(checkStartTouchPoint(startX,startY))
                    touchPath.moveTo(touchX, touchY);
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if(checkStartTouchPoint(startX,startY))
                    touchPath.lineTo(touchX, touchY);



            }else if (event.getAction() == MotionEvent.ACTION_UP) {
//                float touchX = event.getX();
//                float touchY = event.getY();
                 stopX = touchX; stopY = touchY;
                if(checkStopTouchPoint(stopX,stopY))
                    touchPath.lineTo(touchX, touchY);
                c.drawPath(touchPath, blackPaint);
                touchPath = new Path();
            }  else return false;
        if(checkStartTouchPoint(startX,startY) || checkStopTouchPoint(stopX, stopY))
            invalidate();
        return true;
    }

    public boolean checkStartTouchPoint(float x, float y){

        double distanceX = x - width/2;
        double distanceY1 = y - height/6;
        return ((distanceX * distanceX) + (distanceY1 * distanceY1) <= Math.pow(width/10,2));
    }
    public boolean checkStopTouchPoint(float x, float y){

        double distanceX = x - width/2;
        double distanceY2 = y - 5*height/6;
        return ((distanceX * distanceX) + (distanceY2 * distanceY2) <= Math.pow(width/10,2));
    }




}
