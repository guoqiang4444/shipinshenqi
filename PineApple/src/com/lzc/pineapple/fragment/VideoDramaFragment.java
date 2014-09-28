package com.lzc.pineapple.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.lzc.pineapple.BaiduPlayActivity;
import com.lzc.pineapple.R;
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

/**
 * 视频剧集列表Fragment
 * 
 * @author zengchan.lzc
 */
public class VideoDramaFragment extends Fragment {
    private GridView       gridview;
    private GridAdapter    adapter;
    private LayoutInflater inflater;
    
    private final static String VIDEO_INFO_KEY = "video_info_key";
    private final static String SOURCE_TYPE = "source_type";
	private VideoInfoEntity videoInfo;
	
	private int currentSource;
	private int currentQualityPos;
	private boolean isRefresh = false;
	private ProgressDialog progressDialog;
	private RealVideoInfo   realVideoInfo;
	private Activity activity;
	
	private static final String CURR_QUALITY_POS_KEY = "curr_quality_pos_key";
	
	private List<PlatInfo> list = new ArrayList<PlatInfo>();

    public static final Fragment newInstance(VideoInfoEntity videoInfo,int currentSource,int currentQualityPos) {
        Fragment fragment = new VideoDramaFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(VIDEO_INFO_KEY, videoInfo);
        bundle.putInt(SOURCE_TYPE, currentSource);
        bundle.putInt(CURR_QUALITY_POS_KEY, currentQualityPos);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        this.inflater = inflater;
        getValues();
        View view = inflater.inflate(R.layout.fragment_video_detail_drama_layout, null);
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
        gridview = (GridView)view.findViewById(R.id.gridview);
        gridview.setOnItemClickListener(onItemClickListener);
        adapter = new GridAdapter();
        gridview.setAdapter(adapter);
    }
    final OnItemClickListener onItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // TODO Auto-generated method stub
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
    private void playVideo(int position) {
		if(realVideoInfo != null && realVideoInfo.getRows().size() > 0){
			List<RealVideo> listVideo = realVideoInfo.getRows().get(currentQualityPos).getVideos();
			if(listVideo.size() == 1){
				BaiduPlayActivity.launch(activity, UrlDecodeUtils.decrypt(Constant.key, listVideo.get(0).getUrl()));
			}else if(listVideo.size() > 1){
//				BaiduPlayActivity.launch(activity, realVideoInfo,currentQualityPos);
				Uri uri = getPlayUri();
				BaiduPlayActivity.launch(activity, uri);
			}else{
				Utils.showTips(activity, "无法播放");
			}
			
		}else{
			playH5(position);
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
    
    class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.hot_search_grid_item_layout, null);
                holder.text = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.text.setText(list.get(position).getsSetNum());
            return convertView;
        }

    }

    class ViewHolder {
        TextView text;
    }
}
