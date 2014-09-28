package com.lzc.pineapple;

import com.lzc.pineapple.db.PlayCacheInfo;
import com.lzc.pineapple.db.PlayHistoryDBHelper;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 视频播放界面
 * @author zengchan.lzc
 *
 */
@SuppressLint("JavascriptInterface")
public class VideoPlayActivity extends BaseFragmentActivity implements OnClickListener{
    private static final String URL_KEY = "url_key";
    private static final String VIDEO_NAME_KEY = "video_name_key";
    private static final String PLAY_CACHE_KEY = "play_cache_key";
    
    private ImageView back;
    private TextView  title;
    
    private WebView webview;
    
    private Handler handler = new Handler();
    
    private String url;
    private String videoName;
    private PlayCacheInfo playCacheInfo;
    
    private PlayHistoryDBHelper dbHelper;
    
    
//    public static void launch(Context c,String url,String name) {
//        Intent intent = new Intent(c, VideoPlayActivity.class);
//        intent.putExtra(URL_KEY, url);
//        intent.putExtra(VIDEO_NAME_KEY, name);
//        c.startActivity(intent);
//    }
    public static void launch(Context c,String url,PlayCacheInfo playCacheInfo) {
        Intent intent = new Intent(c, VideoPlayActivity.class);
        intent.putExtra(URL_KEY, url);
        intent.putExtra(PLAY_CACHE_KEY, playCacheInfo);
        c.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        openDBhelper();
        setContentView(R.layout.activity_video_play_activity);
        getIntentValues();
        initViews();
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
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        autoStop();
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        closeDBHelper();
    }
    private void openDBhelper() {
        dbHelper = new PlayHistoryDBHelper(this);
        dbHelper.open();
    }

    private void closeDBHelper() {
        dbHelper.close();
    }
    private void getIntentValues(){
        Intent intent = getIntent();
        url = intent.getStringExtra(URL_KEY);
        playCacheInfo = (PlayCacheInfo) intent.getSerializableExtra(PLAY_CACHE_KEY);
        videoName = playCacheInfo.getVideoName();
        
        dbHelper.insertPlayVideoDataToTable(playCacheInfo);
    }
    private void initViews(){
        back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);
        title = (TextView)findViewById(R.id.title);
        title.setText(""+videoName);
        
        webview = (WebView)findViewById(R.id.webview);
        webviewSetting();
    }
    private void webviewSetting(){
        webview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        WebSettings setting = webview.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setSupportZoom(true);
        setting.setBuiltInZoomControls(true);
        setting.setAllowFileAccess(true);
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
        setting.setAppCacheEnabled(true);
        setting.setLoadsImagesAutomatically(true);
        setting.setLightTouchEnabled(true);
        setting.setPluginState(PluginState.ON);
        webview.addJavascriptInterface(new VideoPlayListenerJavaScriptInterface(), "video_play_listener");
        webview.setWebChromeClient(webChromeClient);
        webview.setWebViewClient(webviewClient);
        webview.setOnKeyListener(onKeyListener);
        
        webview.loadUrl(url);
    }
    final WebViewClient webviewClient = new WebViewClient(){
        @Override
        public void onPageFinished(WebView view, String url) {
            handler.postDelayed(new Runnable() {
                
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    playListener();
                    autoPlay();
                    
                }
            }, 1000);
        };
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }
    };
    final WebChromeClient webChromeClient = new WebChromeClient(){
        
        private View               myView     = null;
        private CustomViewCallback myCallback = null;
        
        public void onShowCustomView(View view, CustomViewCallback callback) {
              if (myCallback != null) {
                  myCallback.onCustomViewHidden();
                  myCallback = null;
                  return;
              }
    
              long id = Thread.currentThread().getId();
    
              ViewGroup parent = (ViewGroup) webview.getParent();
              String s = parent.getClass().getName();
              parent.removeView(webview);
              parent.addView(view);
              myView = view;
              myCallback = callback;
      }
      
      public void onHideCustomView() {

          long id = Thread.currentThread().getId();

          if (myView != null) {

              if (myCallback != null) {
                  myCallback.onCustomViewHidden();
                  myCallback = null;
              }

              ViewGroup parent = (ViewGroup) myView.getParent();
              parent.removeView(myView);
              parent.addView(webview);
              myView = null;
          }
      }

    };
    final OnKeyListener onKeyListener = new OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if ((keyCode == KeyEvent.KEYCODE_BACK) && webview != null && webview.canGoBack()) {
                webview.goBack();
                return true;
            }
            return false;
        }
    };
    final class VideoPlayListenerJavaScriptInterface {
        VideoPlayListenerJavaScriptInterface() {
        }

        public void clickonAndroid() {
            //video play start

        }

        public void endonAndroid() {
            //video play end
        }
    }
    private void autoStop(){
        webview.onPause();
//        mWebView.loadUrl("file:///android_asset/nonexistent.html");
    }
    private void autoPlay(){
        String js="javascript: var v=document.getElementsByTagName('video')[0]; "
                +"v.play(); " 
                ;
        webview.loadUrl(js);
    }
    private void autoFullscreenPlay(){
        String js="javascript: var v=document.getElementsByTagName('video')[0]; "+"v.webkitEnterFullscreen(); ";
        webview.loadUrl(js);
    }
    private void playListener(){
        String js="javascript: var v=document.getElementsByTagName('video')[0]; "
                +"v.addEventListener('playing', function() { window.video_play_listener.clickonAndroid(); }, true); ";
        webview.loadUrl(js);
    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.back:
                onBackClick();
                break;

            default:
                break;
        }
    }
    private void onBackClick(){
        onBackPressed();
    }
}
