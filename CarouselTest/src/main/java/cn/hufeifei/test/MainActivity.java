package cn.hufeifei.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.ToxicBakery.viewpager.transforms.FlipHorizontalTransformer;
import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;

import cn.hufeifei.carousel.Carousel;
import cn.hufeifei.carousel.indicator.DigitalIndicator;
import cn.hufeifei.carousel.indicator.ShapeIndicator;
import cn.hufeifei.carousel.indicator.TitleIndicator;

public class MainActivity extends Activity {

    private Carousel carousel1;
    private TitleIndicator pagerTitle1;
    private ShapeIndicator shapeIndicator1;
    private Carousel carousel2;
    private TitleIndicator pagerTitle2;
    private DigitalIndicator digitalIndicator2;
    private Carousel carousel3;
    private ShapeIndicator shapeIndicator3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        carousel1 = (Carousel) findViewById(R.id.carousel1);
        pagerTitle1 = (TitleIndicator) findViewById(R.id.pagerTitle1);
        shapeIndicator1 = (ShapeIndicator) findViewById(R.id.shapeIndicator1);
        carousel2 = (Carousel) findViewById(R.id.carousel2);
        pagerTitle2 = (TitleIndicator) findViewById(R.id.pagerTitle2);
        digitalIndicator2 = (DigitalIndicator) findViewById(R.id.digitalIndicator2);
        carousel3 = (Carousel) findViewById(R.id.carousel3);
        shapeIndicator3 = (ShapeIndicator) findViewById(R.id.shapeIndicator3);

        carousel1.setAdapter(new PagerAdapter());
        carousel1.setPageTransformer(true, new CubeOutTransformer());
        pagerTitle1.attachViewPager(carousel1);
        shapeIndicator1.attachViewPager(carousel1);

        carousel2.setAdapter(new PagerAdapter());
        carousel2.setPageTransformer(true, new FlipHorizontalTransformer());
        pagerTitle2.attachViewPager(carousel2);
        digitalIndicator2.attachViewPager(carousel2);

        carousel3.setAdapter(new PagerAdapter());
        carousel3.setPageTransformer(true, new RotateUpTransformer());
        shapeIndicator3.attachViewPager(carousel3);
    }

    class PagerAdapter extends android.support.v4.view.PagerAdapter {
        int[] ids = {R.drawable.img1, R.drawable.img2, R.drawable.img3, R.drawable.img4, R.drawable.img5, R.drawable.img6, R.drawable.img7};

        private String[] imgTitles = {"美妆狂欢满199减100", "爆款手机低至一折", "手机耍大牌", "惠氏秒杀倒计时", "满199减100", "家电狂欢超值抢购", "实现你的小目标"};

        ImageView[] imgs;

        PagerAdapter() {
            imgs = new ImageView[ids.length];
            for (int i = 0; i < imgs.length; i++) {
                imgs[i] = new ImageView(getBaseContext());
                imgs[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
                imgs[i].setImageResource(ids[i]);
            }
        }

        @Override
        public int getCount() {
            return ids.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return imgTitles[position];
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(imgs[position]);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(imgs[position]);
            return imgs[position];
        }
    }
}
