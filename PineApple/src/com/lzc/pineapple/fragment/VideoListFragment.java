package com.lzc.pineapple.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lzc.pineapple.BaiduPlayActivity;
import com.lzc.pineapple.R;
import com.lzc.pineapple.VideoDetailActivity;
import com.lzc.pineapple.VideoPlayActivity;
import com.lzc.pineapple.db.PlayCacheInfo;
import com.lzc.pineapple.entity.PlatInfo;
import com.lzc.pineapple.entity.RealVideo;
import com.lzc.pineapple.entity.RealVideoInfo;
import com.lzc.pineapple.entity.VideoBasic;
import com.lzc.pineapple.entity.VideoInfoEntity;
import com.lzc.pineapple.util.Constant;
import com.lzc.pineapple.util.NetworkRequest;
import com.lzc.pineapple.util.UrlDecodeUtils;
import com.lzc.pineapple.util.UrlHelper;
import com.lzc.pineapple.util.Utils;
import com.lzc.pineapple.volley.Response;
import com.lzc.pineapple.volley.VolleyError;


public class VideoListFragment extends Fragment{
	private final static String VIDEO_INFO_KEY = "video_info_key";
	private final static String SOURCE_TYPE = "source_type";
	private VideoInfoEntity videoInfo;
	private int currentSource;
	private ListView listview;
	private List<PlatInfo> list = new ArrayList<PlatInfo>();
	private GramaListAdapter adapter;
	
	private ProgressDialog progressDialog;
	private RealVideoInfo   realVideoInfo;
	private int currentQualityPos;
	private static final String CURR_QUALITY_POS_KEY = "curr_quality_pos_key";
	
	private boolean isRefresh = false;
	
	private Activity activity;
	
	public static final Fragment newInstance(VideoInfoEntity videoInfo,int currentSource,int currentQualityPos) {
        Fragment fragment = new VideoListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(VIDEO_INFO_KEY, videoInfo);
        bundle.putInt(SOURCE_TYPE, currentSource);
        bundle.putInt(CURR_QUALITY_POS_KEY, currentQualityPos);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO aAuto-generated method stub
    	getValues();
        View view = inflater.inflate(R.layout.fragment_video_detail_list_layout, null);
        activity = getActivity();
        initViews(view);
        return view;
    }
    private void getValues(){
    	videoInfo = (VideoInfoEntity) this.getArguments().getSerializable(VIDEO_INFO_KEY);
    	currentSource = this.getArguments().getInt(SOURCE_TYPE);
    	list = videoInfo.getmPlatUrl().getPlatInfos(currentSource);
    	if(list == null){
    		list = new ArrayList<PlatInfo>();
    	}
    	currentQualityPos = this.getArguments().getInt(CURR_QUALITY_POS_KEY);
    }
    private void initViews(View view){
    	listview = (ListView)view.findViewById(R.id.listview);
    	listview.setOnItemClickListener(onItemClickListener);
    	adapter = new GramaListAdapter();
    	listview.setAdapter(adapter);
    }
    final OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			requestRealUrl(position);
		}
	};
	private void requestRealUrl(final int position){
		String url = list.get(position).getsUrl();
    	progressDialog = Utils.showProgress(getActivity(), null, "正在获取播放地址，请稍后", true, true);
    	isRefresh = true;
		String requestUrl = UrlHelper.URL_SOURCE_URL;
		StringBuilder sb = new StringBuilder();
		sb.append("&type=0");
		sb.append("&url=").append(url);
		requestUrl += sb.toString();
		Log.d("lzc","requestUrl==>"+requestUrl);
		NetworkRequest.get(requestUrl, RealVideoInfo.class,
				new Response.Listener<RealVideoInfo>() {

					@Override
					public void onResponse(final RealVideoInfo response) {
						Utils.dismissDialog(progressDialog);
						isRefresh = false;
						realVideoInfo = response;
						playVideo(position);
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						isRefresh = false;
						Utils.dismissDialog(progressDialog);
						playH5(position);
					}

				});
    }
	private void playH5(int position){
		VideoBasic videoBasic = videoInfo.getStVideoBasic();
    	PlayCacheInfo playCacheInfo = new PlayCacheInfo();
		playCacheInfo.setType(1);
		playCacheInfo.setCover(videoBasic.getsPicUrl());
		playCacheInfo.setVideoName(videoBasic.getsVideoName());
		playCacheInfo.setTime(Utils.getSysNowTime());
		playCacheInfo.setVideoId(""+videoBasic.getlVideoId());
		
		VideoPlayActivity.launch(activity, list.get(position).getsUrl(),playCacheInfo);
	}
	private void playVideo(int position) {
		if(realVideoInfo != null && realVideoInfo.getRows().size() > 0){
			List<RealVideo> listVideo = realVideoInfo.getRows().get(currentQualityPos).getVideos();
			if(listVideo.size() == 1){
				BaiduPlayActivity.launch(activity, UrlDecodeUtils.decrypt(Constant.key, listVideo.get(0).getUrl()));
			}else if(listVideo.size() > 1){
				BaiduPlayActivity.launch(activity, realVideoInfo,currentQualityPos);
			}else{
				Utils.showTips(activity, "无法播放");
			}
			
		}else{
			playH5(position);
		}
	}
    class GramaListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View view, ViewGroup arg2) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if(view == null){
				holder = new ViewHolder();
				view = View.inflate(getActivity(),R.layout.search_cache_list_item_layout, null);
				holder.text = (TextView)view.findViewById(R.id.text);
				view.setTag(holder);
			}else{
				holder = (ViewHolder) view.getTag();
			}
			holder.text.setText(""+list.get(position).getsBrief());
			return view;
		}
    }
    class ViewHolder{
    	TextView text;
    }
}
