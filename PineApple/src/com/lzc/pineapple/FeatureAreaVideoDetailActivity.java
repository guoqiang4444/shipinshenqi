package com.lzc.pineapple;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzc.pineapple.cache.OfflineCacheActivity;
import com.lzc.pineapple.db.PlayCacheInfo;
import com.lzc.pineapple.db.PlayHistoryDBHelper;
import com.lzc.pineapple.entity.BuyScoreEntity;
import com.lzc.pineapple.entity.CacheVideoInfo;
import com.lzc.pineapple.entity.RecommendVideo;
import com.lzc.pineapple.entity.WrapperCacheVideoInfo;
import com.lzc.pineapple.util.NetworkRequest;
import com.lzc.pineapple.util.UrlHelper;
import com.lzc.pineapple.util.Utils;
import com.lzc.pineapple.volley.Response;
import com.lzc.pineapple.volley.VolleyError;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

/**
 * 特色专区视频详情页
 * 
 * @author zengchan.lzc
 * 
 */
public class FeatureAreaVideoDetailActivity extends BaseFragmentActivity
		implements OnClickListener {
	private ImageView back;
	private TextView title;

	private ImageView cover;
	private TextView name;
	private TextView update;
	private TextView staring;
	private TextView director;
	private TextView play;
	private TextView brief;

	private TextView collection;
	private TextView cache;
	private TextView share;
	private TextView vipScore;
	private PlayHistoryDBHelper dbHelper;

	private boolean isCollected = false;

	private static final String RECOMMEND_VIDEO_KEY = "recommend_video_key";

	private RecommendVideo recommendVideo;

	public static void launch(Context context, RecommendVideo recommendVideo) {
		Intent intent = new Intent(context,
				FeatureAreaVideoDetailActivity.class);
		intent.putExtra(RECOMMEND_VIDEO_KEY, recommendVideo);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		openDBhelper();
		getIntentValues();
		judgeCollectionStatus();
		setContentView(R.layout.activity_feature_area_detail_layout);
		initViews();
		setListener();
		updateCollectionStatus();
		bindData();
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

	private void judgeCollectionStatus() {

		isCollected = dbHelper.isCollected(""+recommendVideo.getId());

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
		recommendVideo = (RecommendVideo) intent.getSerializableExtra(RECOMMEND_VIDEO_KEY);
//		setOriginalUrl();
	}

	private void updateCollectionStatus() {
		if (isCollected) {
			collection.setText(R.string.cancel_collection);
		} else {
			collection.setText(R.string.collection);
		}
	}

	private void initViews() {
		back = (ImageView) findViewById(R.id.back);
		title = (TextView) findViewById(R.id.title);
		cover = (ImageView) findViewById(R.id.cover);
		name = (TextView) findViewById(R.id.name);
		collection = (TextView) findViewById(R.id.collection);
		updateCollectionStatus();
		cache = (TextView) findViewById(R.id.cache);
		share = (TextView) findViewById(R.id.share);
		update = (TextView) findViewById(R.id.update_num);
		staring = (TextView) findViewById(R.id.staring);
		director = (TextView) findViewById(R.id.director);
		play = (TextView) findViewById(R.id.play_text);
		brief = (TextView) findViewById(R.id.brief_text);
		vipScore = (TextView) findViewById(R.id.vip_text);
	}

	private void setListener() {
		back.setOnClickListener(this);
		play.setOnClickListener(this);
		collection.setOnClickListener(this);
		cache.setOnClickListener(this);
		share.setOnClickListener(this);
	}

	private void bindData() {
		title.setText(recommendVideo.getTitle());
		Utils.displayImage(recommendVideo.getImg(), cover);
		name.setText(recommendVideo.getTitle());
		update.setVisibility(View.GONE);

		staring.setText("主演: " + recommendVideo.getActor());
		director.setText("导演: " + recommendVideo.getDirector());
		brief.setText(recommendVideo.getCatalog());
		
		int buy = recommendVideo.getBuy();
		if(buy == 1){//用户已经购买
			vipScore.setText("已购");
		}else{
			int score = recommendVideo.getPrice();
			vipScore.setText(getString(R.string.vip_score_text, score));
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			onBackClick();
			break;
		case R.id.play_text:
			onPlayClick();
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
		default:
			break;
		}
	}

	private void onCollectionClick() {
		if (isCollected) {
			String videoId = ""+recommendVideo.getId();
			dbHelper.deleteCollectionRow(videoId);
			isCollected = false;
		} else {
			PlayCacheInfo playCacheInfo = new PlayCacheInfo();
			String videoId = ""+recommendVideo.getId();
			playCacheInfo.setVideoId(videoId);
			playCacheInfo.setVideoName(recommendVideo.getTitle());
			playCacheInfo.setCover(recommendVideo.getImg());
			playCacheInfo.setTime(Utils.getSysNowTime());
			playCacheInfo.setType(0);
			long id = dbHelper.insertCollectionVideoDataToTable(playCacheInfo);
			isCollected = true;
		}
		updateCollectionStatus();
	}

	private void onBackClick() {
		onBackPressed();
	}

	private void onPlayClick() {
		String url = getPlayUrl();
		if (TextUtils.isEmpty(url)) {
			Utils.showTips(this, "播放地址为空");
		} else {
			BaiduPlayActivity.launch(this, url,recommendVideo.getPrice(),recommendVideo.getId());
		}
	}

	private String getPlayUrl(){
		String url = "";
		List<String> srcs = recommendVideo.getSrc();
		if (srcs != null && srcs.size() > 0) {
			url = recommendVideo.getSrc().get(0);
			url = Utils.encodeUrl(url);
		}
		return url;
	}
	private void onCacheClick() {
		CacheVideoInfo cacheVideoInfo = new CacheVideoInfo();
		cacheVideoInfo.setCacheStatus(CacheVideoInfo.START);
		cacheVideoInfo.setCover(recommendVideo.getImg());
		cacheVideoInfo.setName(recommendVideo.getTitle());
		cacheVideoInfo.setUrl(getPlayUrl());
		WrapperCacheVideoInfo wcvi = new WrapperCacheVideoInfo();
    	List<CacheVideoInfo> list = new ArrayList<CacheVideoInfo>();
    	list.add(cacheVideoInfo);
    	wcvi.setList(list);
		OfflineCacheActivity.launch(this, wcvi);
	}

	// 首先在您的Activity中添加如下成员变量
	final UMSocialService mController = UMServiceFactory
			.getUMSocialService("com.umeng.share");

	private void onShareClick() {
		addWXPlatform();
		addQQPlatform();
		addSinaPlatform();
		addQQZonePlatform();
		// 设置分享视频
		UMVideo umVideo = new UMVideo(getPlayUrl());
		// 设置视频缩略图
		umVideo.setThumb(recommendVideo.getImg());
		umVideo.setTitle("视频神器分享!");
		mController.setShareMedia(umVideo);
		// 设置分享文字内容
		mController.setShareContent(recommendVideo.getTitle()+"终于让我找到了，小伙伴们快来看吧");
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
									Utils.getDeviceId(FeatureAreaVideoDetailActivity.this), response.getScore());
						}
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
					}

				});
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
}
