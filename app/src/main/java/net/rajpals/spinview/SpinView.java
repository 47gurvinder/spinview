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
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;

import net.rajpals.spinview.enums.Direction;

import java.util.Random;

/**
 * Author: Gurwinder Singh
 * Email: 47gurvinder@gmail.com
 * 07-07-17
 **/
public class SpinView extends View {


    private static final long DEFAULT_SPIN_TIME = 7000;
    private static final int RANDOM_ANGLE_LIMIT = 3000;
    Paint linePaint;
    private int[] mImageList = new int[]{};
    private RectF mViewBounds = new RectF();
    private int mDesiredHeight = 300;
    private int mDesiredWidth = 300;
    private int mCenterCircleRadius = 24;

    private int mLastAngle = 0;
    private static final int MINIMUM_ANGLE = 360 * 2;
    private boolean showSelectedItem;
    private int mStartAngle = -90;

    private String LOGTAG = "SpinView";
    private GestureDetector mGestureDetector;

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
        mGestureDetector = new GestureDetector(getContext(), new SwipeGestureDetector());

    }

    private void updateView() {
        setLayoutParams(new ViewGroup.LayoutParams((int) mViewBounds.right, (int) mViewBounds.bottom));

    }


    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        mViewBounds.right = width;
        mViewBounds.bottom = width;
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
        int randomNumber = random.nextInt(RANDOM_ANGLE_LIMIT);
        int angle = randomNumber + mLastAngle + MINIMUM_ANGLE;
        animateSpinner(angle, DEFAULT_SPIN_TIME, new AccelerateDecelerateInterpolator());

    }

    private void adjustSpinnerCenter() {
        float unit = getUnitAngle();


        int divider = (int) ((float) mLastAngle / unit);

        float startAngle = divider * unit;
        float endAngle = startAngle + Math.signum(startAngle) * unit;
        int centerAngle = (int) (startAngle + ((endAngle - startAngle) / 2));


        RotateAnimation rotate = new RotateAnimation(mLastAngle, centerAngle, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setInterpolator(new AccelerateDecelerateInterpolator());
        rotate.setFillAfter(true);
        rotate.setDuration(1000);
        startAnimation(rotate);
        mLastAngle = centerAngle;
    }

    private float getUnitAngle() {
        return ((float) 360 / (float) (mImageList.length));
    }

    private float[] getAngleItem(int item) {
        float unit = getUnitAngle();
        float endAngle = item * unit;
        float startAngle = endAngle - unit;
        return new float[]{startAngle, endAngle};
    }

    public int getSelectedItem() {

        float unit = getUnitAngle();

        if ((getAngle() < unit && getAngle() >= 0))
            return 1;
        else if ((getAngle() <= 0 && getAngle() > unit))
            return mImageList.length;
        int item = (int) (getAngle() / unit);
        int rem = (int) (getAngle() % unit);
        if (rem != 0 && rem > 0)
            item = item + (int) Math.signum(item);
        item = resolveItem(item);
        return item;
    }

    private int resolveItem(int item) {
        if (item < 0) {
            return mImageList.length + item;
        }
        return item;
    }

    public float getAngle() {
        return calculateAngle(mLastAngle);

    }

    private float calculateAngle(float angle) {
        if (angle < 0) {
            while (angle < -360) {
                angle += 360;
            }
        } else {
            while (angle > 360) {
                angle -= 360;
            }
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }


    private class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            Direction direction = getSlope(e1.getX(), e1.getY(), e2.getX(), e2.getY());
            float distance = getDistance(direction, e1, e2);
            float velocity = 0;
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            int rotationDirection = 1;
            switch (direction) {
                case TOP:
                    Log.d(LOGTAG, "top");
                    velocity = velocityY;
                    if (e1.getX() < centerX)
                        rotationDirection = 1;
                    else
                        rotationDirection = -1;
                    updateSpinner(rotationDirection, distance, velocity);
                    return true;
                case LEFT:


                    velocity = velocityX;
                    if (e1.getY() < centerY)
                        rotationDirection = -1;
                    else
                        rotationDirection = 1;
                    updateSpinner(rotationDirection, distance, velocity);

                    Log.d(LOGTAG, "left");
                    return true;
                case BOTTOM:
                    velocity = velocityY;
                    if (e1.getX() < centerX)
                        rotationDirection = -1;
                    else
                        rotationDirection = 1;
                    updateSpinner(rotationDirection, distance, velocity);
                    Log.d(LOGTAG, "down");
                    return true;
                case RIGHT:
                    velocity = velocityX;
                    if (e1.getY() < centerY)
                        rotationDirection = 1;
                    else
                        rotationDirection = -1;
                    updateSpinner(rotationDirection, distance, velocity);

                    Log.d(LOGTAG, "right");
                    return true;
            }
            return false;
        }

        private Direction getSlope(float x1, float y1, float x2, float y2) {
            Double angle = Math.toDegrees(Math.atan2(y1 - y2, x2 - x1));
            if (angle > 45 && angle <= 135)
                // top
                return Direction.TOP;
            if (angle >= 135 && angle < 180 || angle < -135 && angle > -180)
                // left
                return Direction.LEFT;
            if (angle < -45 && angle >= -135)
                // down
                return Direction.BOTTOM;
            if (angle > -45 && angle <= 45)
                // right
                return Direction.RIGHT;
            return Direction.UNDEFINED;
        }

    }

    private void updateSpinner(int rotationDirection, float distance, float velocity) {
        long time = (long) (distance * 10);
        velocity = (int) velocity / 5;
        Interpolator interpolator = new DecelerateInterpolator();
        velocity = resolveRotationDirection(rotationDirection, velocity);
        int angle = (int) velocity + mLastAngle;
        animateSpinner(angle, time, interpolator);
        Log.e("updateSpinner", "dis: " + distance + " v:" + velocity + " time: " + time);
//        Toast.makeText(getContext(), "dis: " + distance + " v: " + velocity + " time: " + time, Toast.LENGTH_SHORT).show();
    }

    private float resolveRotationDirection(int rotationDirection, float angle) {
        angle = Math.abs(angle);
        return angle * rotationDirection;


    }

    private void animateSpinner(int angle, long duration, Interpolator interpolator) {
        RotateAnimation rotate = new RotateAnimation(mLastAngle, angle, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setInterpolator(interpolator);
        rotate.setFillAfter(true);
        rotate.setDuration(duration);
        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showSelectedItem = true;
                invalidate();
                adjustSpinnerCenter();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        showSelectedItem = false;
        startAnimation(rotate);
        mLastAngle = angle;
    }

    private float getDistance(Direction direction, MotionEvent e1, MotionEvent e2) {
        float distance = 0;
        switch (direction) {
            case TOP:
                distance = Math.abs(e1.getY() - e2.getY());
                break;
            case LEFT:
                distance = Math.abs(e1.getX() - e2.getX());
                break;
            case BOTTOM:
                distance = Math.abs(e1.getY() - e2.getY());
                break;
            case RIGHT:
                distance = Math.abs(e1.getX() - e2.getX());
                break;
        }
        return distance;
    }

    /**
     * Finds the angle between two points in the plane (x1,y1) and (x2, y2)
     * The angle is measured with 0/360 being the X-axis to the right, angles
     * increase counter clockwise.
     *
     * @param x1 the x position of the first point
     * @param y1 the y position of the first point
     * @param x2 the x position of the second point
     * @param y2 the y position of the second point
     * @return the angle between two points
     */
    public double getAngle(float x1, float y1, float x2, float y2) {

        double rad = Math.atan2(y1 - y2, x2 - x1) + Math.PI;
        return (rad * 180 / Math.PI + 180) % 360;
    }

}