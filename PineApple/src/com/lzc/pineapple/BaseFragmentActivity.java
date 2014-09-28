package com.lzc.pineapple;

import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class BaseFragmentActivity extends FragmentActivity{
	protected boolean isOnDestroy = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        PApplication.getInstance().addActivity(this);
        super.onCreate(savedInstanceState);
    }
    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
//        MobclickAgent.onResume(this);
    }
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
//        MobclickAgent.onPause(this);
    }
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        PApplication.getInstance().removeActivity(this);
        super.onDestroy();
        isOnDestroy = true;
    }
}
