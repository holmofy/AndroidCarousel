package cn.hufeifei.carousel.indicator;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 数字型ViewPager的指示器
 * Created by Holmofy on 2017/2/26.
 */

public class DigitalIndicator extends TextView implements Indicator {
    private ViewPager mViewPager;
    private IndicatorOnPageChangeListener mPageChangeListener;

    public DigitalIndicator(Context context) {
        this(context, null);
    }

    public DigitalIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DigitalIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 关联ViewPager
     * attach ViewPager
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
            //设置当前文本内容
            this.setText(viewPager.getCurrentItem() + 1 + "/" + adapter.getCount());
        } else {
            //文本置空
            this.setText("");
        }
    }

    private class IndicatorOnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            if (mViewPager != null) {
                final PagerAdapter adapter = mViewPager.getAdapter();
                DigitalIndicator.this.setText(position + 1 + "/" + adapter.getCount());
            }
        }
    }
}
