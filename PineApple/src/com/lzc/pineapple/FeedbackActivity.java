package com.lzc.pineapple;

import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * 用户反馈界面
 * @author zengchan.lzc
 *
 */
public class FeedbackActivity extends BaseFragmentActivity{
    public static void launch(Context c) {
        Intent intent = new Intent(c, FeedbackActivity.class);
        c.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	MobclickAgent.onResume(this);
    }
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	MobclickAgent.onPause(this);
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}
