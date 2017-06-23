package com.abe.dwwd.infoentrycusview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by abe on 2017/6/21.
 */

public class InfoEntryButtonView extends View {
    private static final String TAG = "InfoEntryView";
    private static final int EXCETING = 1;
    private static final int FINISH = 2;
    private static final int IDLE = 3;
    private ValueAnimator valueAnimatorChange, valueAnimatorMove, valueAnimatorOk;
    private float cirleDistanceX = 0;
    private float cirleRadius = 10;
    private int defineTextSize = 40;
    private int height, weight, defineCircleWeightDistance;
    private Paint paint, textPaint, okPaint;
    private int statue = IDLE;
    private String text = "确定";
    private Path path;
    private PathMeasure pathMeasure;
    private AnimatorSet animatorSet;
    private boolean startDrawOk = false;

    public InfoEntryButtonView(Context context) {
        this(context, null);
    }

    public InfoEntryButtonView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InfoEntryButtonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawOvalCircle(canvas);
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        height = h;
        weight = w;
        defineCircleWeightDistance = (weight - height) / 2;
        initPath();
        initAnimator();
    }

    //初始化画笔
    private void initPaint() {

        paint = new Paint();
        paint.setColor(getResources().getColor(android.R.color.holo_blue_bright));
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(getResources().getColor(android.R.color.holo_red_light));
        textPaint.setTextSize(defineTextSize);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setAntiAlias(false);

        okPaint = new Paint();
        okPaint.setStrokeWidth(10);
        okPaint.setAntiAlias(true);
        okPaint.setColor(getResources().getColor(android.R.color.holo_green_dark));
        okPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e(TAG, event.getAction() + "");
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (statue == IDLE) {
                    start();
                }
            case MotionEvent.ACTION_DOWN:
                if (statue == IDLE) {
                    return true;
                }
        }
        return super.onTouchEvent(event);
    }

    private void initAnimator() {
        animatorSet = new AnimatorSet();
        changeRectCircluarAnim();
        moveRectCircleAnim();
        okAnimation();
        animatorSet
                .play(valueAnimatorChange)
                .with(valueAnimatorMove)
                .before(valueAnimatorOk);
    }

    private void start() {
        animatorSet.start();
        statue = EXCETING;
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
        if (startDrawOk) {
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
    }

    private float getBaseLineY(float centerY) {
        return centerY + (textPaint.getFontMetrics().bottom - textPaint.getFontMetrics().top) / 2 - textPaint.getFontMetrics().bottom;
    }

    private void initPath() {
        path = new Path();
        path.moveTo(defineCircleWeightDistance + height / 8 * 3, height / 2);
        path.lineTo(defineCircleWeightDistance + height / 2, height / 5 * 3);
        path.lineTo(defineCircleWeightDistance + height / 3 * 2, height / 5 * 2);
        pathMeasure = new PathMeasure(path, true);
    }

    /**
     * 绘制对勾的动画
     */
    DashPathEffect effect;
    private void okAnimation() {
        valueAnimatorOk = ValueAnimator.ofFloat(1, 0);
        valueAnimatorOk.setDuration(1000);
        valueAnimatorOk.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                startDrawOk = true;
                float value = (Float) animation.getAnimatedValue();
                effect = new DashPathEffect(new float[]{pathMeasure.getLength(), pathMeasure.getLength()}, value * pathMeasure.getLength());
                okPaint.setPathEffect(effect);
                invalidate();
            }
        });
        valueAnimatorOk.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                statue = FINISH;
            }
        });
    }

    public void resetView(){
        statue = IDLE;
        cirleDistanceX = 0;
        cirleRadius = 10;
        effect = new DashPathEffect(new float[]{pathMeasure.getLength(),pathMeasure.getLength()},pathMeasure.getLength());
        okPaint.setPathEffect(effect);
        textPaint.setAlpha(255);
        invalidate();
    }
}
