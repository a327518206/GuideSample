package com.xiaoluogo.guidesample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SplashActivity extends Activity {

    private static final int START_NEXT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
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
