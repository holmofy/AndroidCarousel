package cn.hufeifei.carousel;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 轮播图控件
 *
 * @author holmofy
 */
public class Carousel extends ViewPager {
    private static final boolean DEBUG = false;
    private static final String TAG = "Carousel";

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SCROLL_DIRECTION_LEFT, SCROLL_DIRECTION_RIGHT})
    public @interface ScrollDirection {
    }

    public static final int SCROLL_DIRECTION_LEFT = 0;
    public static final int SCROLL_DIRECTION_RIGHT = 1;

    private static final int DEFAULT_SCROLL_DELAY = 3000;
    private static final boolean DEFAULT_AUTO_SCROLL = true;
    private static final int DEFAULT_SCROLL_DIRECTION = SCROLL_DIRECTION_LEFT;
    private static final int DEFAULT_SCROLL_DURATION = 600;

    private static final int DIRECTION_LEFT = 0x0001;
    private static final int DIRECTION_RIGHT = 0X0002;

    private LoopPagerAdapterWrapper mAdapterWrapper;
    private OnPageChangeListenerWrapper mListenerWrapper;
    private ScrollModifier mScroller;

    private int mScrollDelay;
    private boolean mIsAutoScroll;
    private int mScrollDirection;

    private Handler mHandler;

    static class Handler extends android.os.Handler {
        WeakReference<Carousel> ref;//防止内存泄漏

        Handler(Carousel instance) {
            this.ref = new WeakReference<>(instance);
        }

        @Override
        public void handleMessage(Message msg) {
            removeCallbacksAndMessages(null);
            Carousel instance = ref.get();
            if (instance != null) {
                if (msg.what == DIRECTION_LEFT) {
                    instance.setCurrentItem(instance.getCurrentItem() + 1, true);
                    sendEmptyMessageDelayed(DIRECTION_LEFT, instance.mScrollDelay);
                } else if (msg.what == DIRECTION_RIGHT) {
                    instance.setCurrentItem(instance.getCurrentItem() - 1, true);
                    sendEmptyMessageDelayed(DIRECTION_RIGHT, instance.mScrollDelay);
                }
            }
        }
    }

    public Carousel(Context context) {
        this(context, null);
    }

    public Carousel(Context context, AttributeSet attrs) {
        super(context, attrs);
        mListenerWrapper = new OnPageChangeListenerWrapper();

        super.setOnPageChangeListener(mListenerWrapper);
        modifyViewPagerScroller();
        initFromAttributes(context, attrs);
    }

    private void modifyViewPagerScroller() {
        try {
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            mScroller = new ScrollModifier(getContext());
            mField.set(this, mScroller);
        } catch (Exception e) {
            if (DEBUG)
                Log.e(TAG, e.getMessage());
        }
    }

    private void initFromAttributes(Context context, AttributeSet attrs) {
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Carousel);
        mScrollDelay = a.getInteger(R.styleable.Carousel_scroll_delay, DEFAULT_SCROLL_DELAY);
        mIsAutoScroll = a.getBoolean(R.styleable.Carousel_auto_scroll, DEFAULT_AUTO_SCROLL);
        mScrollDirection = a.getInt(R.styleable.Carousel_scroll_direction, DEFAULT_SCROLL_DIRECTION);
        mScroller.mDuration = a.getInteger(R.styleable.Carousel_scroll_duration, DEFAULT_SCROLL_DURATION);
        if (mIsAutoScroll) {
            mHandler = new Handler(this);
        }
        a.recycle();
    }

    /**
     * 解决用户触摸时的滑动问题
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mHandler.removeCallbacksAndMessages(null);
                break;
            case MotionEvent.ACTION_UP:
                resendMessage();
                break;
        }
        return super.onTouchEvent(ev);
    }

    public boolean isAutoScroll() {
        return mIsAutoScroll;
    }

    public void setAutoScroll(boolean isAutoScroll) {
        this.mIsAutoScroll = isAutoScroll;
        resendMessage();
    }

    /**
     * 获取一个Carousel的滑动方向
     *
     * @return int值是 {@link #SCROLL_DIRECTION_LEFT} 或 {@link #SCROLL_DIRECTION_RIGHT}
     */
    @ScrollDirection
    public int getScrollDirection() {
        return mScrollDirection;
    }

    public void getScrollDirection(@ScrollDirection int direction) {
        this.mScrollDirection = direction;
        resendMessage();
    }

    private void resendMessage() {
        mHandler.removeCallbacksAndMessages(null);
        if (mHandler == null) {
            mHandler = new Handler(this);
        }
        if (mIsAutoScroll) {
            if (SCROLL_DIRECTION_RIGHT == mScrollDirection) {
                mHandler.sendEmptyMessageDelayed(DIRECTION_RIGHT, mScrollDelay);
            } else {
                mHandler.sendEmptyMessageDelayed(DIRECTION_LEFT, mScrollDelay);
            }
        }
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        if (adapter == null) {
            throw new IllegalArgumentException("setAdapter Method parameter is null");
        }
        if (mAdapterWrapper == null) {
            mAdapterWrapper = new LoopPagerAdapterWrapper(adapter);
            super.setAdapter(mAdapterWrapper);
        } else {
            mAdapterWrapper.setRealAdapter(adapter);
        }
        int group = Integer.MAX_VALUE / adapter.getCount();
        int middlePosition = group * adapter.getCount() / 2;
        super.setCurrentItem(middlePosition);
        resendMessage();
    }

    @Override
    public PagerAdapter getAdapter() {
        return mAdapterWrapper.realAdapter;
    }

    @Override
    public void setCurrentItem(int item) {
        int itemPosition = super.getCurrentItem() - this.getCurrentItem() + item;
        super.setCurrentItem(itemPosition);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        int itemPosition = super.getCurrentItem() - this.getCurrentItem() + item;
        super.setCurrentItem(itemPosition, smoothScroll);
    }

    @Override
    public int getCurrentItem() {
        return super.getCurrentItem() % mAdapterWrapper.getRealCount();
    }

    @Override
    public void addOnPageChangeListener(OnPageChangeListener listener) {
        mListenerWrapper.addListener(listener);
    }

    @Override
    public void removeOnPageChangeListener(OnPageChangeListener listener) {
        mListenerWrapper.removeListener(listener);
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        addOnPageChangeListener(listener);
    }

    private int toRealPosition(int position) {
        return position % mAdapterWrapper.getRealCount();
    }

    /**
     * PagerAdapter的包装器，用于Carousel的无限循环
     */
    private class LoopPagerAdapterWrapper extends PagerAdapter {

        PagerAdapter realAdapter;

        int getRealCount() {
            return realAdapter.getCount();
        }

        LoopPagerAdapterWrapper(PagerAdapter realAdapter) {
            this.realAdapter = realAdapter;
        }

        void setRealAdapter(PagerAdapter realAdapter) {
            this.realAdapter = realAdapter;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public void startUpdate(ViewGroup container) {
            realAdapter.startUpdate(container);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return realAdapter.instantiateItem(container, toRealPosition(position));
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            realAdapter.destroyItem(container, toRealPosition(position), object);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            realAdapter.setPrimaryItem(container, toRealPosition(position), object);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            realAdapter.finishUpdate(container);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return realAdapter.isViewFromObject(view, object);
        }

        @Override
        public Parcelable saveState() {
            return realAdapter.saveState();
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
            realAdapter.restoreState(state, loader);
        }

        @Override
        public int getItemPosition(Object object) {
            return realAdapter.getItemPosition(object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //这里不需要对position进行任何处理
            return realAdapter.getPageTitle(position);
        }

        @Override
        public float getPageWidth(int position) {
            return realAdapter.getPageWidth(position);
        }
    }

    private class OnPageChangeListenerWrapper implements OnPageChangeListener {
        List<OnPageChangeListener> listeners = new ArrayList<>();

        void addListener(OnPageChangeListener listener) {
            listeners.add(listener);
        }

        void removeListener(OnPageChangeListener listener) {
            listeners.remove(listener);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (listeners != null && listeners.size() != 0) {
                for (OnPageChangeListener listener : listeners) {
                    listener.onPageScrolled(toRealPosition(position), positionOffset, positionOffsetPixels);
                }
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (listeners != null && listeners.size() != 0) {
                for (OnPageChangeListener listener : listeners) {
                    listener.onPageSelected(toRealPosition(position));
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (listeners != null && listeners.size() != 0) {
                for (OnPageChangeListener listener : listeners) {
                    listener.onPageScrollStateChanged(state);
                }
            }
        }
    }

    /**
     * 滑动时长的修改器
     */
    class ScrollModifier extends Scroller {
        int mDuration;

        ScrollModifier(Context context) {
            this(context, null);
        }

        ScrollModifier(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, mDuration);
        }
    }
}