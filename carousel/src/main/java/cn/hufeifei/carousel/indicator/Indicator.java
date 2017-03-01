package cn.hufeifei.carousel.indicator;

import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;

/**
 * 指示器接口
 * <p>
 * 如果对于指示器你有更好的设计想法，可以实现这个接口
 * Created by Holmofy on 2017/2/27.
 */

public interface Indicator {
    void attachViewPager(@Nullable ViewPager viewPager);
}
