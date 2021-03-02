package com.parker.uipractice;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;


public class SwitchButton extends View {

    private String TAG = this.getClass().getSimpleName();

    /**
     * 默认宽
     */
    private static final int DEFAULT_WIDTH = dp2pxInt(56);

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
     * 边框颜色
     */
    private int borderColor;

    /**
     * 边框大小
     */
    private int borderSize;

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
    private Paint paintRoundRectBorder;

    /**
     * 矩形填充背景画笔
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

    private OnSwitchChangeListener listener;

    private Bitmap mSwitchButtonBackgroundBitmap;
    private Bitmap mSwitchOnButtonBitmap;
    private Bitmap mSwitchOffButtonBitmap;

    public SwitchButton(Context context) {
        this(context, null);
    }

    public SwitchButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttrs(context, attrs);
    }

    /**
     * 初始化画笔
     */
    private void init() {
        rectF = new RectF();
        paintRoundRectBorder = new Paint();
        paintRoundRect = new Paint();
        paintRoundButton = new Paint();
        paintRoundUnSwitchRect = new Paint();
        paintRoundRectBorder.setAntiAlias(true);
        paintRoundRectBorder.setStyle(Paint.Style.STROKE);
        paintRoundRect.setAntiAlias(true);
        paintRoundButton.setAntiAlias(true);
        paintRoundUnSwitchRect.setAntiAlias(true);
        paintRoundUnSwitchRect.setColor(Color.parseColor("#7fffffff"));
    }

    /**
     * 初始化属性
     *
     * @param context
     * @param attrs
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.SwitchButton);
        onColor = mTypedArray.getColor(R.styleable.SwitchButton_on_color, 0xFF32C781);
        offColor = mTypedArray.getColor(R.styleable.SwitchButton_off_color, 0xFFC9C9C9);
        buttonBgColor = mTypedArray.getColor(R.styleable.SwitchButton_button_bg_color, 0xFFFFFFFF);
        switchStatus = mTypedArray.getBoolean(R.styleable.SwitchButton_switch_status, true);
        borderColor = mTypedArray.getColor(R.styleable.SwitchButton_border_color, 0xFFFFFFFF);
        borderSize = mTypedArray.getDimensionPixelSize(R.styleable.SwitchButton_border_size, 1);
        innerThick = mTypedArray.getDimensionPixelSize(R.styleable.SwitchButton_inner_thick, 0);
        Log.d(TAG, "initAttrs: innerThick:" + innerThick);

        //开关背景图片
        int switchBgResourceId = mTypedArray.getResourceId(R.styleable.SwitchButton_switch_background, -1);
        int switchOnResourceId = mTypedArray.getResourceId(R.styleable.SwitchButton_switch_on_background, -1);
        int switchOffResourceId = mTypedArray.getResourceId(R.styleable.SwitchButton_switch_off_background, -1);

        mTypedArray.recycle();
        rate = switchStatus ? 1.0f : 0.0f;
        paintRoundButton.setColor(buttonBgColor);
        paintRoundRect.setColor(onColor);
        paintRoundRectBorder.setColor(borderColor);
        paintRoundRectBorder.setStrokeWidth(borderSize);

        if (switchBgResourceId != -1) {
            setSwitchButtonBackgroundResource(switchBgResourceId);
        }

        if (switchOnResourceId != -1) {
            setSwitchOnButtonResource(switchOnResourceId);
        }

        if (switchOffResourceId != -1) {
            setSwitchOffButtonResource(switchOffResourceId);
        }
    }


    /**
     * 设置背景资源
     *
     * @param switch_background
     */
    public void setSwitchButtonBackgroundResource(int switch_background) {
        mSwitchButtonBackgroundBitmap = BitmapFactory.decodeResource(getResources(), switch_background);
    }

    /**
     * 设置关按钮资源
     *
     * @param switch_on_button
     */
    public void setSwitchOnButtonResource(int switch_on_button) {
        mSwitchOnButtonBitmap = BitmapFactory.decodeResource(getResources(), switch_on_button);
    }

    /**
     * 设置关按钮资源
     *
     * @param switch_off_button
     */
    public void setSwitchOffButtonResource(int switch_off_button) {
        mSwitchOffButtonBitmap = BitmapFactory.decodeResource(getResources(), switch_off_button);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.UNSPECIFIED
                || widthMode == MeasureSpec.AT_MOST) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(DEFAULT_WIDTH, MeasureSpec.EXACTLY);
            //Log.d(TAG, "onMeasure: width Mode AT_MOST or UNSPECIFIED");
        }
        if (heightMode == MeasureSpec.UNSPECIFIED
                || heightMode == MeasureSpec.AT_MOST) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(DEFAULT_HEIGHT, MeasureSpec.EXACTLY);
            //Log.d(TAG, "onMeasure: height Mode AT_MOST or UNSPECIFIED");
        }

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        Log.d(TAG, "onMeasure:: width:" + width + ",height:" + height);
        int realWidth = width - getPaddingLeft() - getPaddingRight();
        int realHeight = height - getPaddingTop() - getPaddingBottom();
        if (realHeight <= realWidth / 2) {
            radius = realHeight / 2;
            width = realHeight * 2 + getPaddingLeft() + getPaddingRight();
        } else {
            radius = realWidth / 4;
            height = realWidth / 2 + getPaddingTop() + getPaddingBottom();
        }

        Log.d(TAG, "onMeasure:: width:" + width + ",height:" + height + ",realWidth:" + realWidth + ",realHeight:" + realHeight + ",rasius:" + radius);

        rectF.left = getPaddingLeft();
        rectF.right = width - getPaddingRight();
        rectF.top = getPaddingTop();
        rectF.bottom = height - getPaddingBottom();
        Log.d(TAG, "onMeasure:: (" + rectF.left + "," + rectF.top + "," + rectF.right + "," + rectF.bottom + ")");
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //设置渐变背景
        //paintRoundRect.setColor(getColor(rate));
        //canvas.drawRoundRect(rectF, radius, radius, paintRoundRect);

        //设置透明背景
        drawBackground(canvas);

        //canvas.drawCircle(getPaddingLeft() + radius + 2 * radius * rate, getPaddingTop() + radius, radius - innerThick, paintRoundButton);
        drawSwitch(canvas);
        if (!isCanSwitch) {
            canvas.drawRoundRect(rectF, radius, radius, paintRoundUnSwitchRect);
        }
    }

    /**
     * 绘制背景
     *
     * @param canvas
     */
    private void drawBackground(Canvas canvas) {
        canvas.drawRoundRect(rectF, radius, radius, paintRoundRectBorder);
        canvas.drawRoundRect(rectF, radius, radius, paintRoundRect);
    }

    /**
     * 绘制开关
     *
     * @param canvas
     */
    private void drawSwitch(Canvas canvas) {
        float cx;
        if (rate == 1) {
            cx = getPaddingLeft() + radius + 2 * radius * rate - 3;
        } else {
            cx = getPaddingLeft() + radius + 2 * radius * rate;
        }
        int cy = getPaddingTop() + radius - 3;
        int radius = this.radius - innerThick;
        Log.d(TAG, "drawSwitch:: cx:" + cx + ",cy:" + cy + ",raduis:" + radius + ",rate:" + rate);
        //canvas.drawCircle(cx, cy, radius, paintRoundButton);

        //绘制开关
        Paint paint = new Paint();
        if (rate >= 0.5) {
            canvas.drawBitmap(mSwitchOnButtonBitmap, cx - radius, cy - radius, paint);
        } else if (rate < 0.5) {
            canvas.drawBitmap(mSwitchOffButtonBitmap, cx - radius, cy - radius, paint);
        }
    }

    /**
     * 根据比例获取渐变颜色
     *
     * @param radio 比例
     * @return 渐变颜色
     */
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

    /**
     * 获取颜色透明度
     *
     * @param alpha
     * @param color
     * @return
     */
    private int getColor(int alpha, int color) {
        int red = Color.red(color);
        int blue = Color.blue(color);
        int green = Color.green(color);
        return Color.argb(alpha, red, blue, green);
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
