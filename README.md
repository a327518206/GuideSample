# GuideSample
####为什么要做向导页面呢？
大家作为手机用户，每天都可能安装新的App，那么每个App第一次运行一般都会给手机用户展示一下特有功能、或者是吸引用户使用的一些小亮点，这样我们第一个看到大概就是这么个页面，大家滑动它来了解这款新的App，当到达最后一页的时候，会有“开始体验”、“立即体验”等按钮等待着被用户点击，点击过后就会直接进入App的主页面了，当然第二次进入的时候就不会出现了。
上面问题的答案也就是：向导页面能吸引用户和提示用户我们所做的App一些功能。
####向导页面存在于什么位置？

```flow
st=>strat:欢迎界面
e=>end:主页面
op=>operation:向导页面
cond=>condition:是否进入过主页面？

st->op->cond
cond(yes)->e
cond(no)->op
```

从流程图上看出引导页面存在的位置。首先我们需要一个欢迎界面和一个主界面。
欢迎界面用SplashActivity，主页面用MainActivity，向导页面用GuideActivity。

	public class SplashActivity extends Activity {
	
	    private static final int START_NEXT = 0;
	
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_splash);
	        //设置延迟是因为每个App都有广告、App的Logo或者用户登录后台处理时间
	        handler.sendEmptyMessageDelayed(START_NEXT, 2000);
	    }
		
	    private Handler handler = new Handler(new Handler.Callback() {
	        @Override
	        public boolean handleMessage(Message msg) {
	            handler.removeMessages(START_NEXT);
	            Intent intent;
	            boolean isStartMain = getSaveBoolean();
	            if (isStartMain) {
	                //启动主页面
	                intent = new Intent(SplashActivity.this, MainActivity.class);
	            } else {
	                //启动引导页面
	                intent = new Intent(SplashActivity.this, GuideActivity.class);
	            }
	            startActivity(intent);
	            finish();
	            return false;
	        }
	    });
	
	    /**
	     * 读取保存的缓存数据
	     *
	     * @return
	     */
	    private boolean getSaveBoolean() {
	        SharedPreferences sp = this.getSharedPreferences("xiaoluogo", Context.MODE_PRIVATE);
	        return sp.getBoolean("isStartMain", false);
	    }
	}

MainActivity的代码和布局代码以及欢迎界面的代码就不往上贴了，最下面会放上源码提供给大家。


	<?xml version="1.0" encoding="utf-8"?>
	<!--这里是GuideActivity的布局代码-->
	<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    	xmlns:tools="http://schemas.android.com/tools"
    	android:layout_width="match_parent"
   		android:layout_height="match_parent"
   		tools:context="com.xiaoluogo.guidesample.GuideActivity">
    	<android.support.v4.view.ViewPager
	        android:id="@+id/view_guide"
	        android:layout_width="match_parent"
	        android:layout_height="match_parent"/>
	    <!-- 这里的想要做出好看的效果可以设置背景和文字颜色使用选择器selector -->
	    <Button
	        android:id="@+id/btn_start_main"
	        android:layout_width="wrap_content"
	        android:layout_height="wrap_content"
	        android:layout_alignParentBottom="true"
	        android:layout_centerHorizontal="true"
	        android:visibility="gone"
	        android:text="开始体验"
	        android:textSize="18sp"
	        android:paddingLeft="20dp"
	        android:paddingRight="20dp"
	        android:layout_marginBottom="80dp"
	        android:textColor="@android:color/white"
	        android:background="@android:color/holo_red_light" />
	    <RelativeLayout
	        android:layout_alignParentBottom="true"
	        android:layout_centerHorizontal="true"
	        android:layout_marginBottom="45dp"
	        android:layout_width="wrap_content"
	        android:layout_height="wrap_content">
	        <LinearLayout
	            android:id="@+id/ll_point_group"
	            android:orientation="horizontal"
	            android:layout_width="wrap_content"
	            android:layout_height="wrap_content"/>
	        <ImageView
	            android:id="@+id/red_point"
	            android:background="@drawable/red_point"
	            android:layout_width="wrap_content"
	            android:layout_height="wrap_content" />
	    </RelativeLayout>
	</RelativeLayout>

利用shape做的一个红点和一个灰点（下面是红色的点）

	<?xml version="1.0" encoding="utf-8"?>
	<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="oval">
	    <size android:height="10dp"
	        android:width="10dp"/>
	    <solid android:color="@android:color/holo_red_light"/>
	</shape>

接下来就是GudieActiviy代码了：
1）实例化布局控件。  
2）在res目录下新建drawable-hdpi文件夹，将需要的向导页面用到的图片放入该文件夹。  
3）创建`List<ImageView> imageViews`集合，用来存放刚才放入的向导图片。  
4）利用int数组存放图片的唯一标识  `R.drawable.(各个文件名)`。  
5）利用for循环将int数组中的每一个id赋给`ImageView`并存入`imageViews`集合。  
6）在for循环内部创建`ImageView`（灰色的点），存入`ll_point_group`中。  
7）设置`ViewPager`的适配器，将`imageViews`集合的图片添加到`ViewPager`。  
8）利用`getViewTreeObserver` 添加全部布局监听（`addGlobalLayoutListener`），获取两点之间的距离。  
9）利用`ViewPager` 设置监听页面改变，动态的获取红点移动的距离，以及设置`btn_start_main` 显示状态。  
10）监听`btn_start_main` 点击事件，首先存放进入过主页面的状态，然后在进入主页面，最后关闭当前活动。  
####代码如下：
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
