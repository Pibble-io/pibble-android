package com.star.pibbledev.wallet.send;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.star.pibbledev.R;

import me.dm7.barcodescanner.core.IViewFinder;

public class CustomZXingScannerView extends View implements IViewFinder {
    private static final String TAG = "ViewFinderView";

    private Rect mFramingRect;

    private static final float PORTRAIT_WIDTH_RATIO = 6f/8;
    private static final float PORTRAIT_WIDTH_HEIGHT_RATIO = 0.75f;

    private static final float LANDSCAPE_HEIGHT_RATIO = 5f/8;
    private static final float LANDSCAPE_WIDTH_HEIGHT_RATIO = 1.4f;
    private static final int MIN_DIMENSION_DIFF = 50;

    private static final float SQUARE_DIMENSION_RATIO = 5f/8;

    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private int scannerAlpha;
    private static final int POINT_SIZE = 10;
    private static final long ANIMATION_DELAY = 80l;
    int width_screen;
    int height_screen;
    private final int mDefaultLaserColor = getResources().getColor(R.color.viewfinder_laser);
    private final int mDefaultMaskColor = getResources().getColor(R.color.viewfinder_mask);
    private final int mDefaultBorderColor = getResources().getColor(R.color.viewfinder_border);
    private final int mDefaultBorderStrokeWidth = 12;
    private final int mDefaultBorderLineLength = 90;

    protected Paint mLaserPaint;
    protected Paint mFinderMaskPaint;
    protected Paint mBorderPaint;
    protected int mBorderLineLength;
    protected boolean mSquareViewFinder;

    public CustomZXingScannerView(Context context) {
        super(context);
        init();
    }

    public CustomZXingScannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        //set up laser paint
        mLaserPaint = new Paint();
        mLaserPaint.setColor(mDefaultLaserColor);
        mLaserPaint.setStyle(Paint.Style.FILL);

        //finder mask paint
        mFinderMaskPaint = new Paint();
        mFinderMaskPaint.setColor(mDefaultMaskColor);

        //border paint
        mBorderPaint = new Paint();
        mBorderPaint.setColor(mDefaultBorderColor);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mDefaultBorderStrokeWidth);

        mBorderLineLength = mDefaultBorderLineLength;

//        setBorderLineLength(90);
//        setBorderStrokeWidth(12);
    }

    public void setLaserColor(int laserColor) {
        mLaserPaint.setColor(laserColor);
    }
    public void setMaskColor(int maskColor) {
        mFinderMaskPaint.setColor(maskColor);
    }
    public void setBorderColor(int borderColor) {
        mBorderPaint.setColor(borderColor);
    }
    public void setBorderStrokeWidth(int borderStrokeWidth) {
        mBorderPaint.setStrokeWidth(borderStrokeWidth);
    }
    public void setBorderLineLength(int borderLineLength) {
        mBorderLineLength = borderLineLength;
    }

    // TODO: Need a better way to configure this. Revisit when working on 2.0
    public void setSquareViewFinder(boolean set) {
        mSquareViewFinder = set;
    }

    public void setupViewFinder() {
        updateFramingRect();
        invalidate();
    }

    public Rect getFramingRect() {
        return mFramingRect;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if(getFramingRect() == null) {
            return;
        }

        drawViewFinderMask(canvas);
        drawViewFinderBorder(canvas);
        drawLaser(canvas);
    }

    public void drawViewFinderMask(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        Rect framingRect = getFramingRect();

        canvas.drawRect(0, 0, width, framingRect.top, mFinderMaskPaint);
        canvas.drawRect(0, framingRect.top, framingRect.left, framingRect.bottom + 1, mFinderMaskPaint);
        canvas.drawRect(framingRect.right + 1, framingRect.top, width, framingRect.bottom + 1, mFinderMaskPaint);
        canvas.drawRect(0, framingRect.bottom + 1, width, height, mFinderMaskPaint);
    }

    public void drawViewFinderBorder(Canvas canvas) {
        Rect framingRect = getFramingRect();
        int width = mDefaultBorderStrokeWidth/2;
        canvas.drawLine(framingRect.left - 1, framingRect.top - 1 - width, framingRect.left - 1 , framingRect.top - 1 + mBorderLineLength - width, mBorderPaint);
        canvas.drawLine(framingRect.left - 1 - width , framingRect.top - 1, framingRect.left - 1 + mBorderLineLength - width, framingRect.top - 1, mBorderPaint);

        canvas.drawLine(framingRect.left - 1, framingRect.bottom + 1 + width, framingRect.left - 1, framingRect.bottom + 1 - mBorderLineLength + width, mBorderPaint);
        canvas.drawLine(framingRect.left - 1 - width, framingRect.bottom + 1, framingRect.left - 1 + mBorderLineLength - width, framingRect.bottom + 1, mBorderPaint);

        canvas.drawLine(framingRect.right + 1, framingRect.top - 1 - width, framingRect.right + 1, framingRect.top - 1 + mBorderLineLength - width, mBorderPaint);
        canvas.drawLine(framingRect.right + 1 + width, framingRect.top - 1 , framingRect.right + 1 - mBorderLineLength + width, framingRect.top - 1 , mBorderPaint);

        canvas.drawLine(framingRect.right + 1, framingRect.bottom + 1 + width, framingRect.right + 1, framingRect.bottom + 1 - mBorderLineLength + width, mBorderPaint);
        canvas.drawLine(framingRect.right + 1 + width, framingRect.bottom + 1, framingRect.right + 1 - mBorderLineLength + width, framingRect.bottom + 1, mBorderPaint);
    }

    public void drawLaser(Canvas canvas) {

    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        width_screen = xNew;
        height_screen = yNew;
        updateFramingRect();
    }

    public synchronized void updateFramingRect() {
        int scanner_width = width_screen*2/3;
        int left = (width_screen-scanner_width) / 2;
        int top = (height_screen - scanner_width) / 2;
        int right = width_screen - left;
        int bottom = height_screen - top;
        mFramingRect = new Rect(left, top, right, bottom);
    }
}
