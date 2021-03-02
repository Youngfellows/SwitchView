package com.parker.uipractice;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;


public class GradientSwitchView extends View {

    private String TAG = this.getClass().getSimpleName();

    /**
     * 默认宽
     */
    private static final int DEFAULT_WIDTH = dp2pxInt(58);

    /**
     * 默认高
     */
    private static final int DEFAULT_HEIGHT = dp2pxInt(36);

    /**
     * 开关状态
     */
    private boolean switchStatus = false;

    /**
     * 上一次的开关状态
     */
    private boolean lastStatus;

    /**
     * 内部按钮半径
     */
    private int radius;

    /**
     * 打开时的背景色
     */
    private int onColor = 0xFF32C781;

    /**
     * 关闭时的背景色
     */
    private int offColor = 0xFFC9C9C9;
    private int buttonBgColor = 0xFFFFFFFF;

    /**
     * 内部圆形跟边缘的距离
     */
    private int innerThick = 8;

    /**
     * 矩形边框
     */
    private RectF rectF;

    /**
     * 矩形边框画笔
     */
    private Paint paintRoundRect;

    /**
     * 圆形画笔
     */
    private Paint paintRoundButton;

    /**
     * 不能切换的画笔
     */
    private Paint paintRoundUnSwitchRect;

    /**
     * 移动比例
     */
    private float rate = 0.0f;

    /**
     * 上次按下的移动比例
     */
    private float lastRate;


    /**
     * 能否切换
     */
    private boolean isCanSwitch = true;

    /**
     * 按下的X坐标
     */
    private float startX;

    /**
     * 按下的Y坐标
     */
    private float startY;

    /**
     * 上次按下的X坐标
     */
    private float lastX;

    /**
     * 按下时间
     */
    private long downTime;

    /**
     * 是否移动
     */
    private boolean hasMoved;

    /**
     * 开关状态变化回调
     */
    private OnSwitchChangeListener listener;

    private LinearGradient mLinearGradient;
    private Paint mPaint;

    public GradientSwitchView(Context context) {
        this(context, null);
    }

    public GradientSwitchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GradientSwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        rectF = new RectF();
        paintRoundRect = new Paint();
        paintRoundButton = new Paint();
        paintRoundUnSwitchRect = new Paint();
        paintRoundRect.setAntiAlias(true);
        paintRoundButton.setAntiAlias(true);
        paintRoundUnSwitchRect.setAntiAlias(true);
        paintRoundUnSwitchRect.setColor(Color.parseColor("#7fffffff"));
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.SwitchView);
        onColor = mTypedArray.getColor(R.styleable.SwitchView_on_color, 0xFF32C781);
        offColor = mTypedArray.getColor(R.styleable.SwitchView_off_color, 0xFFC9C9C9);
        buttonBgColor = mTypedArray.getColor(R.styleable.SwitchView_button_bg_color, 0xFFFFFFFF);
        switchStatus = mTypedArray.getBoolean(R.styleable.SwitchView_switch_status, true);
        mTypedArray.recycle();
        rate = switchStatus ? 1.0f : 0.0f;
        paintRoundButton.setColor(buttonBgColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.UNSPECIFIED
                || widthMode == MeasureSpec.AT_MOST) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(DEFAULT_WIDTH, MeasureSpec.EXACTLY);
            Log.d(TAG, "onMeasure: width Mode AT_MOST or UNSPECIFIED");
        }
        if (heightMode == MeasureSpec.UNSPECIFIED
                || heightMode == MeasureSpec.AT_MOST) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(DEFAULT_HEIGHT, MeasureSpec.EXACTLY);
            Log.d(TAG, "onMeasure: height Mode AT_MOST or UNSPECIFIED");
        }

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int realWidth = width - getPaddingLeft() - getPaddingRight();
        int realHeight = height - getPaddingTop() - getPaddingBottom();
        if (realHeight <= realWidth / 2) {
            radius = realHeight / 2;
            width = realHeight * 2 + getPaddingLeft() + getPaddingRight();
        } else {
            radius = realWidth / 4;
            height = realWidth / 2 + getPaddingTop() + getPaddingBottom();
        }
        rectF.left = getPaddingLeft();
        rectF.right = width - getPaddingRight();
        rectF.top = getPaddingTop();
        rectF.bottom = height - getPaddingBottom();
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paintRoundRect.setColor(getColor(rate));
        canvas.drawRoundRect(rectF, radius, radius, paintRoundRect);
        //canvas.drawCircle(getPaddingLeft() + radius + 2 * radius * rate, getPaddingTop() + radius, radius - innerThick, paintRoundButton);
        drawGradientCircle(canvas);
        if (!isCanSwitch) {
            canvas.drawRoundRect(rectF, radius, radius, paintRoundUnSwitchRect);
        }
    }

    /**
     * 绘制渐变圆
     */
    private void drawGradientCircle(Canvas canvas) {
        float cx = getPaddingLeft() + radius + 2 * radius * rate;
        int cy = getPaddingTop() + radius;
        int radius = this.radius - innerThick;
        //canvas.drawCircle(cx, cy, radius, paintRoundButton);

        /**
        //绘制圆环
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);
        mPaint.setAntiAlias(true);
        RectF rectF = new RectF(cx - radius, cy - radius, cx + radius, cy + radius);
        //mLinearGradient = new LinearGradient(500, 800, 800, 800, new int[]{Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE}, null, Shader.TileMode.CLAMP);
        mLinearGradient = new LinearGradient(cx - radius, cy - radius, cx + radius, cy + radius, new int[]{this.getResources().getColor(R.color.color_gradient_start), this.getResources().getColor(R.color.color_gradient_end)}, null, Shader.TileMode.CLAMP);
        mPaint.setShader(mLinearGradient);
        canvas.drawArc(rectF, 0, 360, false, mPaint);//先画圆圈
        */

        mPaint = new Paint();
        mLinearGradient = new LinearGradient(cx - radius, cy - radius, cx + radius, cy + radius, new int[]{this.getResources().getColor(R.color.color_gradient_start), this.getResources().getColor(R.color.color_gradient_end)}, null, Shader.TileMode.CLAMP);
        mPaint.setShader(mLinearGradient);
        canvas.drawCircle(cx, cy, radius, mPaint);
    }

    private int getColor(float radio) {
        int redStart = Color.red(offColor);
        int blueStart = Color.blue(offColor);
        int greenStart = Color.green(offColor);
        int redEnd = Color.red(onColor);
        int blueEnd = Color.blue(onColor);
        int greenEnd = Color.green(onColor);
        int red = (int) (redStart + ((redEnd - redStart) * radio + 0.5));
        int greed = (int) (greenStart + ((greenEnd - greenStart) * radio + 0.5));
        int blue = (int) (blueStart + ((blueEnd - blueStart) * radio + 0.5));
        return Color.argb(255, red, greed, blue);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isCanSwitch) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                lastX = startX;
                downTime = System.currentTimeMillis();
                lastRate = rate;
                lastStatus = switchStatus;
                break;
            case MotionEvent.ACTION_MOVE:
                float xMove = event.getX();
                float yMove = event.getY();
                if (Math.abs(xMove - startX) < 10 &&
                        Math.abs(yMove - startY) < 10) {
                    hasMoved = false;
                    break;
                }
                if (xMove > lastX) {

                } else {

                }
                lastX = xMove;
                hasMoved = true;
                rate = lastRate + (xMove - startX) / (radius * 2);
                if (rate > 1) {
                    rate = 1;
                    switchStatus = true;
                }
                if (rate < 0) {
                    rate = 0;
                    switchStatus = false;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (hasMoved) {
                    hasMoved = false;
                    if (rate >= 0.5 && rate != 1) {
                        rate = 1;
                        invalidate();
                        switchStatus = true;
                    } else if (rate < 0.5 && rate != 0) {
                        rate = 0;
                        invalidate();
                        switchStatus = false;
                    }
                    if (lastStatus != switchStatus) {
                        if (listener != null) {
                            listener.onSwitchChange(switchStatus);
                        }
                    }
                } else {
                    //点击事件
                    long pressTime = System.currentTimeMillis();
                    if (pressTime - downTime < 200) {
                        setSwitchStatus(!switchStatus, true);
                        if (listener != null) {
                            listener.onSwitchChange(switchStatus);
                        }
                    }
                }
                break;
        }
        return true;
    }

    private static float dp2px(float dp) {
        Resources r = Resources.getSystem();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    private static int dp2pxInt(float dp) {
        return (int) dp2px(dp);
    }

    /**
     * 设置是否切换
     *
     * @param canSwitch
     */
    public void setCanSwitch(boolean canSwitch) {
        isCanSwitch = canSwitch;
    }

    /**
     * 设置开关的状态
     *
     * @param switchStatus 开或者关
     */
    public void setSwitchStatus(boolean switchStatus) {
        if (this.switchStatus != switchStatus) {
            this.switchStatus = switchStatus;
            rate = switchStatus ? 1.0f : 0.0f;
            invalidate();
        }
    }

    /**
     * 切换开关状态
     *
     * @param switchStatus  开或者关
     * @param isSingleClick 是否点击
     */
    public void setSwitchStatus(final boolean switchStatus, boolean isSingleClick) {
        if (this.switchStatus != switchStatus) {
            this.switchStatus = switchStatus;
            if (isSingleClick) {
                valueAnimator(!switchStatus ? 1.0f : 0.0f, switchStatus ? 1.0f : 0.0f, 300, new OnAnimUpdateListener() {
                    @Override
                    public void onAnimationUpdate(float curValue) {
                        rate = curValue;
                        invalidate();
                    }

                    @Override
                    public void onAnimationFinish() {
                        rate = switchStatus ? 1.0f : 0.0f;
                        invalidate();
                    }
                }).start();
            } else {
                rate = switchStatus ? 1.0f : 0.0f;
                invalidate();
            }
        }
    }

    /**
     * 获取当前更新的值
     *
     * @param startValue
     * @param endValue
     * @param duration
     * @param onAnimUpdateListener
     */
    public ValueAnimator valueAnimator(float startValue, float endValue, int duration, final OnAnimUpdateListener onAnimUpdateListener) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(startValue, endValue);
        valueAnimator.setDuration(duration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                float currentValue = (Float) animator.getAnimatedValue();
                onAnimUpdateListener.onAnimationUpdate(currentValue);
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                onAnimUpdateListener.onAnimationFinish();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        return valueAnimator;
    }


    public void setOnSwitchChangeListener(OnSwitchChangeListener listener) {
        this.listener = listener;
    }

    public boolean getSwitchStatus() {
        return switchStatus;
    }

    public interface OnSwitchChangeListener {
        void onSwitchChange(boolean switchStatus);
    }


    public interface OnAnimUpdateListener {

        public void onAnimationUpdate(float curValue);

        public void onAnimationFinish();

    }
}
