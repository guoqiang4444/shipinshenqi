package com.lzc.pineapple;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzc.pineapple.cache.OfflineCacheActivity;
import com.lzc.pineapple.entity.CacheVideoInfo;
import com.lzc.pineapple.entity.RealVideo;
import com.lzc.pineapple.entity.RealVideoInfo;
import com.lzc.pineapple.entity.VideoInfoEntity;
import com.lzc.pineapple.entity.WrapperCacheVideoInfo;
import com.lzc.pineapple.fragment.BaseForCacheFragment;
import com.lzc.pineapple.fragment.BaseFragment;
import com.lzc.pineapple.fragment.VideoCacheDramaFragment;
import com.lzc.pineapple.fragment.VideoCacheListFragment;
import com.lzc.pineapple.util.Constant;
import com.lzc.pineapple.util.NetworkRequest;
import com.lzc.pineapple.util.UrlDecodeUtils;
import com.lzc.pineapple.util.UrlHelper;
import com.lzc.pineapple.util.Utils;
import com.lzc.pineapple.volley.Response;
import com.lzc.pineapple.volley.VolleyError;
import com.umeng.analytics.MobclickAgent;

/**
 * 备缓存列表Activity
 * @author zengchan.lzc
 *
 */
public class VideoListForCacheActivity extends BaseFragmentActivity implements OnClickListener{
	
	private ImageView back;
	private TextView title;
	private TextView rightText;
	private Button done;
	
	private String strTitle;
	
	private VideoInfoEntity videoInfo;
	
	private int currentSource;
	private int currentQualityPos;
	
	private BaseForCacheFragment fragment;
	
	private static final String VIDEO_INFO_KEY = "video_info_key";
	private static final String CURRENT_SOURCE_POSITION_KEY = "current_source_position_key";
	private static final String CURRENT_QUALITY_POS_KEY = "curr_quality_pos_key";
	
	
	private final static int DRAMA_FRAME = 0x02;
	private final static int LIST_FRAME = 0x03;
	
	private List<CacheVideoInfo> list;
	
	private ProgressDialog dialog;
	
	public static void launch(Context context, VideoInfoEntity videoInfo,int currentPos,int currentQualityPos) {
		Intent intent = new Intent(context, VideoListForCacheActivity.class);
		intent.putExtra(VIDEO_INFO_KEY, videoInfo);
		intent.putExtra(CURRENT_SOURCE_POSITION_KEY, currentPos);
		intent.putExtra(CURRENT_QUALITY_POS_KEY, currentQualityPos);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_list_for_cache_layout);
		getIntentValues();
		initViews();
		setListener();
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
    
    @Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			onBackClick();
			break;
		case R.id.right_text:
			onViewCacheListClick();
			break;
		case R.id.done:
			onDoneClick();
			break;
		default:
			break;
		}
	}
	private void getIntentValues(){
		videoInfo = (VideoInfoEntity) getIntent().getSerializableExtra(VIDEO_INFO_KEY);
		
		strTitle = ""+videoInfo.getStVideoBasic().getsVideoName();
		currentSource = getIntent().getIntExtra(CURRENT_SOURCE_POSITION_KEY, 0);
		currentQualityPos = getIntent().getIntExtra(CURRENT_QUALITY_POS_KEY, 0);
		
		if (TextUtils.isEmpty(videoInfo.getmPlatUrl().getPlatInfos().get(0).getsBrief())) {
			changeFrame(DRAMA_FRAME);
		} else {
			changeFrame(LIST_FRAME);
		}
	}
	private void changeFrame(int frameClass) {
		if(isOnDestroy){
			return;
		}
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = null;
		if (fm != null) {
			ft = fm.beginTransaction();
		} else {
			return;
		}
		switch (frameClass) {
	
		case DRAMA_FRAME:
			fragment = (BaseForCacheFragment) VideoCacheDramaFragment.newInstance(videoInfo, currentSource,currentQualityPos);
			ft.replace(R.id.fragment_content, fragment);
			break;
		case LIST_FRAME:
			fragment = (BaseForCacheFragment) VideoCacheListFragment.newInstance(videoInfo, currentSource,currentQualityPos);
			ft.replace(R.id.fragment_content, fragment);
			break;
		}
		ft.commitAllowingStateLoss();
	}
	private void initViews(){
		back = (ImageView) findViewById(R.id.back);
		title = (TextView) findViewById(R.id.title);
		title.setText(strTitle);
		rightText = (TextView) findViewById(R.id.right_text);
		rightText.setVisibility(View.VISIBLE);
		rightText.setText("查看离线");
		
		done = (Button)findViewById(R.id.done);
	}
	private void setListener() {
		back.setOnClickListener(this);
		rightText.setOnClickListener(this);
		done.setOnClickListener(this);
	}
	private void onBackClick() {
		onBackPressed();
	}
	private void onViewCacheListClick(){
		 OfflineCacheActivity.launch(this);
	}
	private void onDoneClick(){
		list = fragment.getSelectedCacheVideo();
		if(list.size() <= 0){
			Utils.showTips(this, "请选择要缓存的视频");
		}
		requestData();		
	}
	private void requestData(){
		dialog = Utils.showProgress(this, null, "正在获取地址", true, true);
		for(CacheVideoInfo cvi :list){
			requestRealUrl(cvi);
		}
	}
	private void requestRealUrl(final CacheVideoInfo cvi){
		String requestUrl = UrlHelper.URL_SOURCE_URL;
		StringBuilder sb = new StringBuilder();
		sb.append("&type=0");
		sb.append("&url=").append(cvi.getUrl());
//		sb.append("&encrypt=0");
		requestUrl += sb.toString();
		Log.d("lzc","requestUrl in videolistforcacheActivity==>"+requestUrl);
		NetworkRequest.get(requestUrl, RealVideoInfo.class,
				new Response.Listener<RealVideoInfo>() {

					@Override
					public void onResponse(final RealVideoInfo response) {
						if(response == null 
								|| response.getRows() == null
								|| response.getRows().size() <= 0){
							cvi.setChanged(true);
//							cvi.setUrl("");
						}else{
							renderValues(cvi,response);
						}
//						judgeChangeOver(cvi);
						judgeChangeOver();
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						judgeChangeOver(cvi);
					}

	    });
	}
	private void judgeChangeOver(final CacheVideoInfo cvi){
		cvi.setChanged(true);
		judgeChangeOver();
	}
	private void judgeChangeOver(){
		boolean isChangeOver = true;
		for(CacheVideoInfo cvi : list){
			if(!cvi.isChanged()){
				isChangeOver = false;
			}
		}
		if(isChangeOver){
			Utils.dismissDialog(dialog);
			WrapperCacheVideoInfo wcvi = new WrapperCacheVideoInfo();
	    	wcvi.setList(list);
			OfflineCacheActivity.launch(this, wcvi);
		}
	}
	private void renderValues(final CacheVideoInfo cvi,final RealVideoInfo response){
		List<RealVideo> listVideo = response.getRows().get(currentQualityPos).getVideos();
		if(listVideo.size() == 1){
			cvi.setChanged(true);
			String url = UrlDecodeUtils.decrypt(Constant.key, listVideo.get(0).getUrl());
//			String url = listVideo.get(0).getUrl();
//			url  = URLDecoder.decode(url);
//			Log.d("lzc","after url decode=="+url);
			cvi.setUrl(url);
		}else if(listVideo.size() > 1){
			CacheVideoInfo tempCacheVideoInfo = cvi;
			list.remove(cvi);
			for(RealVideo rv : listVideo){
				CacheVideoInfo cacheVideoInfo = new CacheVideoInfo();
				cacheVideoInfo = tempCacheVideoInfo;
				cacheVideoInfo.setChanged(true);
//				String url = Utils.encodeUrl(UrlDecodeUtils.decrypt(Constant.key, rv.getUrl()));
				String url = UrlDecodeUtils.decrypt(Constant.key, rv.getUrl());
				cacheVideoInfo.setUrl(url);
				list.add(cacheVideoInfo);
			}
		}
	}
}
