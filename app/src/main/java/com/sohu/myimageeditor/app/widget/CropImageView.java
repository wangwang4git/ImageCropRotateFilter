package com.sohu.myimageeditor.app.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by wangwang on 14-3-12.
 */
public class CropImageView extends ImageView {

    private static final String TAG = CropImageView.class.getSimpleName();

    private static final int PRESS_LT = 0;
    private static final int PRESS_RT = 1;
    private static final int PRESS_RB = 2;
    private static final int PRESS_LB = 3;
    private static final int REMAIN_AREA_ALPHA = 50 * 255 / 100;
    private static final int PRESS_AREA_RADIUS = 36;

    private Bitmap mOriginalBitmap;
    private Rect mOriginalArea;
    private Rect mActualArea;
    private Rect mChooseArea;
    private Rect mPressLTArea;
    private Rect mPressRTArea;
    private Rect mPressRBArea;
    private Rect mPressLBArea;
    private Rect mRemainLeftArea;
    private Rect mRemainRightArea;
    private Rect mRemainTopArea;
    private Rect mRemainBottomArea;

    private Paint mChooseAreaPaint;
    private Paint mRemainAreaPaint;

    private boolean mFirstDrawFlag;
    private boolean mTouchCorrectFlag;
    private int mPressAreaFlag;
    private int mX;
    private int mY;

    public CropImageView(Context context) {
        super(context);
        init();
    }

    public CropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mActualArea = new Rect();
        mChooseArea = new Rect();
        mPressLTArea = new Rect();
        mPressRTArea = new Rect();
        mPressRBArea = new Rect();
        mPressLBArea = new Rect();
        mRemainLeftArea = new Rect();
        mRemainRightArea = new Rect();
        mRemainTopArea = new Rect();
        mRemainBottomArea = new Rect();

        mChooseAreaPaint = new Paint();
        mRemainAreaPaint = new Paint();
        mRemainAreaPaint.setStyle(Paint.Style.FILL);
        mRemainAreaPaint.setAlpha(REMAIN_AREA_ALPHA);

        mFirstDrawFlag = true;
        mTouchCorrectFlag = false;
    }

    public void setBitmap(Bitmap bitmap) {
        mOriginalArea = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        mOriginalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        setImageBitmap(mOriginalBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mFirstDrawFlag) {
            mFirstDrawFlag = false;
            imageScale();
        }

        setRemainArea();
        canvas.drawRect(mRemainLeftArea, mRemainAreaPaint);
        canvas.drawRect(mRemainRightArea, mRemainAreaPaint);
        canvas.drawRect(mRemainTopArea, mRemainAreaPaint);
        canvas.drawRect(mRemainBottomArea, mRemainAreaPaint);

        mChooseAreaPaint.setColor(Color.WHITE);
        mChooseAreaPaint.setStrokeWidth(5.0f);
        mChooseAreaPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(mChooseArea, mChooseAreaPaint);

        mChooseAreaPaint.setColor(Color.argb(153, 255, 255, 255));
        mChooseAreaPaint.setStrokeWidth(2.0f);
        int point1x = (mChooseArea.right - mChooseArea.left) / 3 + mChooseArea.left;
        int point1y = mChooseArea.top;
        int point2x = (mChooseArea.right - mChooseArea.left) * 2 / 3 + mChooseArea.left;
        int point2y = mChooseArea.top;
        int point3x = mChooseArea.right;
        int point3y = (mChooseArea.bottom - mChooseArea.top) / 3 + mChooseArea.top;
        int point4x = mChooseArea.right;
        int point4y = (mChooseArea.bottom - mChooseArea.top) * 2 / 3 + mChooseArea.top;
        int point5x = point2x;
        int point5y = mChooseArea.bottom;
        int point6x = point1x;
        int point6y = mChooseArea.bottom;
        int point7x = mChooseArea.left;
        int point7y = point4y;
        int point8x = mChooseArea.left;
        int point8y = point3y;
        canvas.drawLine(point1x, point1y, point6x, point6y, mChooseAreaPaint);
        canvas.drawLine(point2x, point2y, point5x, point5y, mChooseAreaPaint);
        canvas.drawLine(point8x, point8y, point3x, point3y, mChooseAreaPaint);
        canvas.drawLine(point7x, point7y, point4x, point4y, mChooseAreaPaint);

        mChooseAreaPaint.setColor(Color.WHITE);
        mChooseAreaPaint.setStyle(Paint.Style.FILL);
        setPressArea();
        canvas.drawOval(new RectF(mPressLTArea), mChooseAreaPaint);
        canvas.drawOval(new RectF(mPressRTArea), mChooseAreaPaint);
        canvas.drawOval(new RectF(mPressRBArea), mChooseAreaPaint);
        canvas.drawOval(new RectF(mPressLBArea), mChooseAreaPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mX = (int) event.getX();
            mY = (int) event.getY();
            if (isInPressArea(mX, mY) || isInChooseArea(mX, mY)) {
                mTouchCorrectFlag = true;
                return true;
            }
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE && mTouchCorrectFlag) {
            if (isMovePressArea((int) event.getX(), (int) event.getY())) {
                invalidate();
                mX = (int) event.getX();
                mY = (int) event.getY();
                return true;
            }
            // mChooseArea == mActualArea
            if (mChooseArea.contains(mActualArea)) {
                return true;
            } else {
                moveChooseArea((int) event.getX() - mX, (int) event.getY() - mY);
                invalidate();
                mX = (int) event.getX();
                mY = (int) event.getY();
                return true;
            }
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            mTouchCorrectFlag = false;
            mPressAreaFlag = -1;
            invalidate();
            return true;
        }

        return super.onTouchEvent(event);
    }

    private void imageScale() {
        if (mOriginalArea == null) {
            throw new IllegalStateException("setBitmap() must called before...");
        }
        RectF temp1 = new RectF(mOriginalArea);
        RectF temp2 = new RectF();
        getImageMatrix().mapRect(temp2, temp1);
        mActualArea.set((int) temp2.left, (int) temp2.top, (int) temp2.right, (int) temp2.bottom);
        mChooseArea.set(mActualArea.left + 50, mActualArea.top + 50, mActualArea.right - 50, mActualArea.bottom - 50);
    }

    private void setRemainArea() {
        mRemainLeftArea.set(mActualArea.left, mActualArea.top, mChooseArea.left, mActualArea.bottom);
        mRemainRightArea.set(mChooseArea.right, mActualArea.top, mActualArea.right, mActualArea.bottom);
        mRemainTopArea.set(mChooseArea.left, mActualArea.top, mChooseArea.right, mChooseArea.top);
        mRemainBottomArea.set(mChooseArea.left, mChooseArea.bottom, mChooseArea.right, mActualArea.bottom);
    }

    private void setPressArea() {
        mPressLTArea.set(mChooseArea.left - PRESS_AREA_RADIUS / 2,
                mChooseArea.top - PRESS_AREA_RADIUS / 2, mChooseArea.left + PRESS_AREA_RADIUS / 2, mChooseArea.top + PRESS_AREA_RADIUS / 2);
        mPressRTArea.set(mChooseArea.right - PRESS_AREA_RADIUS / 2,
                mChooseArea.top - PRESS_AREA_RADIUS / 2, mChooseArea.right + PRESS_AREA_RADIUS / 2, mChooseArea.top + PRESS_AREA_RADIUS / 2);
        mPressRBArea.set(mChooseArea.right - PRESS_AREA_RADIUS / 2,
                mChooseArea.bottom - PRESS_AREA_RADIUS / 2, mChooseArea.right + PRESS_AREA_RADIUS / 2, mChooseArea.bottom + PRESS_AREA_RADIUS / 2);
        mPressLBArea.set(mChooseArea.left - PRESS_AREA_RADIUS / 2,
                mChooseArea.bottom - PRESS_AREA_RADIUS / 2, mChooseArea.left + PRESS_AREA_RADIUS / 2, mChooseArea.bottom + PRESS_AREA_RADIUS / 2);
    }

    public boolean isInChooseArea(int x, int y) {
        return mChooseArea.contains(x, y);
    }

    public boolean isInPressArea(int x, int y) {
        Rect temp1 = new Rect(mPressLTArea.left - 5, mPressLTArea.top - 5, mPressLTArea.right + 5, mPressLTArea.bottom + 5);
        Rect temp2 = new Rect(mPressRTArea.left - 5, mPressRTArea.top - 5, mPressRTArea.right + 5, mPressRTArea.bottom + 5);
        Rect temp3 = new Rect(mPressRBArea.left - 5, mPressRBArea.top - 5, mPressRBArea.right + 5, mPressRBArea.bottom + 5);
        Rect temp4 = new Rect(mPressLBArea.left - 5, mPressLBArea.top - 5, mPressLBArea.right + 5, mPressLBArea.bottom + 5);
        if (temp1.contains(x, y)) {
            mPressAreaFlag = PRESS_LT;
            return true;
        } else if (temp2.contains(x, y)) {
            mPressAreaFlag = PRESS_RT;
            return true;
        } else if (temp3.contains(x, y)) {
            mPressAreaFlag = PRESS_RB;
            return true;
        } else if (temp4.contains(x, y)) {
            mPressAreaFlag = PRESS_LB;
            return true;
        }
        return false;
    }

    private boolean isMovePressArea(int x, int y) {
        switch (mPressAreaFlag) {
            case PRESS_LT:
                pressLTArea(x - mX, y - mY);
                break;
            case PRESS_RT:
                pressRTArea(x - mX, y - mY);
                break;
            case PRESS_RB:
                pressRBArea(x - mX, y - mY);
                break;
            case PRESS_LB:
                pressLBArea(x - mX, y - mY);
                break;
            default:
                return false;
        }
        return true;
    }

    private void pressLTArea(int x, int y) {
        int left = mChooseArea.left + x;
        int right = mChooseArea.right;
        int top = mChooseArea.top + y;
        int bottom = mChooseArea.bottom;
        if (left >= mActualArea.left && left <= mActualArea.right - PRESS_AREA_RADIUS && top >= mActualArea.top && top <= mChooseArea.bottom - PRESS_AREA_RADIUS) {
            mChooseArea.set(left, top, right, bottom);
        } else {
            if (left < mActualArea.left) {
                left = mActualArea.left;
            }
            if (top < mActualArea.top) {
                top = mActualArea.top;
            }
            if (left > mChooseArea.right - PRESS_AREA_RADIUS) {
                left = mChooseArea.right - PRESS_AREA_RADIUS;
            }
            if (top > mChooseArea.bottom - PRESS_AREA_RADIUS) {
                top = mChooseArea.bottom - PRESS_AREA_RADIUS;
            }
            mChooseArea.set(left, top, right, bottom);
        }
    }

    private void pressRTArea(int x, int y) {
        int left = mChooseArea.left;
        int right = mChooseArea.right + x;
        int top = mChooseArea.top + y;
        int bottom = mChooseArea.bottom;
        if (right <= mActualArea.right && right >= mChooseArea.left + PRESS_AREA_RADIUS && top >= mActualArea.top && top <= mChooseArea.bottom - PRESS_AREA_RADIUS) {
            mChooseArea.set(left, top, right, bottom);
        } else {
            if (right > mActualArea.right) {
                right = mActualArea.right;
            }
            if (top < mActualArea.top) {
                top = mActualArea.top;
            }
            if (right < mChooseArea.left + PRESS_AREA_RADIUS) {
                right = (mChooseArea.left + PRESS_AREA_RADIUS);
            }
            if (top > mChooseArea.bottom - PRESS_AREA_RADIUS) {
                top = (mChooseArea.bottom - PRESS_AREA_RADIUS);
            }
            mChooseArea.set(left, top, right, bottom);
        }
    }

    private void pressRBArea(int x, int y) {
        int left = mChooseArea.left;
        int right = mChooseArea.right + x;
        int top = mChooseArea.top;
        int bottom = mChooseArea.bottom + y;
        if (right <= mActualArea.right && left >= mChooseArea.left + PRESS_AREA_RADIUS && bottom <= mActualArea.bottom && bottom >= mChooseArea.top + PRESS_AREA_RADIUS) {
            mChooseArea.set(left, top, right, bottom);
        } else {
            if (right > mActualArea.right) {
                right = mActualArea.right;
            }
            if (bottom > mActualArea.bottom) {
                bottom = mActualArea.bottom;
            }
            if (right < mChooseArea.left + PRESS_AREA_RADIUS) {
                right = mChooseArea.left + PRESS_AREA_RADIUS;
            }
            if (bottom < mChooseArea.top + PRESS_AREA_RADIUS) {
                bottom = mChooseArea.top + PRESS_AREA_RADIUS;
            }
            mChooseArea.set(left, top, right, bottom);
        }
    }

    private void pressLBArea(int x, int y) {
        int left = mChooseArea.left + x;
        int right = mChooseArea.right;
        int top = mChooseArea.top;
        int bottom = mChooseArea.bottom + y;
        if (left >= mActualArea.left && left <= mChooseArea.right - PRESS_AREA_RADIUS && bottom <= mActualArea.bottom && bottom >= mChooseArea.top + PRESS_AREA_RADIUS) {
            mChooseArea.set(left, top, right, bottom);
        } else {
            if (left < mActualArea.left) {
                left = mActualArea.left;
            }
            if (bottom > mActualArea.bottom) {
                bottom = mActualArea.bottom;
            }
            if (left > mChooseArea.right - PRESS_AREA_RADIUS) {
                left = mChooseArea.right - PRESS_AREA_RADIUS;
            }
            if (bottom < mChooseArea.top + PRESS_AREA_RADIUS) {
                bottom = mChooseArea.top + PRESS_AREA_RADIUS;
            }
            mChooseArea.set(left, top, right, bottom);
        }
    }

    public void moveChooseArea(int x, int y) {
        int left = mChooseArea.left + x;
        int right = mChooseArea.right + x;
        int top = mChooseArea.top + y;
        int bottom = mChooseArea.bottom + y;
        if (mActualArea.contains(left, top, right, bottom)) {
        } else {
            if (left < mActualArea.left) {
                left = mActualArea.left;
                right = mChooseArea.right;
            }
            if (right > mActualArea.right) {
                right = mActualArea.right;
                left = mChooseArea.left;
            }
            if (top < mActualArea.top) {
                top = mActualArea.top;
                bottom = mChooseArea.bottom;
            }
            if (bottom > mActualArea.bottom) {
                bottom = mActualArea.bottom;
                top = mChooseArea.top;
            }
        }
        mChooseArea.set(left, top, right, bottom);
    }

    public Bitmap getCropBitmap() {
        float ratioWidth = mOriginalBitmap.getWidth() / (float) (mActualArea.right - mActualArea.left);
        float ratioHeight = mOriginalBitmap.getHeight() / (float) (mActualArea.bottom - mActualArea.top);
        int left = (int) ((mChooseArea.left - mActualArea.left) * ratioWidth);
        int right = (int) (left + (mChooseArea.right - mChooseArea.left) * ratioWidth);
        int top = (int) ((mChooseArea.top - mActualArea.top) * ratioHeight);
        int bottom = (int) (top + (mChooseArea.bottom - mChooseArea.top) * ratioHeight);
        return Bitmap.createBitmap(mOriginalBitmap, left, top, right - left, bottom - top);
    }

    public void cropBitmap() {
        mFirstDrawFlag = true;
        mTouchCorrectFlag = false;
        setBitmap(getCropBitmap());
    }

    public void reset() {
        mFirstDrawFlag = true;
        mTouchCorrectFlag = false;
    }

}
