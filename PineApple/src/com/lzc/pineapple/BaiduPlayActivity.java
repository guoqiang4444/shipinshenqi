package com.lzc.pineapple;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.baidu.cyberplayer.core.BMediaController;
import com.baidu.cyberplayer.core.BVideoView;
import com.baidu.cyberplayer.core.BVideoView.OnCompletionListener;
import com.baidu.cyberplayer.core.BVideoView.OnErrorListener;
import com.baidu.cyberplayer.core.BVideoView.OnInfoListener;
import com.baidu.cyberplayer.core.BVideoView.OnPlayingBufferCacheListener;
import com.baidu.cyberplayer.core.BVideoView.OnPreparedListener;
import com.bbs.os.Ppbcqq;
import com.lzc.pineapple.entity.ConsumEntity;
import com.lzc.pineapple.entity.RealVideo;
import com.lzc.pineapple.entity.RealVideoInfo;
import com.lzc.pineapple.entity.UserSearchEntity;
import com.lzc.pineapple.util.Constant;
import com.lzc.pineapple.util.NetworkRequest;
import com.lzc.pineapple.util.UrlDecodeUtils;
import com.lzc.pineapple.util.UrlHelper;
import com.lzc.pineapple.util.Utils;
import com.lzc.pineapple.util.Utils.OnDialogDoneListener;
import com.lzc.pineapple.volley.Response;
import com.lzc.pineapple.volley.VolleyError;
import com.umeng.analytics.MobclickAgent;

public class BaiduPlayActivity extends BaseFragmentActivity implements OnPreparedListener,
		OnCompletionListener, OnErrorListener, OnInfoListener,
		OnPlayingBufferCacheListener {
	private final String TAG = "lzc";

	/**
	 * 您的ak
	 */
	private String AK = "t207G26112s0cbhYjtDuxBP7";
	/**
	 * //您的sk的前16位
	 */
	private String SK = "9s4Eof8Os1zvEht4ybem6U64Sw0R3Xtc";

	private String mVideoSource = null;
	
	private List<RealVideo> listVideo = new ArrayList<RealVideo>();

	private BVideoView mVV = null;
	private BMediaController mVVCtl = null;
	private RelativeLayout mViewHolder = null;
	private LinearLayout mControllerHolder = null;
	
	private ProgressDialog progressDialog;
	
	private RealVideoInfo   realVideoInfo;
	private int currentQualityPos = 0;//当前选中播放的视频清晰度
	private int currentPlayPos    = 0;//如果是分片播放的话，记录当前播放的分片index
	private final static String REAL_VIDEO_INFO_KEY = "real_video_info_key";
	private final static String CURR_QUALITY_POS_KEY = "curr_quality_pos_key";
	private final static String CURR_PLAY_INDEX_KEY = "curr_play_index_key";
	private final static String LAST_PLAY_POS_KEY = "last_play_pos_key";
	private final static String IS_PLAYED_KEY = "is_play_key";
	private final static String PRICE_KEY = "price_key";
	private final static String VIDEO_ID_KEY = "video_id_key";

	private boolean mIsHwDecode = false;
	private boolean isActivityOnPause = false;

	private EventHandler mEventHandler;
	private HandlerThread mHandlerThread;

	private final Object SYNC_Playing = new Object();

	private final int EVENT_PLAY = 0;

	private WakeLock mWakeLock = null;
	private static final String POWER_LOCK = "VideoViewPlayingActivity";
	
	/**
	 * 播放状态
	 */
	private enum PLAYER_STATUS {
		PLAYER_IDLE, PLAYER_PREPARING, PLAYER_PREPARED,
	}

	private PLAYER_STATUS mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
	
	private int price;
	
	private int videoId;

	/**
	 * 记录播放位置
	 */
	private int mLastPos = 0;
	
	private int slicePos = 0;//分片播放的位置
	
	private Handler handler = new Handler();
	
	private final static int FREE_VIEW_TIME = 60 * 5 ;//单位是秒,五分钟
	
	private final static int DELAY_TIME = 1000 * FREE_VIEW_TIME;//
	
	private final static int  PERIOD_TIME = 1000 * 5;//五秒检查一次
	
	private Timer timer = new Timer(true);
	
	private TimerTask task = new TimerTask() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int pos = mVV.getCurrentPosition();
			if(pos >= FREE_VIEW_TIME){
				stopTimer();
				pausePlay();
				handler.post(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						showPayDialog();
					}
					
				});
			}
		}
	}; 
    private void pausePlay(){
    	mVV.pause();
    }
    private void activeFinishActivity(){
    	mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
		finish();
    }
    private void showPayDialog(){
    	int userScore = Utils.getPreferenceInt(getApplicationContext(), Utils.getDeviceId(this));
    	if(userScore < price){
    		String msg = "当前积分不够支付本次播放所需积分，是否免费获取积分？";
    		Utils.showDialog(this, msg,false, new OnDialogDoneListener(){

				@Override
				public void onDialogDone() {
					// TODO Auto-generated method stub
					Ppbcqq.getInstance(BaiduPlayActivity.this).comffnet();
					activeFinishActivity();
				}

				@Override
				public void onDialogCancel() {
					// TODO Auto-generated method stub
					activeFinishActivity();
				}
    		});
    	}else{
    		String msg = "是否支付积分继续观看？";
    		Utils.showDialog(this, msg,false, new OnDialogDoneListener(){

				@Override
				public void onDialogDone() {
					// TODO Auto-generated method stub
					commitConsumerScore();
				}

				@Override
				public void onDialogCancel() {
					// TODO Auto-generated method stub
					activeFinishActivity();
				}
    			
    		});
    	}
    }
    private void commitConsumerScore(){
    	progressDialog = Utils.showProgress(getApplicationContext(),
				null, "支付中", true, true);
    	StringBuilder sb = new StringBuilder();
		sb.append("&user_id=").append(Utils.getDeviceId(this));
		sb.append("&device_id=").append(Utils.getDeviceId(this));
		sb.append("&user_type=4&user_site=0");
		sb.append("&pay_id="+videoId);
		sb.append("&pay_score="+price);
		sb.append("&pay_type=3");
		String url = UrlHelper.URL_CONSUMER_SCORE + sb.toString();
		NetworkRequest.get(url, ConsumEntity.class,
				new Response.Listener<ConsumEntity>() {

					@Override
					public void onResponse(ConsumEntity response) {
						// TODO Auto-generated method stub
						Utils.dismissDialog(progressDialog);
						if(response.getRet_code() == 0){//支付成功
							mVV.resume();
						}else{
							 Utils.showTips(getApplicationContext(), "支付失败");
							 activeFinishActivity();
						}
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Utils.dismissDialog(progressDialog);
                        Utils.showTips(getApplicationContext(), "支付失败");
                        activeFinishActivity();
					}

				});
    }
	class EventHandler extends Handler {
		public EventHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case EVENT_PLAY:
				/**
				 * 如果已经播放了，等待上一次播放结束
				 */
				if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
					synchronized (SYNC_Playing) {
						try {
							SYNC_Playing.wait();
							Log.v(TAG, "wait player status to idle");
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				if(mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE){
					return;
				}
                if(mVideoSource != null){
                	/**
    				 * 设置播放url
    				 */
    				mVV.setVideoPath(mVideoSource);
    				if(price > 0){
    					startTimer();
    				}
    				
                }else{
                	String url = listVideo.get(currentPlayPos).getUrl();
                	url = UrlDecodeUtils.decrypt(Constant.key, url);
                	Uri uriPath = Uri.parse(url);
                	String videoSource = null;
            		if (null != uriPath) {
            			String scheme = uriPath.getScheme();
            			if (null != scheme) {
            				videoSource = uriPath.toString();
            			} else {
            				videoSource = uriPath.getPath();
            			}
            			mVV.setVideoPath(videoSource);
            		}else{
            			Utils.showTips(getApplicationContext(), "播放出错");
            			finish();
            		}
                }
                /**
				 * 续播，如果需要如此
				 */
				if (mLastPos > 0) {

					mVV.seekTo(mLastPos);
					mLastPos = 0;
				}
				/**
				 * 显示或者隐藏缓冲提示
				 */
				mVV.showCacheInfo(true);

				/**
				 * 开始播放
				 */
				mVV.start();

				mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARING;
				break;
			default:
				break;
			}
		}
	}
	
	
    private void startTimer(){
    	timer.schedule(task, DELAY_TIME, PERIOD_TIME);
    }
    private void stopTimer(){
    	 if (timer != null) {  
    		 timer.cancel();  
    		 timer = null;  
         }  
   
         if (task != null) {  
        	 task.cancel();  
        	 task = null;  
         }     
    }
	/**
	 * 实现切换示例
	 */
	private View.OnClickListener mPreListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.v(TAG, "pre btn clicked");
			/**
			 * 如果已经开发播放，先停止播放
			 */
			if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
				mVV.stopPlayback();
			}

			/**
			 * 发起一次新的播放任务
			 */
			if (mEventHandler.hasMessages(EVENT_PLAY))
				mEventHandler.removeMessages(EVENT_PLAY);
			mEventHandler.sendEmptyMessage(EVENT_PLAY);
		}
	};

	private View.OnClickListener mNextListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.v(TAG, "next btn clicked");
		}
	};
    public static void launch(Context context,String url){
    	Intent intent = new Intent(context, BaiduPlayActivity.class);
		intent.setData(Uri.parse(url));
		context.startActivity(intent);
    }
    public static void launch(Context context,Uri uri){
    	Intent intent = new Intent(context, BaiduPlayActivity.class);
		intent.setData(uri);
		context.startActivity(intent);
    }
    public static void launch(Context context,String url,int price,int videoId){
    	Intent intent = new Intent(context, BaiduPlayActivity.class);
		intent.setData(Uri.parse(url));
		intent.putExtra(PRICE_KEY, price);
		intent.putExtra(VIDEO_ID_KEY, videoId);
		context.startActivity(intent);
    }
    public static void launch(Context context,RealVideoInfo realVideoInfo,int currentQualityPos){
    	Intent intent = new Intent(context, BaiduPlayActivity.class);
		intent.putExtra(REAL_VIDEO_INFO_KEY, realVideoInfo);
		intent.putExtra(CURR_QUALITY_POS_KEY, currentQualityPos);
		context.startActivity(intent);
    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_bd_playing_layout);

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
				| PowerManager.ON_AFTER_RELEASE, POWER_LOCK);

		getIntentValues();

		initUI();

		/**
		 * 开启后台事件处理线程
		 */
		mHandlerThread = new HandlerThread("event handler thread",
				Process.THREAD_PRIORITY_BACKGROUND);
		mHandlerThread.start();
		mEventHandler = new EventHandler(mHandlerThread.getLooper());
	}
    private void getIntentValues(){
    	Intent intent = getIntent();
    	mIsHwDecode =intent.getBooleanExtra("isHW", false);
    	price = intent.getIntExtra(PRICE_KEY, 0);
    	videoId = intent.getIntExtra(VIDEO_ID_KEY, -1);
		Uri uriPath = intent.getData();
		if (null != uriPath) {
			String scheme = uriPath.getScheme();
			if (null != scheme) {
				mVideoSource = uriPath.toString();
			} else {
				mVideoSource = uriPath.getPath();
			}
		}else{
			realVideoInfo = (RealVideoInfo) intent.getSerializableExtra(REAL_VIDEO_INFO_KEY);
			currentQualityPos = intent.getIntExtra(CURR_QUALITY_POS_KEY, 0);
			if(realVideoInfo != null){
				listVideo = realVideoInfo.getRows().get(currentQualityPos).getVideos();
			}
		}
    }
	/**
	 * 初始化界面
	 */
	private void initUI() {
		mViewHolder = (RelativeLayout) findViewById(R.id.view_holder);
		mControllerHolder = (LinearLayout) findViewById(R.id.controller_holder);

		/**
		 * 设置ak及sk的前16位
		 */
		BVideoView.setAKSK(AK, SK);

		/**
		 * 创建BVideoView和BMediaController
		 */
		mVV = new BVideoView(this);
		mVVCtl = new BMediaController(this);
		mViewHolder.addView(mVV);
		mControllerHolder.addView(mVVCtl);

		/**
		 * 注册listener
		 */
		mVV.setOnPreparedListener(this);
		mVV.setOnCompletionListener(this);
		mVV.setOnErrorListener(this);
		mVV.setOnInfoListener(this);
		mVVCtl.setPreNextListener(mPreListener, mNextListener);

		/**
		 * 关联BMediaController
		 */
		mVV.setMediaController(mVVCtl);
		/**
		 * 设置解码模式
		 */
		mVV.setDecodeMode(BVideoView.DECODE_SW);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d(TAG, "onPause");
		MobclickAgent.onPause(this);
		/**
		 * 在停止播放前 你可以先记录当前播放的位置,以便以后可以续播
		 */
		if (mPlayerStatus == PLAYER_STATUS.PLAYER_PREPARED) {
			mLastPos = mVV.getCurrentPosition();
			savePlayStatus();
			mVV.stopPlayback();
		}
		stopTimer();
		
		isActivityOnPause = true;
	}
   
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		Log.v(TAG, "onResume");
		if (null != mWakeLock && (!mWakeLock.isHeld())) {
			mWakeLock.acquire();
		}
		initPlayInfoValues();
		/**
		 * 发起一次播放任务,当然您不一定要在这发起
		 */
		mEventHandler.sendEmptyMessage(EVENT_PLAY);
	}
    private void initPlayInfoValues(){
    	if(mVideoSource != null){
    		mLastPos = Utils.getPreferenceInt(this, mVideoSource);
    	}else{
    		String url = listVideo.get(0).getUrl();
        	url = UrlDecodeUtils.decrypt(Constant.key, url);
    		boolean isPlayed = Utils.getDefaultFalsePrefrence(this, url);
    		if(isPlayed){
    			currentPlayPos = Utils.getPreferenceInt(this, CURR_PLAY_INDEX_KEY, currentPlayPos);
    			mLastPos = Utils.getPreferenceInt(this, LAST_PLAY_POS_KEY, 0);
    		}
    	}
    }
    private void savePlayStatus(){
    	if(mVideoSource != null){
    		Utils.savePreferenceInt(this, mVideoSource, mLastPos);
    	}else{
    		String url = listVideo.get(0).getUrl();
        	url = UrlDecodeUtils.decrypt(Constant.key, url);
        	Utils.setBooleanPrefrence(this,url,true);
        	Utils.savePreferenceInt(this, CURR_PLAY_INDEX_KEY, currentPlayPos);
        	Utils.savePreferenceInt(this, LAST_PLAY_POS_KEY, mLastPos);
    	}
    }
	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "onStop");
	}

	@SuppressLint("NewApi")
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != mWakeLock && (mWakeLock.isHeld())) {
			mWakeLock.release();
		}
		/**
		 * 结束后台事件处理线程
		 */
		mHandlerThread.quit();
		Log.d(TAG, "onDestroy");
	}

	@Override
	public boolean onInfo(int what, int extra) {
		// TODO Auto-generated method stub
		switch (what) {
		/**
		 * 开始缓冲
		 */
		case BVideoView.MEDIA_INFO_BUFFERING_START:
			break;
		/**
		 * 结束缓冲
		 */
		case BVideoView.MEDIA_INFO_BUFFERING_END:
			break;
		default:
			break;
		}
		return false;
	}

	/**
	 * 当前缓冲的百分比， 可以配合onInfo中的开始缓冲和结束缓冲来显示百分比到界面
	 */
	@Override
	public void onPlayingBufferCache(int percent) {
		// TODO Auto-generated method stub

	}

	/**
	 * 播放出错
	 */
	@Override
	public boolean onError(int what, int extra) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onError");
		synchronized (SYNC_Playing) {
			SYNC_Playing.notify();
		}
		mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
		finish();
		return false;
	}

	/**
	 * 播放完成
	 */
	@Override
	public void onCompletion() {
		// TODO Auto-generated method stub
		Log.v(TAG, "onCompletion");

		synchronized (SYNC_Playing) {
			SYNC_Playing.notify();
		}
		mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
		if(isPlayNext()){
			currentPlayPos ++;
			/**
			 * 发起一次新的播放任务
			 */
			if (mEventHandler.hasMessages(EVENT_PLAY))
				mEventHandler.removeMessages(EVENT_PLAY);
			mEventHandler.sendEmptyMessage(EVENT_PLAY);
		}
		
	}
	private boolean isPlayNext(){
		return mVideoSource == null && currentPlayPos < (listVideo.size() - 1) && !isActivityOnPause;
	}

	/**
	 * 播放准备就绪
	 */
	@Override
	public void onPrepared() {
		// TODO Auto-generated method stub
		Log.v(TAG, "onPrepared");
		mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARED;
	}
}
