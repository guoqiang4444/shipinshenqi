package com.lzc.pineapple;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lzc.pineapple.adapter.SourceAdapter;
import com.lzc.pineapple.db.PlayCacheInfo;
import com.lzc.pineapple.db.PlayHistoryDBHelper;
import com.lzc.pineapple.entity.BuyScoreEntity;
import com.lzc.pineapple.entity.ConsumEntity;
import com.lzc.pineapple.entity.PlatInfo;
import com.lzc.pineapple.entity.RealVideo;
import com.lzc.pineapple.entity.RealVideoEntity;
import com.lzc.pineapple.entity.RealVideoInfo;
import com.lzc.pineapple.entity.SourceSet;
import com.lzc.pineapple.entity.VideoBasic;
import com.lzc.pineapple.entity.VideoInfoEntity;
import com.lzc.pineapple.fragment.VideoDetailBriefFragment;
import com.lzc.pineapple.fragment.VideoDramaFragment;
import com.lzc.pineapple.fragment.VideoListFragment;
import com.lzc.pineapple.util.Constant;
import com.lzc.pineapple.util.NetworkRequest;
import com.lzc.pineapple.util.UrlDecodeUtils;
import com.lzc.pineapple.util.UrlHelper;
import com.lzc.pineapple.util.Utils;
import com.lzc.pineapple.volley.Response;
import com.lzc.pineapple.volley.VolleyError;
import com.lzc.pineapple.widget.HorizontalListView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.SocializeUser;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.FetchUserListener;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

/**
 * 视频详情展示Activity
 * 
 * @author zengchan.lzc
 * 
 */
public class VideoDetailActivity extends BaseFragmentActivity implements
		OnClickListener {
	private final static int BRIEF_FRAME = 0x01;
	private final static int DRAMA_FRAME = 0x02;
	private final static int LIST_FRAME = 0x03;
	private int fragmentType;

	private ImageView back;
	private TextView title;

	private ImageView cover;
	private TextView name;
	private TextView staring;
	private TextView director;
	private HorizontalListView listview;

	private TextView briefText;
	private TextView dramaText;
	private TextView divider;
	private TextView collection;
	private TextView cache;
	private TextView share;

	private RelativeLayout qualityLayout;
	private LinearLayout videoDetail;
	private Spinner spinner;
	private ArrayAdapter<String> qualityAdapter;

	private static final String VIDEO_BASIC_KEY = "VIDEO_BASIC_KEY";
	private static final String VIDEO_ID_KEY = "VIDEO_ID_KEY";
	private static final String VIDEO_NAME_KEY = "VIDEO_NAME_KEY";
	private String videoId;
	private String videoName;

	private ProgressBar progressBar;

	private ProgressDialog progressDialog;

	private boolean isRefresh = false;

	private VideoInfoEntity videoInfo;
	private RealVideoInfo realVideoInfo;

	private List<SourceSet> setSource = new ArrayList<SourceSet>();

	private SourceAdapter adapter;

	private int currentSource = 0;

	private int currentQualityPos = 0;

	private PlayHistoryDBHelper dbHelper;

	private boolean isCollected = false;

	private int platInfoSize;

	public static void launch(Context c) {
		Intent intent = new Intent(c, VideoDetailActivity.class);
		c.startActivity(intent);
	}

	public static void launch(Context context, VideoBasic videoBasic) {
		Intent intent = new Intent(context, VideoDetailActivity.class);
		intent.putExtra(VIDEO_BASIC_KEY, videoBasic);
		context.startActivity(intent);
	}

	public static void launch(Context context, String videoId, String videoName) {
		Intent intent = new Intent(context, VideoDetailActivity.class);
		intent.putExtra(VIDEO_ID_KEY, videoId);
		intent.putExtra(VIDEO_NAME_KEY, videoName);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initUmengService();
		openDBhelper();
		setContentView(R.layout.activity_video_detail_layout);
		getIntentValues();
		judgeCollectionStatus();
		initViews();
		setListener();
		requestData(videoId);
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
		closeDBHelper();
	}

	private UMSocialService umSocialService;
	private SocializeUser mUser;
	private Drawable user_icon;

	private void initUmengService() {
		umSocialService = UMServiceFactory.getUMSocialService(DESCRIPTOR);
		umSocialService.getUserInfo(this, new FetchUserListener() {
			@Override
			public void onStart() {
			}

			@Override
			public void onComplete(int status, SocializeUser user) {
				mUser = user;
				if (mUser != null
						&& mUser.mLoginAccount != null
						&& !TextUtils.isEmpty(mUser.mLoginAccount
								.getAccountIconUrl())) {
					String url = mUser.mLoginAccount.getAccountIconUrl();
//					new BindNETiMGTask(url) {
//						@Override
//						public void onComplate(Drawable drawable) {
//							user_icon = drawable;
//						}
//					}.execute();
				}
			}
		});
	}

	private void openDBhelper() {
		dbHelper = new PlayHistoryDBHelper(this);
		dbHelper.open();
	}

	private void closeDBHelper() {
		dbHelper.close();
	}

	private void getIntentValues() {
		Intent intent = getIntent();
		videoId = intent.getStringExtra(VIDEO_ID_KEY);
		videoName = intent.getStringExtra(VIDEO_NAME_KEY);
	}

	private void judgeCollectionStatus() {

		isCollected = dbHelper.isCollected(videoId);

	}

	private void initViews() {
		back = (ImageView) findViewById(R.id.back);

		title = (TextView) findViewById(R.id.title);
		title.setText(videoName);
		briefText = (TextView) findViewById(R.id.brief_text);
		dramaText = (TextView) findViewById(R.id.drama_text);

		divider = (TextView) findViewById(R.id.divider);
		collection = (TextView) findViewById(R.id.collection);
		updateCollectionStatus();
		cache = (TextView) findViewById(R.id.cache);
		share = (TextView) findViewById(R.id.share);
		cover = (ImageView) findViewById(R.id.cover);
		name = (TextView) findViewById(R.id.name);
		staring = (TextView) findViewById(R.id.staring);
		director = (TextView) findViewById(R.id.director);
		listview = (HorizontalListView) findViewById(R.id.source_list);
		adapter = new SourceAdapter(this, setSource);
		listview.setAdapter(adapter);
		qualityLayout = (RelativeLayout) findViewById(R.id.quality_layout);
		qualityLayout.setVisibility(View.VISIBLE);
		spinner = (Spinner) findViewById(R.id.spinner);
		progressBar = (ProgressBar) findViewById(R.id.progress_bar);

		videoDetail = (LinearLayout) findViewById(R.id.video_detail_layout);
	}

	private void setListener() {
		back.setOnClickListener(this);
		briefText.setOnClickListener(this);
		dramaText.setOnClickListener(this);
		collection.setOnClickListener(this);
		cache.setOnClickListener(this);
		share.setOnClickListener(this);
		cover.setOnClickListener(this);
		videoDetail.setOnClickListener(this);
		listview.setOnItemClickListener(onItemClickListener);
	}

	private void updateCollectionStatus() {
		if (isCollected) {
			collection.setText(R.string.cancel_collection);
		} else {
			collection.setText(R.string.collection);
		}
	}

	final OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			// TODO Auto-generated method stub
			currentSource = position;
			updateSourceView(position);
			updateDramaList(position);
			String qualityUrl = videoInfo.getmPlatUrl().getListPlatInfo()
					.get(currentSource).get(0).getsUrl();
			requestRealUrl(qualityUrl, "正在获取视频支持的清晰度");
		}
	};

	private void updateSourceView(int position) {
		for (int i = 0; i < setSource.size(); i++) {
			if (i == position) {
				setSource.get(i).setSelected(true);
			} else {
				setSource.get(i).setSelected(false);
			}
		}
		adapter.updateList(setSource);
	}

	private void updateDramaList(int position) {
		if (fragmentType == DRAMA_FRAME || fragmentType == LIST_FRAME) {
			changeFrame(fragmentType);
		}
	}

	private void playVideo() {
		if (realVideoInfo != null && realVideoInfo.getRows().size() > 0) {
			List<RealVideo> listVideo = realVideoInfo.getRows().get(currentQualityPos).getVideos();
			Log.d("lzc","size===>"+listVideo.size());
			if (listVideo.size() == 1) {
				BaiduPlayActivity.launch(this, UrlDecodeUtils.decrypt(
						Constant.key, listVideo.get(0).getUrl()));
			} else if (listVideo.size() > 1) {
//				BaiduPlayActivity.launch(this, realVideoInfo, currentQualityPos);
				Uri uri = getPlayUri();
				BaiduPlayActivity.launch(this, uri);
			} else {
				Utils.showTips(this, "无法播放");
			}

		} else {
			if(videoInfo == null){
				return;
			}
			int src = setSource.get(currentSource).getiSrc();
			String url = videoInfo.getmPlatUrl().getPlatInfos(src).get(0)
					.getsUrl();
			VideoBasic videoBasic = videoInfo.getStVideoBasic();
			PlayCacheInfo playCacheInfo = new PlayCacheInfo();
			playCacheInfo.setType(1);
			playCacheInfo.setCover(videoBasic.getsPicUrl());
			playCacheInfo.setVideoName(videoBasic.getsVideoName());
			playCacheInfo.setTime(Utils.getSysNowTime());
			playCacheInfo.setVideoId("" + videoBasic.getlVideoId());
			VideoPlayActivity.launch(VideoDetailActivity.this, url,
					playCacheInfo);
		}
	}
    private Uri getPlayUri(){
    	List<RealVideo> listVideo = realVideoInfo.getRows().get(currentQualityPos).getVideos();
    	Uri uri = Uri.fromFile(createFile(listVideo));
    	return uri;
    }
    private File createFile(List<RealVideo> listVideo){
    	String title = realVideoInfo.getRows().get(currentQualityPos).getTitle();
		String path = Environment.getExternalStorageDirectory()+"/Download/"+title+".m3u8";
		Log.d("lzc","path===>"+path);
		File file = new File(path);
		if(file.exists()){
			return file;
		}else{
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
		String writeString = getWriteString(listVideo);
        byte [] bytes = writeString.getBytes();   
        try {
			fos.write(bytes);
			fos.close();   
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
		return file;
	}
    private String getWriteString(List<RealVideo> listVideo){
		long second = getSumSecond(listVideo);
		StringBuilder sb = new StringBuilder();
		sb.append("#EXTM3U\r\n");
		sb.append("#EXT-X-TARGETDURATION:"+second+"\r\n");
		sb.append("#EXT-X-VERSION:2\r\n");
		sb.append("\r\n");
		sb.append("\r\n");
		for(RealVideo realVideo : listVideo){
			sb.append("#EXTINF:");
			sb.append(realVideo.getSeconds());
			sb.append(",");
			sb.append("\r\n");
			String url = UrlDecodeUtils.decrypt(Constant.key, realVideo.getUrl());
			sb.append(url);
			sb.append("\r\n");
		}
		sb.append("\r\n");
		sb.append("\r\n");
		sb.append("#EXT-X-ENDLIST");
		return sb.toString();
	}
    private long getSumSecond(List<RealVideo> listVideo){
    	long second = 0;
    	for(RealVideo realVideo : listVideo){
    		second += Long.parseLong(realVideo.getSeconds());
    	}
    	return second;
    }
	private void requestData(String videoId) {
		// progressBar.setVisibility(View.VISIBLE);
		progressDialog = Utils.showProgress(this, null, "初始化数据请稍后", true, true);
		isRefresh = true;
		String url = UrlHelper.URL_VIDEO_INFO;
		StringBuilder sb = new StringBuilder();
		sb.append("&vid=").append(videoId);
		sb.append("&actsrc=1&actkey=14&needurl=1");
		url += sb.toString();
		NetworkRequest.get(url, VideoInfoEntity.class,
				new Response.Listener<VideoInfoEntity>() {

					@Override
					public void onResponse(final VideoInfoEntity response) {
						Utils.dismissDialog(progressDialog);
						if (response == null || response.getiRet() != 0) {

							return;
						}
						videoInfo = response;
						String qualityUrl = videoInfo.getmPlatUrl()
								.getListPlatInfo().get(currentSource).get(0)
								.getsUrl();
						requestRealUrl(qualityUrl, "正在获取视频支持的清晰度");

						updateView();
						platInfoSize = response.getmPlatUrl().getPlatInfos()
								.size();
						if (platInfoSize <= 1) {
							briefSelected();
							changeFrame(BRIEF_FRAME);
						} else {
							dramaSelected();
							if (TextUtils.isEmpty(response.getmPlatUrl()
									.getPlatInfos().get(0).getsBrief())) {
								changeFrame(DRAMA_FRAME);
							} else {
								changeFrame(LIST_FRAME);
							}
						}

					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						isRefresh = false;
						Utils.dismissDialog(progressDialog);
					}

				});
	}

	private void requestRealUrl(String url, String message) {
		progressDialog = Utils.showProgress(this, null, message, true, true);
		isRefresh = true;
		String requestUrl = UrlHelper.URL_SOURCE_URL;
		StringBuilder sb = new StringBuilder();
		sb.append("&type=0");
		sb.append("&url=").append(url);
		sb.append("&encrypt=0");
		requestUrl += sb.toString();
		NetworkRequest.get(requestUrl, RealVideoInfo.class,
				new Response.Listener<RealVideoInfo>() {

					@Override
					public void onResponse(final RealVideoInfo response) {
						Utils.dismissDialog(progressDialog);
						isRefresh = false;
						realVideoInfo = response;
						updateQualityList();
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						isRefresh = false;
						Utils.dismissDialog(progressDialog);
					}

				});
	}

	private void updateView() {
		VideoBasic videoBasic = videoInfo.getStVideoBasic();
		Utils.displayImage(videoBasic.getsPicUrl(), cover);
		name.setText(videoBasic.getsVideoName());
		staring.setText("主演: " + videoBasic.getsActor());
		director.setText("导演: " + videoBasic.getsDirector());

		updateSetSource();

	}

	private void updateQualityList() {
		if (realVideoInfo == null) {
			return;
		}
		int rowsSize = realVideoInfo.getRows().size();
		String[] item = new String[rowsSize];
		for (int i = 0; i < rowsSize; i++) {
			RealVideoEntity rve = realVideoInfo.getRows().get(i);
			item[i] = rve.getQuality();
		}

		qualityAdapter = new ArrayAdapter<String>(this,
				R.drawable.drop_list_hover, item);
		// 设置下拉列表的风格
		// adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		qualityAdapter.setDropDownViewResource(R.drawable.drop_list_ys);
		// adapter.setDropDownViewResource(R.layout.drop_down_item);
		// 将adapter 添加到spinner中
		spinner.setAdapter(qualityAdapter);
		// 添加事件Spinner事件监听
		spinner.setOnItemSelectedListener(new SpinnerSelectedListener());
	}

	// 使用数组形式操作
	class SpinnerSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			currentQualityPos = position;
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	private void updateSetSource() {
		List<List<PlatInfo>> listPlatInfo = videoInfo.getmPlatUrl()
				.getListPlatInfo();
		int listSum = listPlatInfo.size();
		for (int i = 0; i < listSum; i++) {
			SourceSet ss = new SourceSet();
			if (0 == i) {
				ss.setSelected(true);
				currentSource = 0;
			} else {
				ss.setSelected(false);
			}
			ss.setiSrc(listPlatInfo.get(i).get(0).getiSrc());
			setSource.add(ss);
		}

		adapter.notifyDataSetChanged();
	}

	private void changeFrame(int frameClass) {
		if(isOnDestroy){
			return;
		}
		FragmentManager fm = getSupportFragmentManager();// FragmentManager();
		FragmentTransaction ft = null;
		if (fm != null) {
			ft = fm.beginTransaction();
		} else {
			return;
		}
		Fragment fragment = null;

		switch (frameClass) {
		case BRIEF_FRAME:
			fragment = VideoDetailBriefFragment.newInstance(videoInfo);
			ft.replace(R.id.fragment_content, fragment);
			break;
		case DRAMA_FRAME:

			fragment = VideoDramaFragment.newInstance(videoInfo,
					setSource.get(currentSource).getiSrc(), currentQualityPos);
			ft.replace(R.id.fragment_content, fragment);
			break;
		case LIST_FRAME:
			fragment = VideoListFragment.newInstance(videoInfo,
					setSource.get(currentSource).getiSrc(), currentQualityPos);
			ft.replace(R.id.fragment_content, fragment);
			break;
		}

		ft.commitAllowingStateLoss();
		fragmentType = frameClass;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			onBackClick();
			break;
		case R.id.brief_text:
			onBriefClick();
			break;
		case R.id.drama_text:
			onDramaClick();
			break;
		case R.id.collection:
			onCollectionClick();
			break;
		case R.id.cache:
			onCacheClick();
			break;
		case R.id.share:
			onShareClick();
			break;
		case R.id.cover:
			onCoverClick();
			break;
		case R.id.video_detail_layout:
			onVideoDetailClick();
			break;
		default:
			break;
		}
	}

	private void onCollectionClick() {
		if (isCollected) {
			dbHelper.deleteCollectionRow(videoId);
			isCollected = false;
		} else {
			PlayCacheInfo playCacheInfo = new PlayCacheInfo();
			VideoBasic videoBasic = videoInfo.getStVideoBasic();
			playCacheInfo.setVideoId("" + videoBasic.getlVideoId());
			playCacheInfo.setVideoName(videoBasic.getsVideoName());
			playCacheInfo.setCover(videoBasic.getsPicUrl());
			playCacheInfo.setTime(Utils.getSysNowTime());
			playCacheInfo.setType(1);
			long id = dbHelper.insertCollectionVideoDataToTable(playCacheInfo);
			isCollected = true;
		}
		updateCollectionStatus();
	}

	private void onBriefClick() {
		briefSelected();
		if (fragmentType != BRIEF_FRAME) {
			changeFrame(BRIEF_FRAME);
		}
	}

	private void onDramaClick() {
		dramaSelected();
		if (videoInfo != null
				&& TextUtils.isEmpty(videoInfo.getmPlatUrl().getPlatInfos()
						.get(0).getsBrief())) {
			if (fragmentType != DRAMA_FRAME) {
				changeFrame(DRAMA_FRAME);
			}
		} else {
			if (fragmentType != LIST_FRAME) {
				changeFrame(LIST_FRAME);
			}
		}

	}

	private void briefSelected() {
		briefText.setSelected(true);
		dramaText.setSelected(false);
	}

	private void dramaSelected() {
		briefText.setSelected(false);
		dramaText.setSelected(true);
	}

	private void onBackClick() {
		onBackPressed();
	}

	// 首先在您的Activity中添加如下成员变量
	final UMSocialService mController = UMServiceFactory
			.getUMSocialService("com.umeng.share");

	private void onShareClick() {
		addWXPlatform();
		addQQPlatform();
		addSinaPlatform();
		addQQZonePlatform();
		if(videoInfo == null){
			return;
		}
		String url = "";
		try {
			// realVideoInfo.getRows().get(0).getVideos();
			int src = setSource.get(0).getiSrc();
			url = videoInfo.getmPlatUrl().getPlatInfos(src).get(0).getsUrl();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 设置分享视频
		UMVideo umVideo = new UMVideo(url);
		// 设置视频缩略图
		umVideo.setThumb(videoInfo.getStVideoBasic().getsPicUrl());
		umVideo.setTitle("视频神器分享!");
		mController.setShareMedia(umVideo);
		// 设置分享文字内容
		mController.setShareContent(videoInfo.getStVideoBasic().getsVideoName()+"终于让我找到了，小伙伴们快来看吧");
		mController.getConfig().setPlatformOrder(SHARE_MEDIA.WEIXIN_CIRCLE,
				SHARE_MEDIA.SINA, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.QZONE);
		mController.getConfig().removePlatform(SHARE_MEDIA.RENREN,
				SHARE_MEDIA.DOUBAN, SHARE_MEDIA.TENCENT);
		
		mController.registerListener(new SnsPostListener() {

			@Override
			public void onStart() {

			}

			@Override
			public void onComplete(SHARE_MEDIA platform, int eCode,
					SocializeEntity entity) {
				//qq,sina,qzone,weixin,weixin_circle
				String type = platform.toString();
				//0 内购、 1 微博分享、2 微信朋友圈 3 qzone分享 4 微信好友 5 QQ好友
				int src = -1;
                if(TextUtils.equals(type, "sina")){
                	src = 1;
                }else if(TextUtils.equals(type, "weixin_circle")){
                	src = 2;
                }else if(TextUtils.equals(type, "qzone")){
                	src = 3;
                }else if(TextUtils.equals(type, "weixin")){
                	src = 4;
                }else if(TextUtils.equals(type, "qq")){
                	src = 5;
                }
				if(eCode == 200 || eCode ==40000){//分享成功
					commitShareInfo(src);
				}
//				commitShareInfo(src);
			}
		});
		// 是否只有已登录用户才能打开分享选择页
		mController.openShare(this, false);

	}
    private void commitShareInfo(int src){
    	StringBuilder sb = new StringBuilder();
		sb.append("&user_id=").append(Utils.getDeviceId(this));
		sb.append("&device_id=").append(Utils.getDeviceId(this));
		sb.append("&user_type=4&user_site=0");
		sb.append("&src="+src);
		String url = UrlHelper.URL_BUY_SCORE + sb.toString();
		NetworkRequest.get(url, BuyScoreEntity.class,
				new Response.Listener<BuyScoreEntity>() {

					@Override
					public void onResponse(BuyScoreEntity response) {
						// TODO Auto-generated method stub
						if(response.getRet_code() == 0){//调用成功
							Utils.savePreferenceInt(getApplicationContext(), 
									Utils.getDeviceId(VideoDetailActivity.this), response.getScore());
						}
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
					}

				});
    }
	private void onCacheClick() {
		VideoListForCacheActivity.launch(this, videoInfo,
				setSource.get(currentSource).getiSrc(), currentQualityPos);
	}

	private void onCoverClick() {
		playVideo();
	}

	private void onVideoDetailClick() {
		// playVideo();
	}

	private final static String qqAppId = "1102004104";
	private final static String qqAppKey = "6vKZ22B3NX7yDXAu";

	/**
	 * @功能描述 : 添加微信平台分享
	 * @return
	 */
	private void addWXPlatform() {

		// wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
		String appId = "wx96a7ce09dcabc761";
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(this, appId);
		wxHandler.addToSocialSDK();
		wxHandler.setTitle("视频神器还不错，快来下载吧。");

		// 支持微信朋友圈
		// 朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(this, appId);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
		wxCircleHandler.setTitle("视频神器还不错，快来下载吧。");

	}

	private void addQQPlatform() {
		// 添加QQ支持, 并且设置QQ分享内容的target url
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, qqAppId,
				qqAppKey);
		qqSsoHandler.addToSocialSDK();
	}

	private void addSinaPlatform() {
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
	}

	private void addQQZonePlatform() {
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, qqAppId,
				qqAppKey);
		qZoneSsoHandler.addToSocialSDK();
	}

	public static final String DESCRIPTOR = "com.umeng.share";

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("lzc","onActivityResult==>");
		UMSocialService controller = UMServiceFactory.getUMSocialService(DESCRIPTOR);
		// 根据requestCode获取对应的SsoHandler
		UMSsoHandler ssoHandler = controller.getConfig().getSsoHandler(
				requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
			Log.d("lzc", "#### ssoHandler.authorizeCallBack");
		}
	}
}
