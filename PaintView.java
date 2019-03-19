package com.example.root.finalbletest;

/**paint output to batteryServiceFragment**/
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class PaintView extends View{

    public PaintView(Context context,AttributeSet attrs) {
        super(context,attrs);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

//        //畫筆
//        Paint paint = new Paint();
//
//        //畫布底色
//        canvas.drawColor(Color.WHITE);
//
//        //畫筆色(灰)
//        paint.setColor(Color.GRAY);
//        //畫圓
//        canvas.drawCircle(160, 160, 150, paint);
//
//        //畫方
//        paint.setColor(Color.BLUE);
//        Rect rect = new Rect(100, 110, 120, 130);
//        canvas.drawRect(rect, paint);
//
//        //畫圓角方
//        paint.setColor(Color.GREEN);
//        RectF rectf = new RectF(200, 110, 220, 130);
//        canvas.drawRoundRect(rectf, 7, 7, paint);
//
//        //畫弧
//        paint.setColor(Color.YELLOW);
//        RectF oval = new RectF(50, 150, 270, 250);
//        canvas.drawArc(oval, 180, -180, true, paint);
//
//        //畫字
//        paint.setColor(Color.BLACK);
//        canvas.drawText("Andy", 160, 350, paint);

        paintBus(canvas);
        paintPeople(canvas);
    }

    public void paintPeople(Canvas canvas){
        Paint p = new Paint();

        p.setColor(Color.BLACK);
        //head
        canvas.drawCircle(155,175,10,p);
        //hands
        Rect rect = new Rect(125,188,185,198);
        canvas.drawRect(rect,p);
        //body
        Rect rect1 = new Rect(140,198,170,230);
        canvas.drawRect(rect1,p);
        //right-leg
        Rect rect2 = new Rect(160,230,170,270);
        canvas.drawRect(rect2,p);
        //left-leg
        Rect rect3 = new Rect(140,230,150,270);
        canvas.drawRect(rect3,p);

    }

    public void paintBus(Canvas canvas){
        Paint p = new Paint();

        p.setColor(Color.GRAY);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(4);
        RectF rectF = new RectF(30,20,900,300);
        RectF rectF1 = new RectF(45,35,885,285);
//        canvas.drawLine(20,20,20,320,p);
//        canvas.drawLine(20,20,750,20,p);
//        canvas.drawLine(750,20,750,320,p);
        canvas.drawRoundRect(rectF1,15,15,p);
        p.setStrokeWidth(8);
        canvas.drawRoundRect(rectF,15,15,p);
        p.setColor(Color.WHITE);
        canvas.drawLine(70,300,170,300,p);
        canvas.drawLine(670,300,770,300,p);
        canvas.drawLine(80,285,160,285,p);
        canvas.drawLine(680,285,760,285,p);
        //wheel
        p.setARGB(200,33,33,33);
        canvas.drawCircle(120,320,45,p);
        canvas.drawCircle(720,320,45,p);
        p.setStyle(Paint.Style.FILL);
        canvas.drawCircle(120,320,8,p);
        canvas.drawCircle(720,320,8,p);
        p.setColor(Color.GRAY);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(5);
        canvas.drawCircle(120,320,35,p);
        canvas.drawCircle(720,320,35,p);
        //door-front
        p.setARGB(100,21,101,192);
        rectF.set(600,150,50,100);
        canvas.drawLine(790,150,790,280,p);
        canvas.drawLine(790,150,850,150,p);
        canvas.drawLine(850,150,850,280,p);
        canvas.drawRoundRect(rectF,10,10,p);
        //door-black
        canvas.drawLine(390,150,560,150,p);
        canvas.drawLine(390,150,390,280,p);
        canvas.drawLine(560,150,560,280,p);
        canvas.drawRoundRect(rectF,10,10,p);
    }
}
