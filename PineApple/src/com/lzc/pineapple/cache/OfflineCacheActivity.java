package com.lzc.pineapple.cache;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzc.pineapple.BaseFragmentActivity;
import com.lzc.pineapple.R;
import com.lzc.pineapple.entity.CacheVideoInfo;
import com.lzc.pineapple.entity.WrapperCacheVideoInfo;
import com.umeng.analytics.MobclickAgent;

/**
 * 离线缓存Activity
 * s
 * @author zengchan.lzc
 */
public class OfflineCacheActivity extends BaseFragmentActivity implements OnClickListener{
    private ImageView back;
    private TextView  title;
    private Button  downloading;
    private Button  downloaded;
    
    private final static int DOWNLOADING_TYPE = 0x01;
    private final static int COMPLETED_TYPE = 0x02;
    
    private WrapperCacheVideoInfo cacheVideoInfo;
    
    private static final String CACHE_VIDEO_INFO_KEY = "CACHE_VIDEO_INFO_KEY";
    
    public static void launch(Context c) {
        Intent intent = new Intent(c, OfflineCacheActivity.class);
        c.startActivity(intent);
    }
    public static void launch(Context c,WrapperCacheVideoInfo cacheVideoInfo) {
        Intent intent = new Intent(c, OfflineCacheActivity.class);
        intent.putExtra(CACHE_VIDEO_INFO_KEY, cacheVideoInfo);
        c.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_cache_layout);
        getIntentValues();
        initViews();
        setListener();
        downloadingSelected();
        changeFrame(DOWNLOADING_TYPE,cacheVideoInfo);
        
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
    private void getIntentValues(){
    	cacheVideoInfo = (WrapperCacheVideoInfo) getIntent().getSerializableExtra(CACHE_VIDEO_INFO_KEY);
    }
    private void changeFrame(int frameClass,WrapperCacheVideoInfo cacheVideoInfo) {
        FragmentManager fm = getSupportFragmentManager();//FragmentManager();
        FragmentTransaction ft = null;
        if (fm != null) {
            ft = fm.beginTransaction();
        } else {
            return;
        }
        Fragment fragment = null;
        switch (frameClass) {
            case DOWNLOADING_TYPE:
            	
                fragment = DownloadingFragment.newInstance(cacheVideoInfo);
                ft.replace(R.id.fragment_content, fragment);
               
                break;
            case COMPLETED_TYPE:
                fragment = CacheCompletedFragment.newInstance();
                ft.replace(R.id.fragment_content, fragment);
               
                break;
        }
        
        ft.commitAllowingStateLoss();
    }
    private void initViews(){
        back = (ImageView)findViewById(R.id.back);
        title = (TextView)findViewById(R.id.title);
        title.setText(R.string.offline_cache);
        downloading = (Button)findViewById(R.id.downloading_text);
        downloaded  = (Button)findViewById(R.id.completed_text);
    }
    private void setListener(){
    	back.setOnClickListener(this);
        downloading.setOnClickListener(this);
        downloaded.setOnClickListener(this);
    }
    private void downloadingSelected() {
    	downloading.setSelected(true);
    	downloaded.setSelected(false);
	}

	private void completedSelected() {
		downloading.setSelected(false);
		downloaded.setSelected(true);
	}
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.back:
                onBackClick();
                break;
            case R.id.downloading_text:
            	onDownloadingClick();
            	break;
            case R.id.completed_text:
            	onCompletedClick();
            	break;
            default:
                break;
        }
    }
    private void onDownloadingClick(){
    	downloadingSelected();
    	changeFrame(DOWNLOADING_TYPE, null);
    }
    private void onCompletedClick(){
    	completedSelected();
    	changeFrame(COMPLETED_TYPE, null);
    }
    private void onBackClick(){
        onBackPressed();
    }
}
