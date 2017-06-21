package com.abe.dwwd.infoentrycusview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by abe on 2017/6/21.
 */

public class InfoEntryView extends View {
    private static final int EXCETING = 1;
    private static final int FINISH = 2;
    private ValueAnimator valueAnimatorChange, valueAnimatorMove,valueAnimatorOk;
    private float cirleDistanceX = 0;
    private float cirleRadius = 10;
    private int height, weight, defineCircleWeightDistance;
    private Paint paint, textPaint,okPaint;
    private int statue = FINISH;
    private String text = "确定";
    private Path path;
    private PathMeasure pathMeasure;

    public InfoEntryView(Context context) {
        this(context, null);
    }

    public InfoEntryView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InfoEntryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawOvalCircle(canvas);
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        height = getHeight();
        weight = getWidth();
        defineCircleWeightDistance = (weight - height) / 2;
    }

    //初始化画笔
    private void initPaint() {

        paint = new Paint();
        paint.setColor(getResources().getColor(android.R.color.holo_blue_bright));
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(getResources().getColor(android.R.color.holo_red_light));
        textPaint.setTextSize(40);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setAntiAlias(false);

        okPaint = new Paint();
        okPaint.setStrokeWidth(10);
        okPaint.setAntiAlias(true);
        okPaint.setColor(getResources().getColor(android.R.color.holo_green_dark));
        okPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (statue == FINISH) {
                    changeRectCircluarAnim();
                    return true;
                }
        }
        return super.onTouchEvent(event);
    }

    //画圆角矩形
    private void drawOvalCircle(Canvas canvas) {
        RectF rectF = new RectF();
        rectF.left = cirleDistanceX;
        rectF.top = 0;
        rectF.bottom = height;
        rectF.right = weight - cirleDistanceX;
        canvas.drawRoundRect(rectF, cirleRadius, cirleRadius, paint);
        canvas.drawText(text, (weight - textPaint.measureText(text)) / 2, getBaseLineY(height / 2), textPaint);
        if (path != null) {
            canvas.drawPath(path, okPaint);
        }
    }

    //改变圆角动画
    private void changeRectCircluarAnim() {
        valueAnimatorChange = ValueAnimator.ofInt(0, defineCircleWeightDistance);
        valueAnimatorChange.setDuration(1000);
        valueAnimatorChange.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                cirleRadius = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimatorChange.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                set_draw_ok_animation();
//                moveRectCircleAnim();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                statue = EXCETING;
            }

        });
        valueAnimatorChange.start();
    }

    //移动圆形
    private void moveRectCircleAnim() {
        valueAnimatorMove = ValueAnimator.ofInt(0, defineCircleWeightDistance);
        valueAnimatorMove.setDuration(1000);
        valueAnimatorMove.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                cirleDistanceX = (int) animation.getAnimatedValue();
                textPaint.setAlpha((int) (255 - ((cirleDistanceX * 255) / defineCircleWeightDistance)));
                invalidate();
            }
        });
        valueAnimatorMove.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                set_draw_ok_animation();
            }

            @Override
            public void onAnimationStart(Animator animation) {

            }
        });
        valueAnimatorMove.start();
    }

    private float getBaseLineY(float centerY) {
        return centerY + (textPaint.getFontMetrics().bottom - textPaint.getFontMetrics().top) / 2 - textPaint.getFontMetrics().bottom;
    }

    private void initPath() {
        path = new Path();
        path.moveTo(defineCircleWeightDistance + height / 8 * 3, height / 2);
        path.lineTo(defineCircleWeightDistance + height / 2, height / 5 * 3);
        path.lineTo(defineCircleWeightDistance + height / 3 * 2, height / 5 * 2);
         pathMeasure = new PathMeasure(path,true);
    }

    /**
     * 绘制对勾的动画
     */
    DashPathEffect effect;
    private void set_draw_ok_animation() {
        initPath();
        valueAnimatorOk = ValueAnimator.ofFloat(1, 0);
        valueAnimatorOk.setDuration(5000);
        valueAnimatorOk.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                statue = FINISH;
                effect = new DashPathEffect(new float[]{pathMeasure.getLength(), pathMeasure.getLength()}, value * pathMeasure.getLength());
                okPaint.setPathEffect(effect);
                invalidate();
            }
        });
        valueAnimatorOk.start();
    }
}
