package com.lzc.pineapple;

import com.bbs.Ppacqq;
import com.lzc.pineapple.entity.RegisterEntity;
import com.lzc.pineapple.entity.StartPageEntity;
import com.lzc.pineapple.entity.UserSearchEntity;
import com.lzc.pineapple.util.Constant;
import com.lzc.pineapple.util.NetworkRequest;
import com.lzc.pineapple.util.UrlHelper;
import com.lzc.pineapple.util.Utils;
import com.lzc.pineapple.volley.Response;
import com.lzc.pineapple.volley.VolleyError;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class SplashActivity extends Activity {
	private Handler handler = new Handler();
	private static final String REGISTER_CODE_KEY = "register_code_key";
	private int registerCode = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_layout);
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		UmengUpdateAgent.update(this);
		MobclickAgent.updateOnlineConfig(this);
		MobclickAgent.setDebugMode(true);
		try {
			Constant.key = Integer.parseInt(MobclickAgent.getConfigParams(this,
					"key_string"));
		} catch (Exception e) {
		}
		initYoumi();
		initRegisterCode();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private void initYoumi() {
		// 初始化接口，应用启动的时候调用
		// 参数：appId, appSecret, 调试模式
		Ppacqq.getInstance(this).init("deec1370599126a7", "38becb22ebad625e",false);
	}

	private void initRegisterCode() {
		registerCode = Utils.getPreferenceInt(this, REGISTER_CODE_KEY, -100);
		if (registerCode == 0 || registerCode == -2) {// 如果已经注册或者用户已经存在就不再注册
			getUserScore();
		} else {
			register();
		}
	}

	private void register() {
		StringBuilder sb = new StringBuilder();
		sb.append("&user_id=").append(Utils.getDeviceId(this));
		sb.append("&device_id=").append(Utils.getDeviceId(this));
		sb.append("&user_type=4&user_site=0");
		NetworkRequest.get(UrlHelper.URL_USER_REGISTER + sb.toString(),
				RegisterEntity.class, new Response.Listener<RegisterEntity>() {

					@Override
					public void onResponse(RegisterEntity response) {
						// TODO Auto-generated method stub
						handleRegister(response);
						getUserScore();
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						getUserScore();
						Utils.showTips(getApplicationContext(), "注册失败");
					}

				});
	}

	private void handleRegister(RegisterEntity registerEntity) {
		int retCode = registerEntity.getRet_code();
		Utils.savePreferenceInt(this, REGISTER_CODE_KEY, retCode);
		switch (retCode) {
		case 0:
			Utils.showTips(this, "注册成功");
			break;
		case -1:
			Utils.showTips(this, "输入参数不全");
			break;
		case -2:
			Utils.showTips(this, "用户已存在");
			break;
		default:
			break;
		}

	}

	private void delayGetUserScore() {
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				getUserScore();
			}

		}, 500);
	}

	private void getUserScore() {
		StringBuilder sb = new StringBuilder();
		sb.append("&user_id=").append(Utils.getDeviceId(this));
		sb.append("&device_id=").append(Utils.getDeviceId(this));
		sb.append("&user_type=4&user_site=0");
		String url = UrlHelper.URL_USER_SEARCH + sb.toString();
		NetworkRequest.get(url, UserSearchEntity.class,
				new Response.Listener<UserSearchEntity>() {

					@Override
					public void onResponse(UserSearchEntity response) {
						// TODO Auto-generated method stub
						Utils.savePreferenceInt(getApplicationContext(),
								Utils.getDeviceId(SplashActivity.this),
								response.getScore());
						launchMainActivity();
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						launchMainActivity();
					}

				});
	}

	// private void delayLaunchMainActivity(){
	//
	// handler.postDelayed(new Runnable() {
	//
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// launchMainActivity();
	// }
	//
	// }, 500);
	//
	// }
	private void launchMainActivity() {
		MainActivity.launch(SplashActivity.this);
		finish();
	}
}
