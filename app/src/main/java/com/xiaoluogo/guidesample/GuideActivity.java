package com.xiaoluogo.guidesample;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xiaoluogo.guidesample.Utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private Button btn_start_main;
    private ImageView red_point;
    private LinearLayout ll_point_group;
    List<ImageView> imageViews;
    private int[] ids;
    //两点之间的距离
    private int pointDelta;
    //单位转换好的值
    private int pointSize;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        viewPager = (ViewPager) findViewById(R.id.view_guide);
        btn_start_main = (Button) findViewById(R.id.btn_start_main);
        red_point = (ImageView) findViewById(R.id.red_point);
        ll_point_group = (LinearLayout) findViewById(R.id.ll_point_group);

        ids = new int[]{
                R.drawable.guide1,
                R.drawable.guide2,
                R.drawable.guide3
        };
        imageViews = new ArrayList<>();
        for (int i = 0; i < ids.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setBackgroundResource(ids[i]);
            imageViews.add(imageView);
            //创建点
            ImageView normal_point = new ImageView(this);
            normal_point.setBackgroundResource(R.drawable.normal_point);
            //利用单位转换,将dp转换为px以适应不同分辨率的手机
            pointSize = DensityUtil.dip2px(this,10);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(pointSize, pointSize);
            if (i != 0) {
                //除了第0个,其他的点都距离左边有间距
                params.leftMargin = pointSize;
            }
            normal_point.setLayoutParams(params);
            ll_point_group.addView(normal_point);
        }
        //设置适配器
        viewPager.setAdapter(new MyPagerAdapter());
        //利用视图树观察者添加全局监听 View生命周期中onLayout和onDraw时已经可以得到高,宽,边距等数值
        red_point.getViewTreeObserver().addOnGlobalLayoutListener(new MyOnGlobalLayoutListener());
        //viewPager添加页面改变监听
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());

        btn_start_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //当点击按钮首先保存进入过主页面的状态
                SharedPreferences sp = getSharedPreferences("xiaoluogo", Context.MODE_PRIVATE);
                sp.edit().putBoolean("isStartMain",true).commit();
                //进入主页面
                Intent intent = new Intent(GuideActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        /**
         * 页面滑动的时候,会回调该方法
         *
         * @param position             当前页面位置
         * @param positionOffset       滑动屏幕百分比
         * @param positionOffsetPixels 滑动屏幕的像素
         */
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //动态的获取移动的距离
            //移动的距离 : 间距 = 滑动屏幕百分比
            //int move = (int) (positionOffset * pointDelta);
            // 红点的位置 = 移动的距离 + 当前的位置
            int move = (int) (positionOffset * pointDelta) + position * pointDelta;
            //设置红点的移动距离
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) red_point.getLayoutParams();
            params.leftMargin = move;
            red_point.setLayoutParams(params);
        }

        /**
         * 页面被选中的时候调用该方法
         *
         * @param position
         */
        @Override
        public void onPageSelected(int position) {
            if (position == imageViews.size() - 1) {
                btn_start_main.setVisibility(View.VISIBLE);
            } else {
                btn_start_main.setVisibility(View.GONE);
            }
        }

        /**
         * 页面滑动状态发生改变时会调用该方法
         *
         * @param state 三种状态(拖拽,静止,惯性回滚)
         */
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    class MyOnGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {

        @Override
        public void onGlobalLayout() {
            //移除监听,因为此时执行不止一次---利用过时的方法是希望兼容15及以下版本
            red_point.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            //点间距 = 第1个点到左边的距离 - 第0个点到左边的距离
            pointDelta = ll_point_group.getChildAt(1).getLeft() - ll_point_group.getChildAt(0).getLeft();
        }
    }

    class MyPagerAdapter extends PagerAdapter {
        /**
         * 得到页面的总数
         * @return
         */
        @Override
        public int getCount() {
            return imageViews.size();
        }

        /**
         * 相当于getView
         * @param container viewPager
         * @param position
         * @return
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = imageViews.get(position);
            container.addView(imageView);
            return imageView;
        }

        /**
         * 判断
         * @param view
         * @param object
         * @return
         */
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        /**
         * 销毁页面
         * @param container
         * @param position
         * @param object
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //super.destroyItem(container, position, object);
            //container.removeViewAt(position);如果这样写会导致崩溃
            container.removeView((View) object);
        }
    }
}
