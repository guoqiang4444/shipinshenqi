package com.lzc.pineapple;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.bbs.Ppacqq;
import com.bbs.os.Ppbiqq;
import com.bbs.os.Ppbkqq;
import com.lzc.pineapple.entity.RecommendVideos;
import com.lzc.pineapple.entity.StartPageEntity;
import com.lzc.pineapple.fragment.BaseTabFragment;
import com.lzc.pineapple.fragment.ProfileFragment;
import com.lzc.pineapple.fragment.RecommendFragment;
import com.lzc.pineapple.fragment.SortFragment;
import com.lzc.pineapple.fragment.TabRefreshListener;
import com.lzc.pineapple.search.SearchFragment;
import com.lzc.pineapple.util.Constant;
import com.lzc.pineapple.util.NetworkRequest;
import com.lzc.pineapple.util.UrlHelper;
import com.lzc.pineapple.util.Utils;
import com.lzc.pineapple.volley.Response;
import com.lzc.pineapple.volley.VolleyError;
import com.umeng.analytics.MobclickAgent;

public class MainActivity extends BaseFragmentActivity implements Ppbiqq{
    private FragmentManager         fm      = null;
    private FragmentTabHost         tabHost = null;
    private View                  indicator = null; 
    private boolean               isRefresh = false; 
    private StartPageEntity startPageEntity = null;
    private RecommendVideos recommendVideos = null;
    
    private static final int RECOMMEND_TYPE = 0x01;
    private static final int SORT_TYPE      = 0x02;
    private static final int SEARCH_TYPE    = 0x03;
    private static final int PROFILE_TYPE   = 0x04;
    
    private static final String RECOMMEND_SPEC = "recommend";
    private static final String SORT_SPEC      = "sort";
    private static final String SEARCH_SPEC    = "search";
    private static final String PROFILE_SPEC   = "profile";

    private TabRefreshListener tabRefreshListener ;
    
    private int pointsBalance;
    public static void launch(Context c) {
        Intent intent = new Intent(c, MainActivity.class);
        c.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);  
        fm      = getSupportFragmentManager();
        tabHost.setup(this,fm, R.id.realtabcontent);  
  
        initTab();
        pointsBalance = Ppbkqq.getInstance(this).cometnet();// 查询积分余额
        // (可选)开启用户数据统计服务,默认不开启，传入false值也不开启，只有传入true才会调用
     	Ppacqq.getInstance(this).comfdnet(true);
        requestData();
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
    private void initTab(){
        // 添加tab名称和图标  
        indicator = getIndicatorView(R.string.recommend, RECOMMEND_TYPE);  
        tabHost.addTab(tabHost.newTabSpec(RECOMMEND_SPEC)  
                .setIndicator(indicator), RecommendFragment.class, null);  
        indicator = getIndicatorView(R.string.sort, SORT_TYPE);  
        tabHost.addTab(  
                tabHost.newTabSpec(SORT_SPEC).setIndicator(indicator),  
                SortFragment.class, null);  
  
        indicator = getIndicatorView(R.string.search, SEARCH_TYPE);  
        tabHost.addTab(  
                tabHost.newTabSpec(SEARCH_SPEC).setIndicator(indicator),  
                SearchFragment.class, null);  
        
        indicator = getIndicatorView(R.string.profile, PROFILE_TYPE);  
        tabHost.addTab(  
                tabHost.newTabSpec(PROFILE_SPEC).setIndicator(indicator),  
                ProfileFragment.class, null);  
        tabHost.setOnTabChangedListener(new OnTabChangeListener() {
            
            @Override
            public void onTabChanged(String tabId) {
                // TODO Auto-generated method stub
            }
        });
       
    }
    private View getIndicatorView(int resId, int type) {  
        TextView tv = (TextView) getLayoutInflater().inflate(R.layout.tab_item_layout, null);  
        switch (type) {
            case RECOMMEND_TYPE:
                setTextViewCompoundDrawable(tv,R.drawable.tab_home_btn);
                break;
            case SORT_TYPE:
                setTextViewCompoundDrawable(tv,R.drawable.tab_cate_btn);
                break;
            case SEARCH_TYPE:
                setTextViewCompoundDrawable(tv,R.drawable.tab_search_btn);
                break;
            case PROFILE_TYPE:
                setTextViewCompoundDrawable(tv,R.drawable.tab_profile_btn);
                break;
            default:
                break;
        }
        tv.setText(resId);  
        return tv;  
    }  
    public void refresh(){
        requestData();
    }
    private void requestData(){
        
        isRefresh = true;
        NetworkRequest.get(UrlHelper.URL_HOME_PAGE, StartPageEntity.class, new Response.Listener<StartPageEntity>() {

            @Override
            public void onResponse(StartPageEntity response) {
                // TODO Auto-generated method stub
                isRefresh = false;
                startPageEntity = response;
                initRefreshListener();
                tabRefreshListener.refreshCompleted(response);
                requestFeatureData();
            }
            
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                isRefresh = false;
                initRefreshListener();
                tabRefreshListener.refreshError(error);
                requestFeatureData();
            }
            
        });
    }
    /**
     * 请求特色专区的数据
     */
    private void requestFeatureData(){
    	StringBuilder sb = new StringBuilder();
    	sb.append("&user_id="+Utils.getDeviceId(this));
    	sb.append("&device_id="+Utils.getDeviceId(this));
    	sb.append("&user_type=4&user_site=0");
    	String url = UrlHelper.URL_RECOMMEND_LIST + sb.toString();
    	Log.d("lzc","request feature url===>"+url);
        NetworkRequest.get(url, RecommendVideos.class, new Response.Listener<RecommendVideos>() {

            @Override
            public void onResponse(final RecommendVideos response) {
                // TODO Auto-generated method stub
            	recommendVideos = response;
            	
            	tabRefreshListener.refreshRecommendCompleted(startPageEntity, response);
                
            }
            
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
            	tabRefreshListener.refreshError(error,1);
            }
            
        });
    }
    private void initRefreshListener(){
        String tag = tabHost.getCurrentTabTag();
        BaseTabFragment fragment = (BaseTabFragment) fm.findFragmentByTag(tag);
        tabRefreshListener = fragment;
    }
    public boolean isRefresh(){
        return isRefresh;
    }
    public void setRefresh(boolean isRefresh){
        this.isRefresh = isRefresh;
    }
    public StartPageEntity getStartPageEntity(){
        return startPageEntity;
    }
    public RecommendVideos getRecommendVideos(){
        return recommendVideos;
    }
    private void setTextViewCompoundDrawable(TextView text,int resId){
        Drawable drawable = getResources().getDrawable(resId);
        drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
        text.setCompoundDrawables(null,drawable , null, null);
    }
 
    @Override  
    protected void onDestroy(){  
        // TODO Auto-generated method stub  
        super.onDestroy();  
        tabHost = null;  
    }  
    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	 Utils.showExitDialog(this, new Utils.OnDialogDoneListener() {
             @Override
             public void onDialogDone() {
                
            	 PApplication.getInstance().exit();
             }

			@Override
			public void onDialogCancel() {
				// TODO Auto-generated method stub
				
			}
         });
    }

	@Override
	public void comepnet(int pointsBalance) {
		// TODO Auto-generated method stub
		this.pointsBalance = pointsBalance;
		String tag = tabHost.getCurrentTabTag();
	    BaseTabFragment fragment = (BaseTabFragment) fm.findFragmentByTag(tag);
	    if(fragment instanceof ProfileFragment){
	    	((ProfileFragment) fragment).updatePointBalance();
	    }
	}
}
