package com.lzc.pineapple.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lzc.pineapple.R;
import com.lzc.pineapple.VideoPlayActivity;
import com.lzc.pineapple.db.PlayCacheInfo;
import com.lzc.pineapple.entity.CacheVideoInfo;
import com.lzc.pineapple.entity.PlatInfo;
import com.lzc.pineapple.entity.VideoBasic;
import com.lzc.pineapple.entity.VideoInfoEntity;
import com.lzc.pineapple.util.Utils;

public class VideoCacheListFragment extends BaseForCacheFragment {
	private final static String VIDEO_INFO_KEY = "video_info_key";
	private final static String SOURCE_TYPE = "source_type";
	private final static String CURR_QUALITY_POS_KEY = "curr_quality_pos_key";
	private VideoInfoEntity videoInfo;
	private int currentSource;
	private ListView listview;
	private GramaListAdapter adapter;

	private View emptyView;
	
	private int currentQualityPos = 0;

	private List<CacheVideoInfo> list = new ArrayList<CacheVideoInfo>();

	public static final Fragment newInstance(VideoInfoEntity videoInfo,
			int currentSource,int currentQualityPos) {
		Fragment fragment = new VideoCacheListFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(VIDEO_INFO_KEY, videoInfo);
		bundle.putInt(SOURCE_TYPE, currentSource);
		bundle.putInt(CURR_QUALITY_POS_KEY, currentQualityPos);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO aAuto-generated method stub
		getValues();
		View view = inflater.inflate(
				R.layout.fragment_video_detail_list_layout, null);
		initViews(view);
		chooseListToShow();
		return view;
	}

	private void getValues() {
		videoInfo = (VideoInfoEntity) this.getArguments().getSerializable(
				VIDEO_INFO_KEY);
		currentSource = this.getArguments().getInt(SOURCE_TYPE);
        currentQualityPos = this.getArguments().getInt(CURR_QUALITY_POS_KEY);
	}

	private void initViews(View view) {
		listview = (ListView) view.findViewById(R.id.listview);
		listview.setOnItemClickListener(onItemClickListener);
		adapter = new GramaListAdapter();
		listview.setAdapter(adapter);
	}
	@Override
    public List<CacheVideoInfo> getSelectedCacheVideo(){
    	List<CacheVideoInfo> cacheList = new ArrayList<CacheVideoInfo>();
    	for(CacheVideoInfo cvi : list){
    		if(cvi.isSelected()){
    			cacheList.add(cvi);
    		}
    	}
    	return cacheList;
    }
	/**
	 * Show the correct ListView and hide the other, or hide both and show the
	 * empty view.
	 */
	private void chooseListToShow() {
		List<PlatInfo> pList = videoInfo.getmPlatUrl().getPlatInfos(currentSource);
		if (pList == null || pList.size() <= 0) {
			listview.setVisibility(View.GONE);
			emptyView.setVisibility(View.VISIBLE);
		} else {
			VideoBasic vb = videoInfo.getStVideoBasic();
			String cover = vb.getsPicUrl();
			String name = vb.getsVideoName();
			for (PlatInfo pi : pList) {
				CacheVideoInfo cvi = new CacheVideoInfo();
				cvi.setUrl(pi.getsUrl());
				cvi.setCover(cover);
				cvi.setName(name);
				cvi.setCacheStatus(CacheVideoInfo.START);
				cvi.setSelected(false);
				cvi.setsSetNum(pi.getsSetNum());
				cvi.setBrief(pi.getsBrief());
				list.add(cvi);
			}
			adapter.notifyDataSetChanged();
		}

	}

	final OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			// TODO Auto-generated method stub
			if (list.get(position).isSelected()) {
				list.get(position).setSelected(false);
			} else {
				list.get(position).setSelected(true);
			}
			adapter.notifyDataSetChanged();
		}
	};

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
			if (view == null) {
				holder = new ViewHolder();
				view = View.inflate(getActivity(),
						R.layout.search_cache_list_item_layout, null);
				holder.text = (TextView) view.findViewById(R.id.text);
				holder.corner = (ImageView) view.findViewById(R.id.coner_image);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			CacheVideoInfo cvi = list.get(position);
			holder.text.setText(""+cvi.getBrief());
			if (cvi.isSelected()) {
				holder.corner.setVisibility(View.VISIBLE);
			} else {
				holder.corner.setVisibility(View.GONE);
			}
			return view;
		}
	}

	class ViewHolder {
		TextView text;
		ImageView corner;
	}
}
