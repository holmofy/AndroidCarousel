package cn.hufeifei.carousel;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * ViewPager页面标题
 * 调用{@link #attachViewPager(ViewPager)}方法可以关联ViewPager
 * 这个标题内容来自于ViewPager的适配器中的方法{@link PagerAdapter#getPageTitle(int)}
 * 因为这个类继承自{@link TextView}，因此你可以像设置TextView的属性一样设置它的属性
 * Used to display the title of the {@link ViewPager}.
 * Calls the method {@link #attachViewPager(ViewPager)} can be associated ViewPager.
 * The title from each page is supplied by the method {@link PagerAdapter#getPageTitle(int)}
 * in the adapter supplied to the ViewPager.
 * Because it is inherited from {@link TextView} so you can take it as a TextView to use.
 * Created by Holmofy on 2017/2/26.
 */

public class PagerTitle extends TextView {
    private ViewPager mViewPager;
    private TitleOnPageChangeListener mPageChangeListener;

    public PagerTitle(Context context) {
        super(context, null);
    }

    public PagerTitle(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagerTitle(Context context, AttributeSet attrs, int defStyleAttr) {
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
                mPageChangeListener = new TitleOnPageChangeListener();
            }
            viewPager.addOnPageChangeListener(mPageChangeListener);
            //设置当前文本内容
            this.setText(adapter.getPageTitle(viewPager.getCurrentItem()));
        } else {
            //文本置空
            this.setText("");
        }
    }

    private class TitleOnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            if (mViewPager != null) {
                final PagerAdapter adapter = mViewPager.getAdapter();
                PagerTitle.this.setText(adapter.getPageTitle(position));
            }
        }
    }
}
