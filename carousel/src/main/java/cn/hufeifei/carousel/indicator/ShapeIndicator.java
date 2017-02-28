package cn.hufeifei.carousel.indicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import cn.hufeifei.carousel.R;

/**
 * 圆形指示器，用于指明ViewPager当前页所在的位置
 * used to indicate position of the ViewPager's current page
 * <p>
 * Created by Holmofy on 2017/2/26.
 */

public class ShapeIndicator extends LinearLayout implements Indicator {
    private Context mContext;

    private int mCircleWidth;
    private int mCircleHeight;
    private int mCircleGap;
    private boolean mCircleClickable;

    private int mSelectIndex;

    private ViewPager mViewPager;
    private IndicatorOnPageChangeListener mPageChangeListener;
    private int mSelectedDrawable;
    private int mUnSelectedDrawable;

    public ShapeIndicator(Context context) {
        this(context, null);
    }

    public ShapeIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initFromAttributes(context, attrs);
    }

    private void initFromAttributes(Context context, AttributeSet attrs) {
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShapeIndicator);
        mCircleWidth = a.getDimensionPixelSize(R.styleable.ShapeIndicator_item_width, 0);
        mCircleHeight = a.getDimensionPixelSize(R.styleable.ShapeIndicator_item_height, 0);
        mCircleGap = a.getDimensionPixelSize(R.styleable.ShapeIndicator_item_gap, mCircleWidth);
        mCircleClickable = a.getBoolean(R.styleable.ShapeIndicator_item_clickable, true);
        mSelectedDrawable = a.getResourceId(R.styleable.ShapeIndicator_selected_drawable, R.drawable.indicator_default_selected_drawable);
        mUnSelectedDrawable = a.getResourceId(R.styleable.ShapeIndicator_unselected_drawable, R.drawable.indicator_default_unselected_drawable);
        a.recycle();
    }

    /**
     * 关联ViewPager
     *
     * @param viewPager 需要关联的ViewPager，如果为空则解除之前ViewPager的关联
     */
    public void attachViewPager(@Nullable ViewPager viewPager) {
        if (mViewPager != null && mPageChangeListener != null) {
            // If we've already been setup with a ViewPager, remove us from it
            mViewPager.removeOnPageChangeListener(mPageChangeListener);
        }
        mViewPager = viewPager;
        if (viewPager != null) {
            final PagerAdapter adapter = viewPager.getAdapter();
            if (adapter == null) {
                throw new IllegalArgumentException("ViewPager does not have a PagerAdapter set");
            }
            // 添加ViewPager的页面滚动的事件监听
            if (mPageChangeListener == null) {
                mPageChangeListener = new IndicatorOnPageChangeListener();
            }
            viewPager.addOnPageChangeListener(mPageChangeListener);
            updateIndicator(adapter.getCount());
        } else {
            updateIndicator(0);
        }
    }

    private void updateIndicator(final int count) {
        if (this.getChildCount() != 0) {
            this.removeAllViews();
        }
        for (int i = 0; i < count; i++) {
            View item = new View(mContext);
            item.setTag(i);
            item.setClickable(mCircleClickable);
            if (mCircleClickable) {
                item.setOnClickListener(new CircleClickListener(i));
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mCircleWidth, mCircleHeight);
            if (i == 0) {
                //默认先选中第一个
                mSelectIndex = 0;
                item.setBackgroundResource(mSelectedDrawable);
            } else {
                //既可以是水平方向的也可以是垂直方向的
                if (this.getOrientation() == HORIZONTAL) {
                    params.leftMargin = mCircleGap;
                } else {
                    params.topMargin = mCircleGap;
                }
                item.setBackgroundResource(mUnSelectedDrawable);
            }
            this.addView(item, params);
        }
    }

    private void select(int index) {
        if (mSelectIndex == index) return;
        findViewWithTag(mSelectIndex).setBackgroundResource(mUnSelectedDrawable);
        findViewWithTag(index).setBackgroundResource(mSelectedDrawable);
        mSelectIndex = index;
    }

    private class IndicatorOnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            ShapeIndicator.this.select(position);
        }
    }

    private class CircleClickListener implements View.OnClickListener {
        private final int mIndex;

        CircleClickListener(int index) {
            mIndex = index;
        }

        @Override
        public void onClick(View v) {
            if (mViewPager != null) {
                mViewPager.setCurrentItem(mIndex);
            }
        }
    }
}
