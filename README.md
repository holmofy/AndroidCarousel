# Carousel
这是一个安卓客户端实用的轮播图控件，废话不多说，先上Demo效果图

![效果图](https://github.com/holmofy/Carousel/blob/master/Screenshot/screenshot-1.gif)

使用时在布局文件中设计好你想要的轮播图效果，如下
```xml
<RelativeLayout
        android:layout_width="match_parent"
        android:layout_height="160dp">

        <cn.hufeifei.carousel.Carousel
            android:id="@+id/carousel"
            android:layout_width="match_parent"
            android:layout_height="160dp" />

        <RelativeLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_alignParentBottom="true"
            android:background="#9000">

            <cn.hufeifei.carousel.indicator.TitleIndicator
                android:id="@+id/pagerTitle"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_centerVertical="true"
                android:layout_marginLeft="10dp"
                android:padding="10dp"
                android:textColor="#fff"
                tools:ignore="RtlHardcoded" />

            <cn.hufeifei.carousel.indicator.ShapeIndicator
                android:id="@+id/shapeIndicator"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_alignParentRight="true"
                android:layout_centerVertical="true"
                android:layout_marginRight="10dp"
                app:item_gap="10dp"
                app:item_height="10dp"
                app:item_width="10dp"
                tools:ignore="RtlHardcoded" />
        </RelativeLayout>
    </RelativeLayout>
```
其中有Carousel、TitleIndicator、ShapeIndicator、DigitalIndicator四个自定义类
1、Carousel是无限循环播放图片的，它继承自ViewPager，完全可以把它当成ViewPager使用(比如设置PagerAdapter，添加页面滑动监听)，它的自定义属性如下：
<table><tr><th>属性</th><th>含义</th><th>类型</th><th>默认值</th></tr><tr><td>scroll_delay</td><td>滑动间隔时间</td><td>integer</td><td>3000ms</td></tr><tr><td>scroll_duration</td><td>滑动时长</td><td>integer</td><td>600ms</td></tr><tr><td>scroll_direction</td><td>滑动方向</td><td>enum（left|right）</td><td>left</td></tr><tr><td>auto_scroll</td><td>是否自动滑动</td><td>boolean</td><td>true</td></tr></table>

2、ShapeIndicator是形状指示器，默认指示器形状为圆形（如Demo中的第一个轮播图）
你还可以selected_drawable与unselected_drawable两个属性来自定义它的形状以及颜色（如Demo中的第三个轮播图），它的自定义属性如下表：
<table><tr><th>属性</th><th>含义</th><th>类型</th><th>默认值</th></tr><tr><td>item_width</td><td>每个指示器的宽度</td><td>dimension</td><td>0(所以必须设置该属性)</td></tr><tr><td>item_height</td><td>每个指示器的高度</td><td>dimension</td><td>0(所以必须设置该属性)</td></tr><tr><td>item_gap</td><td>指示器之间的间隔<br>(如果为垂直方向则为垂直间隔)</td><td>dimension</td><td>默认与item_width相等</td></tr><tr><td>item_clickable</td><td>指示器是否能相应点击事件</td><td>boolean</td><td>true(所以默认点击指示器也能切换页面)</td></tr><tr><td>selected_drawable</td><td>指示器被选中时的drawable</td><td>drawable</td><td>默认为圆形浅红色(详见Demo演示图)</td></tr><tr><td>unselected_drawable</td><td>指示器未被选中时的drawable</td><td>drawable</td><td>默认为圆形暗灰色</td></tr></table>

3、TitleIndicator是用来显示页面标题的，它通过调用PagerAdapter的**getPageTitle**方法来获取标题内容的，所以如果你想设置表示那你需要重载getPageTitle方法。另外由于TitleIndicator继承自TextView，所以使用方式与TextView完全相同，你可以为它设置背景，颜色等各种样式（它没有自定义属性）。

4、DigitalIndicator是以数字的方式显示当前页面所在的位置的，它也是直接继承自TextView，与ViewPager关联后会自动根据ViewPager所在的位置修改文本内容（Demo中第二个轮播图就是用了该指示器）

**在以上的Indicator指示器中都有一个[void attachViewPager(@Nullable ViewPager viewPager)](https://github.com/holmofy/Carousel/blob/master/carousel/src/main/java/cn/hufeifei/carousel/indicator/Indicator.java#LC14) 方法可以将指示器与ViewPager关联，由于Carousel直接继承自ViewPager，所以你也可以使用该方法关联Carousel，如果传入参数为null，解除该指示器与原来的ViewPager的关联**
具体关联ViewPager的Java代码如下 ：
```
carousel.setAdapter(new PagerAdapter());
pagerTitle.attachViewPager(carousel);
shapeIndicator.attachViewPager(carousel);//调用该方法关联ViewPager对象
```

## 设置ViewPager切换动画
如果你想设置更炫的动画切换效果你可以参考** [ViewPagerTransforms项目](https://github.com/ToxicBakery/ViewPagerTransforms) **，使用方式非常简单：
```
//先添加该库的Gradle依赖
compile 'com.ToxicBakery.viewpager.transforms:view-pager-transforms:1.2.32@aar'

//通过setPageTransformer方法即可设置切换动画
pager = (ViewPager) findViewById(R.id.container);
pager.setAdapter(mAdapter);
pager.setPageTransformer(true, new RotateUpTransformer());
```
