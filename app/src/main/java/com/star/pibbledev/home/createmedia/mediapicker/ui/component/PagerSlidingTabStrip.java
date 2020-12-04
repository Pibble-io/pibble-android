/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.star.pibbledev.home.createmedia.mediapicker.ui.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.pibbledev.R;
import com.star.pibbledev.services.global.Constants;

import java.util.Locale;


public class PagerSlidingTabStrip extends HorizontalScrollView {

    // @formatter:off
    private static final int[] ATTRS = new int[]{
            android.R.attr.textSize,
            android.R.attr.textColor
    };
    // @formatter:on
    private OnPageChangeListener mDelegatePageListener;
    private LinearLayout.LayoutParams mDefaultTabLayoutParams;
    private LinearLayout.LayoutParams mExpandedTabLayoutParams;
    private LinearLayout mTabsContainer;
    private ViewPager mViewPager;
    private int mTabCount;
    private int mCurrentPosition = 0;
    private float mCurrentPositionOffset = 0f;
    private Paint mRectPaint;
    private Paint mDividerPaint;
    private int mIndicatorColor = 0xFF666666;
    private int mUnderlineColor = 0x1A000000;
    private int mDividerColor = 0x1A000000;
    private boolean mIsShouldExpand = false;
    private boolean mIsTextAllCaps = true;
    private int mScrollOffset = 52;
    private int mIndicatorHeight = 8;
    private int mUnderlineHeight = 2;
    private int mDividerPadding = 12;
    private int mTabPadding = 24;
    private int mDividerWidth = 1;
    private int mTabTextSize = 12;
    private int mTabTextColor = android.R.color.white;
    private int mTabDeactivateTextColor = android.R.color.black;
    private Typeface mTabTypeface = null;
    private int mLastScrollX = 0;
    private int mTabBackgroundResId;
    private Locale mLocale;
    private boolean mIsTabSwitch = false;
    private boolean mIsSingleLineText = true;

    public PagerSlidingTabStrip(Context context) {
        this(context, null);
    }

    public PagerSlidingTabStrip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagerSlidingTabStrip(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setFillViewport(true);
        setWillNotDraw(false);

        mTabsContainer = new LinearLayout(context);
        mTabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        mTabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(mTabsContainer);

        DisplayMetrics dm = getResources().getDisplayMetrics();

        mScrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mScrollOffset, dm);
        mIndicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mIndicatorHeight, dm);
        mUnderlineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mUnderlineHeight, dm);
        mDividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mDividerPadding, dm);
        mTabPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mTabPadding, dm);
        mDividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mDividerWidth, dm);
        mTabTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTabTextSize, dm);

        // get system attrs (android:textSize and android:textColor)
        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
        mTabTextSize = a.getDimensionPixelSize(0, mTabTextSize);
        mTabTextColor = a.getColor(1, mTabTextColor);
        a.recycle();

        // get custom attrs

        a = context.obtainStyledAttributes(attrs, R.styleable.PagerSlidingTabStrip);
        mIndicatorColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsIndicatorColor, mIndicatorColor);
        mUnderlineColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsUnderlineColor, mUnderlineColor);
        mDividerColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsDividerColor, mDividerColor);
        mIndicatorHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsIndicatorHeight, mIndicatorHeight);
        mUnderlineHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsUnderlineHeight, mUnderlineHeight);
        mDividerPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsDividerPadding, mDividerPadding);
        mTabPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsTabPaddingLeftRight, mTabPadding);
        mTabBackgroundResId = a.getResourceId(R.styleable.PagerSlidingTabStrip_pstsTabBackground, mTabBackgroundResId);
        mIsShouldExpand = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsShouldExpand, mIsShouldExpand);
        mScrollOffset = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsScrollOffset, mScrollOffset);
        mIsTextAllCaps = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsTextAllCaps, mIsTextAllCaps);
        mIsTabSwitch = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsTabSwitch, mIsTabSwitch);
        mTabTextColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsActivateTextColor, mTabTextColor);
        mTabDeactivateTextColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsDeactivateTextColor, mTabDeactivateTextColor);
        mIsSingleLineText = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsSingleLine, mIsSingleLineText);
        a.recycle();

        mRectPaint = new Paint();
        mRectPaint.setAntiAlias(true);
        mRectPaint.setStyle(Style.FILL);

        mDividerPaint = new Paint();
        mDividerPaint.setAntiAlias(true);
        mDividerPaint.setStrokeWidth(mDividerWidth);

        mDefaultTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        mExpandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);

        if (mLocale == null) {
            mLocale = getResources().getConfiguration().locale;
        }
    }

    public void setViewPager(ViewPager pager) {
        this.mViewPager = pager;
        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have mAdapter instance.");
        }
        PageListener mPageListener = new PageListener();
        pager.addOnPageChangeListener(mPageListener);
        notifyDataSetChanged();
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.mDelegatePageListener = listener;
    }

    public void notifyDataSetChanged() {
        mTabsContainer.removeAllViews();
        mTabCount = mViewPager.getAdapter().getCount();
        for (int i = 0; i < mTabCount; i++) {
            if (mViewPager.getAdapter() instanceof IconTabProvider) {
                addIconTab(i, ((IconTabProvider) mViewPager.getAdapter()).getPageIconResId(i));
            } else {
//                addTextTab(i, mViewPager.getAdapter().getPageTitle(i).toString());
                addTextTab(i, getContext().getString(Constants.mediaPickerTitle[i]));
            }

        }
        updateTabStyles();
        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @SuppressLint("NewApi")
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                mCurrentPosition = mViewPager.getCurrentItem();
                scrollToChild(mCurrentPosition, 0);
            }
        });
    }

    private void addTextTab(final int position, String title) {
        TextView tab = new TextView(getContext());
        tab.setText(title);
        tab.setGravity(Gravity.CENTER);
        if (mIsSingleLineText) {
            tab.setSingleLine();
        }
        addTab(position, tab);
    }

    private void addIconTab(final int position, int resId) {
        ImageButton tab = new ImageButton(getContext());
        tab.setImageResource(resId);
        addTab(position, tab);
    }

    private void addTab(final int position, View tab) {
        tab.setFocusable(true);
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(position, false);
            }
        });
        tab.setPadding(mTabPadding, 0, mTabPadding, 0);
        mTabsContainer.addView(tab, position, mIsShouldExpand ? mExpandedTabLayoutParams : mDefaultTabLayoutParams);
    }

    private void updateTabStyles() {
        for (int i = 0; i < mTabCount; i++) {
            View v = mTabsContainer.getChildAt(i);
            int transparentColorId = R.color.transparent;
            v.setBackgroundResource(mIsTabSwitch ? mTabBackgroundResId : transparentColorId);
            if (v instanceof TextView) {
                TextView tab = (TextView) v;
                tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTabTextSize);
                int mTabTypefaceStyle = Typeface.BOLD;
                tab.setTypeface(mTabTypeface, mTabTypefaceStyle);
                tab.setTextColor(mIsTabSwitch && i != 0 ? mTabDeactivateTextColor : mTabTextColor);
                // setAllCaps() is only available from API 14, so the upper case is made manually if we are on a
                // pre-ICS-build
                if (mIsTextAllCaps) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        tab.setAllCaps(true);
                    } else {
                        tab.setText(tab.getText().toString().toUpperCase(mLocale));
                    }
                }
                tab.setSelected(mIsTabSwitch && i == 0);
            } else if (v instanceof ImageButton) {
                ImageButton tab = (ImageButton) v;
                tab.setSelected(mIsTabSwitch && i == 0);
            }
        }
    }

    public void updateActivateTab(final int position) {
        for (int i = 0; i < mTabCount; i++) {
            View v = mTabsContainer.getChildAt(i);
            if (v instanceof TextView) {
                TextView tab = (TextView) v;
                tab.setTextColor(position == i ? mTabTextColor : mTabDeactivateTextColor);
                tab.setSelected(position == i);
            } else {
                v.setSelected(position == i);
            }
        }
    }

    private void scrollToChild(int position, int offset) {
        if (mTabCount == 0) {
            return;
        }
        int newScrollX = mTabsContainer.getChildAt(position).getLeft() + offset;

        if (position > 0 || offset > 0) {
            newScrollX -= mScrollOffset;
        }
        if (newScrollX != mLastScrollX) {
            mLastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode() || mTabCount == 0) {
            return;
        }

        final int height = getHeight();

        // draw indicator line
        mRectPaint.setColor(mIndicatorColor);

        // default: line below current tab
        View currentTab = mTabsContainer.getChildAt(mCurrentPosition);
        float lineLeft = currentTab.getLeft();
        float lineRight = currentTab.getRight();

        // if there is an offset, start interpolating left and right coordinates between current and next tab
        if (mCurrentPositionOffset > 0f && mCurrentPosition < mTabCount - 1) {
            View nextTab = mTabsContainer.getChildAt(mCurrentPosition + 1);
            final float nextTabLeft = nextTab.getLeft();
            final float nextTabRight = nextTab.getRight();
            lineLeft = (mCurrentPositionOffset * nextTabLeft + (1f - mCurrentPositionOffset) * lineLeft);
            lineRight = (mCurrentPositionOffset * nextTabRight + (1f - mCurrentPositionOffset) * lineRight);
        }

        canvas.drawRect(lineLeft, height - mIndicatorHeight, lineRight, height, mRectPaint);

        // draw underline
        mRectPaint.setColor(mUnderlineColor);
        canvas.drawRect(0, height - mUnderlineHeight, mTabsContainer.getWidth(), height, mRectPaint);

        // draw divider
        mDividerPaint.setColor(mDividerColor);
        for (int i = 0; i < mTabCount - 1; i++) {
            View tab = mTabsContainer.getChildAt(i);
            canvas.drawLine(tab.getRight(), mDividerPadding, tab.getRight(), height - mDividerPadding, mDividerPaint);
        }
    }

    public int getTextSize() {
        return mTabTextSize;
    }

    public void setTextSize(int textSizePx) {
        this.mTabTextSize = textSizePx;
        updateTabStyles();
    }

    public int getTextColor() {
        return mTabTextColor;
    }

    public void setTextColor(int textColor) {
        this.mTabTextColor = textColor;
        updateTabStyles();
    }

    public void setTabBackground(int resId) {
        this.mTabBackgroundResId = resId;
    }

    public void setIndicatorColor(@ColorRes int colorRes) {
        mIndicatorColor = ContextCompat.getColor(getContext(), colorRes);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mCurrentPosition = savedState.currentPosition;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPosition = mCurrentPosition;
        return savedState;
    }

    public interface IconTabProvider {
        int getPageIconResId(int position);
    }

    static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        int currentPosition;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPosition);
        }
    }

    private class PageListener implements OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            mCurrentPosition = position;
            mCurrentPositionOffset = positionOffset;
            scrollToChild(position, (int) (positionOffset * mTabsContainer.getChildAt(position).getWidth()));
            invalidate();
            if (mDelegatePageListener != null) {
                mDelegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                scrollToChild(mViewPager.getCurrentItem(), 0);
            }
            if (mDelegatePageListener != null) {
                mDelegatePageListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (mIsTabSwitch) {
                updateActivateTab(position);
            }
            if (mDelegatePageListener != null) {
                mDelegatePageListener.onPageSelected(position);
            }
        }

    }
}
