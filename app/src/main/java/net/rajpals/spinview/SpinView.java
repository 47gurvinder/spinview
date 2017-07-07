package net.rajpals.spinview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import java.util.Random;

/**
 * Author: Gurwinder Singh
 * Email: 47gurvinder@gmail.com
 * 07-07-17
 **/
public class SpinView extends View {


    Paint linePaint;
    private int[] mImageList = new int[]{};
    private RectF mViewBounds = new RectF();
    private int mDesiredHeight = 300;
    private int mDesiredWidth = 300;
    private int mCenterCircleRadius = 24;
    private int mNewAngle;
    private int mLastAngle = 0;
    private static final int MINIMUM_ANGLE = 360 * 2;
    private boolean showSelectedItem;
    private int mStartAngle = -90;
    private int multiRatio = 0;

    public SpinView(Context context) {
        super(context);
        initView();
    }

    public SpinView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SpinView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {

        mViewBounds = new RectF(0, 0, mDesiredWidth, mDesiredHeight);


        linePaint = new Paint();
        linePaint.setColor(Color.GREEN);
        linePaint.setStrokeWidth(4);

        updateView();
    }

    private void updateView() {
        setLayoutParams(new ViewGroup.LayoutParams((int) mViewBounds.right, (int) mViewBounds.bottom));

    }


    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        mViewBounds.right = width;
        mViewBounds.bottom = width;
        //  updateView();
        invalidate();

    }

    protected void createItemOval(Bitmap bitmap, Canvas canvas, int items, int itemNumber) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        RectF imageBound = new RectF();
        Matrix m = new Matrix();
        RectF src = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF dst = mViewBounds;
        m.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER);
        Shader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        shader.setLocalMatrix(m);
        paint.setShader(shader);
        m.mapRect(imageBound, src);
        float oneAngle = (float) ((float) 360 / (float) items);
        float start = (itemNumber * oneAngle) - oneAngle;
        canvas.drawArc(imageBound, mStartAngle + start, oneAngle, true, paint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircle(canvas);

        drawMenuItems(canvas);
        if (showSelectedItem)
            drawSelectedItem(canvas);
        drawLines(canvas);
        //drawBorder(canvas);
        drawBorderOuter(canvas);
        drawCenterComponents(canvas);
    }

    private void drawSelectedItem(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(180, 255, 0, 0));
        float unit = (float) 360 / (float) mImageList.length;
//        float start = (((getSelectedItem()-1)*unit)-unit)-getAngle();
        float start = 0 - ((getSelectedItem() - 1) * unit) - unit;
        canvas.drawArc(mViewBounds, mStartAngle + start, unit, true, paint);
    }

    private void drawCenterComponents(Canvas canvas) {
        /* draw circle*/
        Paint paint = new Paint();
        paint.setColor(Color.DKGRAY);
        int radius = mCenterCircleRadius;
        int centerX = (int) mViewBounds.right / 2;
        int centerY = (int) mViewBounds.bottom / 2;
        canvas.drawCircle(centerX, centerY, radius, paint);


        /* draw circle border*/
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        int mStrokeWidth = 2;
        paint.setStrokeWidth(mStrokeWidth);
        int strokeSize = mStrokeWidth / 2;
        RectF imageBound = new RectF(centerX - radius + strokeSize, centerX - radius + strokeSize, centerY + radius - strokeSize, centerY + radius - strokeSize);
        canvas.drawArc(imageBound, mStartAngle + 0, 360, true, paint);


    }


    public void setImageList(@DrawableRes int[] images) {
        this.mImageList = images;
        invalidate();
    }

    private void drawMenuItems(Canvas canvas) {
        float[] angles = getAngles(mImageList.length);
        for (int i = 0; i < angles.length; i++) {
            Bitmap b = getBitmapFromResId(mImageList[i]);
            createItemOval(b, canvas, mImageList.length, i + 1);
        }
    }

    private Bitmap getBitmapFromResId(int resId) {
        return BitmapFactory.decodeResource(getResources(), resId);
    }

    private void drawLines(Canvas canvas) {
        Paint paintLine = new Paint();
        paintLine.setColor(Color.LTGRAY);
        paintLine.setStrokeWidth(3);
        int centerX = (int) mViewBounds.right / 2;
        int centerY = (int) mViewBounds.bottom / 2;
        PointP[] pointP = getLinePoints(mImageList.length);
        for (PointP point : pointP) {
            canvas.drawLine(point.x, point.y, centerX, centerY, paintLine);
        }
    }

    private void drawCircle(Canvas canvas) {
        linePaint.setColor(Color.MAGENTA);
        int radius = (int) (mViewBounds.right - mViewBounds.left) / 2;
        int centerX = (int) mViewBounds.right / 2;
        int centerY = (int) mViewBounds.bottom / 2;
        canvas.drawCircle(centerX, centerY, radius, linePaint);


    }

    private void drawBorder(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.MAGENTA);
        paint.setStrokeWidth(4);
        int strokeSize = 4 / 2;
        RectF imageBound = new RectF(mViewBounds.left + strokeSize, mViewBounds.top + strokeSize, mViewBounds.right - strokeSize, mViewBounds.bottom - strokeSize);
        canvas.drawArc(imageBound, mStartAngle + 0, 360, true, paint);
    }

    private void drawBorderOuter(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.LTGRAY);
        paint.setStrokeWidth(8);
        int strokeSize = 8 / 2;
        RectF imageBound = new RectF(mViewBounds.left + strokeSize, mViewBounds.top + strokeSize, mViewBounds.right - strokeSize, mViewBounds.bottom - strokeSize);
        canvas.drawArc(imageBound, mStartAngle + 0, 360, true, paint);
    }

    private PointP[] getLinePoints(int length) {
        int radius = (int) (mViewBounds.right - mViewBounds.left) / 2;
        //x0, y0 - center's coordinates
        int x0 = (int) mViewBounds.right / 2;
        int y0 = (int) mViewBounds.bottom / 2;
        int n = length;
        int r = radius;
        PointP[] pointPs = new PointP[n];
        for (int i = 0; i < n; i++) {
            PointP point = new PointP();
            float angle = (float) i * (float) ((float) 360 / (float) n);
            point.x = (float) (x0 + r * Math.cos(Math.toRadians(angle)));
            point.y = (float) (y0 + r * Math.sin(Math.toRadians(angle)));
            pointPs[i] = point;
        }
        return pointPs;
    }

    private float[] getAngles(int length) {
        int n = length;
        float[] floats = new float[n];
        for (int i = 0; i < n; i++) {
            PointP point = new PointP();
            float angle = i * (360 / n);
            floats[i] = angle;
        }
        return floats;
    }

    public void spin() {
        Random random = new Random();
        int randomNumber = random.nextInt(3000);
        mNewAngle = randomNumber + mLastAngle + MINIMUM_ANGLE;
        RotateAnimation rotate = new RotateAnimation(mLastAngle, mNewAngle, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setInterpolator(new AccelerateDecelerateInterpolator());
        rotate.setFillAfter(true);
        rotate.setDuration(7000);
        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showSelectedItem = true;
                invalidate();
                moveToCenter();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        showSelectedItem = false;
        startAnimation(rotate);
        mLastAngle = mNewAngle;
    }

    private void moveToCenter() {
        int item = getSelectedItem();
        float[] angles = getAngleItem(item);
        float startAngle = angles[0] + multiRatio * 360;
        float endAngle = angles[1] + multiRatio * 360;
        int centerAngle = (int) (angles[0] + ((angles[1] - angles[0]) / 2)) + multiRatio * 360;
        ;

        RotateAnimation rotate = new RotateAnimation(mLastAngle, centerAngle, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setInterpolator(new AccelerateDecelerateInterpolator());
        rotate.setFillAfter(true);
        rotate.setDuration(1000);
        startAnimation(rotate);
        mLastAngle = centerAngle;
    }

    private float[] getAngleItem(int item) {
        float unit = ((float) 360 / (float) (mImageList.length));

        float endAngle = item * unit;
        float startAngle = endAngle - unit;
        return new float[]{startAngle, endAngle};
    }

    public int getSelectedItem() {

        float unit = ((float) 360 / (float) (mImageList.length));
        if (getAngle() <= unit)
            return 1;
        int item = (int) (getAngle() / unit);
        int rem = (int) (getAngle() % unit);
        if (rem != 0)
            item = item + 1;
        return item;
    }

    public float getAngle() {
        return calculateAngle(mLastAngle);

    }

    private float calculateAngle(float angle) {
        multiRatio = 0;
        while (angle > 360) {
            angle -= 360;
            multiRatio++;
        }
        return angle;
    }

    private class PointP {
        float x;
        float y;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredWidth = mDesiredWidth;//(int)mViewBounds.right;
        int desiredHeight = mDesiredHeight;//(int)mViewBounds.bottom;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }
/*According to our requirements*/
        height = width;

        //MUST CALL THIS
        setMeasuredDimension(width, height);
        mViewBounds.right = width;
        mViewBounds.bottom = height;
        // updateView();
        invalidate();
    }


}