package com.lzc.pineapple.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bbs.os.Ppbcqq;
import com.lzc.pineapple.CopyrightActivity;
import com.lzc.pineapple.FeedbackActivity;
import com.lzc.pineapple.MainActivity;
import com.lzc.pineapple.PersonalCollectionActivity;
import com.lzc.pineapple.PlayHistoryActivity;
import com.lzc.pineapple.R;
import com.lzc.pineapple.SplashActivity;
import com.lzc.pineapple.cache.OfflineCacheActivity;
import com.lzc.pineapple.entity.UserSearchEntity;
import com.lzc.pineapple.util.Constant;
import com.lzc.pineapple.util.NetworkRequest;
import com.lzc.pineapple.util.UrlHelper;
import com.lzc.pineapple.util.Utils;
import com.lzc.pineapple.util.Utils.OnDialogDoneListener;
import com.lzc.pineapple.volley.Response;
import com.lzc.pineapple.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;

/**
 * 个人中心展示
 * 
 * @author zengchan.lzc
 */
public class ProfileFragment extends BaseTabFragment implements OnClickListener {
	private RelativeLayout offlineCacheLayout;
	private RelativeLayout playHistoryLayout;
	private RelativeLayout personalCollectionLayout;
	private RelativeLayout clearCacheLayout;
	private CheckBox cacheOnlyWifi;
	private RelativeLayout feedbackLayout;
	private RelativeLayout copyrightLayout;
	private RelativeLayout checkUpdateLayout;
	private RelativeLayout getScoreLayout;

	private TextView pointBalance;

	private Activity activity;

	public static final Fragment newInstance() {
		Fragment fragment = new ProfileFragment();
		Bundle bundle = new Bundle();
		// bundle.putString(Constant.DEVICE_NAME_KEY, deviceName);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_profile_layout, null);
		activity = getActivity();
		getUserScore();
		initViews(view);
		setListener();
		return view;
	}

	private void getUserScore() {
		StringBuilder sb = new StringBuilder();
		sb.append("&user_id=").append(Utils.getDeviceId(activity));
		sb.append("&device_id=").append(Utils.getDeviceId(activity));
		sb.append("&user_type=4&user_site=0");
		String url = UrlHelper.URL_USER_SEARCH + sb.toString();
		NetworkRequest.get(url, UserSearchEntity.class,
				new Response.Listener<UserSearchEntity>() {

					@Override
					public void onResponse(UserSearchEntity response) {
						// TODO Auto-generated method stub
						Utils.savePreferenceInt(
								activity.getApplicationContext(),
								Utils.getDeviceId(activity),
								response.getScore());
						updateScore();
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub

					}

				});
	}

	private void initViews(View view) {
		offlineCacheLayout = (RelativeLayout) view
				.findViewById(R.id.offline_cache_layout);
		playHistoryLayout = (RelativeLayout) view
				.findViewById(R.id.play_history_layout);
		personalCollectionLayout = (RelativeLayout) view
				.findViewById(R.id.personal_collection_layout);
		clearCacheLayout = (RelativeLayout) view
				.findViewById(R.id.clear_cache_layout);
		cacheOnlyWifi = (CheckBox) view.findViewById(R.id.cache_only_wifi_box);
		boolean isOnlyWifiChecked = Utils.getBooleanPrefrence(activity.getApplicationContext(),
				Constant.CACHE_ONLY_WIFI_KEY);
		cacheOnlyWifi.setChecked(isOnlyWifiChecked);
		feedbackLayout = (RelativeLayout) view
				.findViewById(R.id.feedback_layout);
		copyrightLayout = (RelativeLayout) view
				.findViewById(R.id.copyright_layout);
		checkUpdateLayout = (RelativeLayout) view
				.findViewById(R.id.check_update_layout);
		getScoreLayout = (RelativeLayout) view
				.findViewById(R.id.get_score_layout);

		pointBalance = (TextView) view.findViewById(R.id.my_score_text);
		updateScore();
	}
    private void updateScore(){
    	int score = Utils.getPreferenceInt(activity.getApplicationContext(), Utils.getDeviceId(activity));
		pointBalance.setText(getString(R.string.my_score,score));
    }
	private void setListener() {
		offlineCacheLayout.setOnClickListener(this);
		playHistoryLayout.setOnClickListener(this);
		personalCollectionLayout.setOnClickListener(this);
		clearCacheLayout.setOnClickListener(this);
		feedbackLayout.setOnClickListener(this);
		copyrightLayout.setOnClickListener(this);
		checkUpdateLayout.setOnClickListener(this);
		getScoreLayout.setOnClickListener(this);
		cacheOnlyWifi.setOnCheckedChangeListener(checkedChangeListener);
	}

	public void updatePointBalance() {
		
	}

	final OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			Utils.setBooleanPrefrence(activity.getApplicationContext(), Constant.CACHE_ONLY_WIFI_KEY, isChecked);
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.offline_cache_layout:
			onOfflineCacheClick();
			break;
		case R.id.play_history_layout:
			onPlayHistoryClick();
			break;
		case R.id.personal_collection_layout:
			onPersonalCollectionClick();
			break;
		case R.id.clear_cache_layout:
			onClearCacheClick();
			break;
		case R.id.feedback_layout:
			onFeedbackClick();
			break;
		case R.id.copyright_layout:
			onCopyrightClick();
			break;
		case R.id.check_update_layout:
			onCheckUpdateClick();
			break;
		case R.id.get_score_layout:
			onGetScoreClick();
			break;
		default:
			break;
		}
	}

	private void onOfflineCacheClick() {
		OfflineCacheActivity.launch(activity);
	}

	private void onPlayHistoryClick() {
		PlayHistoryActivity.launch(activity);
	}

	private void onPersonalCollectionClick() {
		PersonalCollectionActivity.launch(activity);
	}

	private void onClearCacheClick() {
		Utils.showDialog(
				getActivity(),
				getActivity().getResources().getString(
						R.string.clear_cache_conform),
				new OnDialogDoneListener() {

					@Override
					public void onDialogDone() {
						// TODO Auto-generated method stub
						ImageLoader.getInstance().clearDiscCache();
					}

					@Override
					public void onDialogCancel() {
						// TODO Auto-generated method stub
						
					}

				});

	}

	private void onFeedbackClick() {
		FeedbackAgent agent = new FeedbackAgent(activity);
		agent.startFeedbackActivity();
	}

	private void onCopyrightClick() {
		CopyrightActivity.launch(activity);
	}

	private void onCheckUpdateClick() {
		UmengUpdateAgent.setDefault();
		UmengUpdateAgent.forceUpdate(activity);
	}

	private void onGetScoreClick() {
		Ppbcqq.getInstance(activity).comffnet();
	}
}
