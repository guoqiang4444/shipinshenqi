package com.lzc.pineapple.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzc.pineapple.R;
import com.lzc.pineapple.entity.CacheVideoInfo;
import com.lzc.pineapple.entity.PlatInfo;
import com.lzc.pineapple.entity.VideoBasic;
import com.lzc.pineapple.entity.VideoInfoEntity;

public class VideoCacheDramaFragment extends BaseForCacheFragment{
    private GridView       gridview;
    private GridAdapter    adapter;
    private LayoutInflater inflater;
    
    private final static String VIDEO_INFO_KEY = "video_info_key";
    private final static String SOURCE_TYPE = "source_type";
    private static final String CURRENT_QUALITY_POS_KEY = "curr_quality_pos_key";
	private VideoInfoEntity videoInfo;
	
	private int currentSource;
	private int currentQualityPos;
	
	private View     emptyView;
	
	private List<CacheVideoInfo> list = new ArrayList<CacheVideoInfo>();

    public static final Fragment newInstance(VideoInfoEntity videoInfo,int currentSource,int currentQualityPos) {
        Fragment fragment = new VideoCacheDramaFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(VIDEO_INFO_KEY, videoInfo);
        bundle.putInt(SOURCE_TYPE, currentSource);
        bundle.putInt(CURRENT_QUALITY_POS_KEY, currentQualityPos);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        this.inflater = inflater;
        getValues();
        View view = inflater.inflate(R.layout.fragment_video_detail_drama_layout, null);
        initViews(view);
        chooseListToShow();
        return view;
    }
    private void getValues(){
    	videoInfo = (VideoInfoEntity) this.getArguments().getSerializable(VIDEO_INFO_KEY);
    	currentSource = this.getArguments().getInt(SOURCE_TYPE);
    	currentQualityPos = this.getArguments().getInt(CURRENT_QUALITY_POS_KEY);
    }
    private void initViews(View view){
        gridview = (GridView)view.findViewById(R.id.gridview);
        gridview.setOnItemClickListener(onItemClickListener);
        adapter = new GridAdapter();
        gridview.setAdapter(adapter);
       
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
		List<PlatInfo> pList= videoInfo.getmPlatUrl().getPlatInfos(currentSource);
    	if(pList == null || pList.size() <= 0){
    		gridview.setVisibility(View.GONE);
    		emptyView.setVisibility(View.VISIBLE);
    	}else{
    		VideoBasic vb = videoInfo.getStVideoBasic();
    		String cover = vb.getsPicUrl();
    		String name = vb.getsVideoName();
    		for(PlatInfo pi : pList){
    			CacheVideoInfo cvi = new CacheVideoInfo();
    			cvi.setUrl(pi.getsUrl());
    			cvi.setCover(cover);
    			cvi.setName(name);
    			cvi.setCacheStatus(CacheVideoInfo.START);
    			cvi.setSelected(false);
    			cvi.setsSetNum(pi.getsSetNum());
    			Log.d("lzc","origin url==?"+cvi.getUrl());
    			list.add(cvi);
    		}
    		adapter.notifyDataSetChanged();
    	}
		
	}
    final OnItemClickListener onItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // TODO Auto-generated method stub
        	if(list.get(position).isSelected()){
        		list.get(position).setSelected(false);
        	}else{
        		list.get(position).setSelected(true);
        	}
        	adapter.notifyDataSetChanged();
        	
        }
    };
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
                holder.corner = (ImageView) convertView.findViewById(R.id.coner_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            
            CacheVideoInfo cvi = list.get(position);
            holder.text.setText(cvi.getsSetNum());
            if(cvi.isSelected()){
            	holder.corner.setVisibility(View.VISIBLE);
            }else{
            	holder.corner.setVisibility(View.GONE);
            }
            return convertView;
        }

    }

    class ViewHolder {
        TextView text;
        ImageView corner;
    }

}
