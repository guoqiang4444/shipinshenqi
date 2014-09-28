
package com.lzc.pineapple.search;

import com.lzc.pineapple.BaseFragmentActivity;
import com.lzc.pineapple.R;
import com.lzc.pineapple.VideoDetailActivity;
import com.lzc.pineapple.VideoPlayActivity;
import com.lzc.pineapple.adapter.SourceAdapter;
import com.lzc.pineapple.db.PlayCacheInfo;
import com.lzc.pineapple.entity.SearchResultEntity;
import com.lzc.pineapple.entity.SetNum;
import com.lzc.pineapple.entity.SourceSet;
import com.lzc.pineapple.entity.VideoBasic;
import com.lzc.pineapple.util.ImageLoaderConfig;
import com.lzc.pineapple.util.NetworkRequest;
import com.lzc.pineapple.util.UrlHelper;
import com.lzc.pineapple.util.Utils;
import com.lzc.pineapple.volley.Response;
import com.lzc.pineapple.volley.VolleyError;
import com.lzc.pineapple.volley.toolbox.NetworkImageView;
import com.lzc.pineapple.widget.HorizontalListView;
import com.lzc.pineapple.widget.pulltorefresh.SwipeRefreshLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class VideoSearchedActivity extends BaseFragmentActivity implements OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {
    private View headerView;

    private SwipeRefreshLayout swipeLayout;

    private ListView listview;

    private VideoAdapter videoAdapter;

    private SourceAdapter souceAdapter;

    private TextView searchNum;

    private TextView viewMore;

    private RelativeLayout videoDetailLayout;

    private ImageView videoCover;

    private TextView videoName;

    private TextView staring;

    private TextView director;

    private HorizontalListView sourceList;

    private ImageView back;

    private TextView title;

    private int pageNum = 0;

    private int pageSize = 20;
    
    private int currentSource;

    private boolean isRefresh = false; // 是否刷新中

    private String searchKey;
    
    private int                         currentPageNum = 0;//用这个来判断是不是下拉刷新
    
    private List<SourceSet> setSource = new ArrayList<SourceSet>();
    
    private List<VideoBasic>            rows        = new ArrayList<VideoBasic>();
    private SearchResultEntity searchResultEntity;
    private int totalNum = 0;
    
    private ProgressBar                 progressBar;
    
    
    public static final String HOT_VIDEO_KEY_WORD       = "HOT_VIDEO_KEY_WORD";
    
    public static void launch(Context context,String keyWord){
    	Intent intent = new Intent();
        intent.setClass(context, VideoSearchedActivity.class);
        intent.putExtra(HOT_VIDEO_KEY_WORD, keyWord);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        searchKey = getIntent().getStringExtra(HOT_VIDEO_KEY_WORD);
        setContentView(R.layout.activity_video_searched_layout);
        initViews();
        requestData();
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
    }

    private void initViews() {
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        title = (TextView) findViewById(R.id.title);
        title.setText(searchKey);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        // 加载颜色是循环播放的，只要没有完成刷新就会一直循环，color1>color2>color3>color4
        swipeLayout.setColorScheme(android.R.color.white, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        listview = (ListView) findViewById(R.id.listview);
        listview.addHeaderView(initHeaderView());
        videoAdapter = new VideoAdapter();
        listview.setAdapter(videoAdapter);
        listview.setOnItemClickListener(onListViewItemClickListener);
    }

    private View initHeaderView() {
        View headerView = getLayoutInflater().inflate(R.layout.search_result_list_header_layout,
                null);
        searchNum = (TextView) headerView.findViewById(R.id.search_num);
        viewMore = (TextView) headerView.findViewById(R.id.view_more);
        videoDetailLayout = (RelativeLayout) headerView.findViewById(R.id.video_detail_layout);
        videoCover = (ImageView) headerView.findViewById(R.id.cover);
        videoName = (TextView) headerView.findViewById(R.id.name);
        staring = (TextView) headerView.findViewById(R.id.staring);
        director = (TextView) headerView.findViewById(R.id.director);
        sourceList = (HorizontalListView) headerView.findViewById(R.id.source_list);
        souceAdapter = new SourceAdapter(this,setSource);
        sourceList.setAdapter(souceAdapter);
        sourceList.setOnItemClickListener(onHLItemClickListener);
        return headerView;
    }
    private void requestData(){
    	requestData(searchKey,pageNum);
    }
    private void requestData(String key,final int pageNum) {
        isRefresh = true;
        String url = UrlHelper.URL_SEARCH_LIST;
        StringBuilder sb = new StringBuilder();
        sb.append("&content=").append(URLEncoder.encode(key));
        sb.append("&pagenum=" + pageNum);
        sb.append("&pagesize=" + pageSize);
        url += sb.toString();
        NetworkRequest.get(url, SearchResultEntity.class,
                new Response.Listener<SearchResultEntity>() {

                    @Override
                    public void onResponse(final SearchResultEntity response) {
                    	currentPageNum = pageNum;
                    	isRefresh = false;
                    	progressBar.setVisibility(View.GONE);
                    	setRefreshState(false);
                        if (response == null || response.getiRet() != 0) {
                            return;
                        }
                        totalNum = response.getiTotal();
                        searchResultEntity = response;
                        updateViews();
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    	isRefresh = false;
                    	setRefreshState(false);
                    	progressBar.setVisibility(View.GONE);
                    }

                });
    }
    private void setRefreshState(boolean isRefreshing){
        swipeLayout.setRefreshing(isRefreshing);
    }
    private void loadNextPage(){
    	pageNum ++;
    	requestData();
    }
    private void updateViews(){
    	if(currentPageNum <= 0){
    		rows.clear();
    		String text = String.format(getResources().getString(R.string.search_num), totalNum);
        	searchNum.setText(text);
    	}
    	rows.addAll(searchResultEntity.getvVideoBasic());
    	videoAdapter.notifyDataSetChanged();
    	if(currentPageNum <= 0){
    		updateTopViews();
    	}
    }
    private void updateTopViews(){
    	VideoBasic videoBasic = rows.get(0);
    	displayImage(videoBasic.getsPicUrl(),videoCover);
    	videoName.setText(videoBasic.getsVideoName());
    	if(TextUtils.isEmpty(videoBasic.getsActor())){
    		staring.setVisibility(View.GONE);
    	}else{
    		staring.setText("主演: "+videoBasic.getsActor());
    	}
    	if(TextUtils.isEmpty(videoBasic.getsDirector())){
    		
    		director.setVisibility(View.GONE);
    	}else{
    		director.setText("导演: "+videoBasic.getsDirector());
    	}
    	
    	List<SetNum> setNums = videoBasic.getvSetNum();
    	for(int i = 0;i < setNums.size();i++){
    		SourceSet ss = new SourceSet();
    		if( 0 == i){
    			ss.setSelected(true);
    			currentSource = setNums.get(i).getiSrc();
    		}else{
    			ss.setSelected(false);
    		}
    		ss.setiSrc(setNums.get(i).getiSrc());
    		setSource.add(ss);
    	}
    	souceAdapter.notifyDataSetChanged();
    }
    final OnItemClickListener onHLItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			// TODO Auto-generated method stub
			currentSource = position;
			updateSourceView(position);
			playVideo(position);
		}
	};
	final OnItemClickListener onListViewItemClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				// TODO Auto-generated method stub
				int index = position - listview.getHeaderViewsCount();
				index = index < 0 ? 0 : index;
				VideoBasic videoBasic = rows.get(index);
				VideoDetailActivity.launch(VideoSearchedActivity.this, ""+videoBasic.getlVideoId(), videoBasic.getsVideoName());
			}
		};
	private void updateSourceView(int position){
		for(int i= 0;i < setSource.size();i++){
			if( i == position){
				setSource.get(i).setSelected(true);
			}else{
				setSource.get(i).setSelected(false);
			}
		}
		souceAdapter.updateList(setSource);
	}
	private void playVideo(int position){
		int src = setSource.get(position).getiSrc();
		VideoBasic videoBasic = searchResultEntity.getvVideoBasic().get(0);
		//http://v.html5.qq.com/redirect?vid=2000752&type=2&src=3&num=1&actsrc=3
		String url = "http://v.html5.qq.com/redirect";
		StringBuilder sb = new StringBuilder();
		sb.append("?vid=").append(videoBasic.getlVideoId());
		sb.append("&src=").append(src);
		SetNum setNum = null ;
		for(SetNum sn : videoBasic.getvSetNum()){
			if(src == sn.getiSrc()){
				setNum = sn;
				break;
			}
		}
		if(setNum != null){
			sb.append("&num=").append(setNum.getsCurSetNum());
		}
		url += sb.toString();
    	PlayCacheInfo playCacheInfo = new PlayCacheInfo();
		playCacheInfo.setType(1);
		playCacheInfo.setCover(videoBasic.getsPicUrl());
		playCacheInfo.setVideoName(videoBasic.getsVideoName());
		playCacheInfo.setTime(Utils.getSysNowTime());
		playCacheInfo.setVideoId(""+videoBasic.getlVideoId());
		VideoPlayActivity.launch(VideoSearchedActivity.this, url, playCacheInfo);
	}
    private void displayImage(String url,ImageView imageView){
        ImageLoader.getInstance().displayImage(url, imageView, ImageLoaderConfig.getPosterImageOption(), new ImageLoadingListener() {
            
            @Override
            public void onLoadingStarted(String arg0, View arg1) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onLoadingCancelled(String arg0, View arg1) {
                // TODO Auto-generated method stub
                
            }
        });
    }
    class VideoAdapter extends BaseAdapter {



        @Override
        public int getCount() {
            return rows == null ? 0 : rows.size();
        }

        @Override
        public VideoBasic getItem(int position) {
            return rows == null ? null : rows.get(position);
        }

        @Override
        public long getItemId(int position) {
            return rows == null ? 0 : rows.get(position).getlVideoId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final VideoBasic item = getItem(position);
            if (item == null) {
                return convertView;
            }
            VideoHolder holder;
            if (convertView == null) {
                holder = new VideoHolder();
                convertView = getLayoutInflater().inflate(R.layout.search_video_list_item_layout,
                        null);
                holder.videoCover = (NetworkImageView) convertView.findViewById(R.id.cover);
                holder.videoName = (TextView) convertView.findViewById(R.id.name);
                holder.updateNum = (TextView) convertView.findViewById(R.id.update_num);
                holder.staring = (TextView) convertView.findViewById(R.id.staring);
                holder.director = (TextView) convertView.findViewById(R.id.director);
                convertView.setTag(holder);
            } else {
                holder = (VideoHolder) convertView.getTag();
            }
            holder.videoCover.setDefaultImageResId(R.drawable.bg_cover);
            holder.videoCover.setErrorImageResId(R.drawable.bg_cover);
            holder.videoCover.setImageUrl(item.getsPicUrl(), NetworkRequest.getImageLoader());
            holder.videoName.setText(item.getsVideoName());
            holder.updateNum.setText(item.getvSetNum().get(0).getsCurSetNum());
            holder.staring.setText(item.getiYear()+"");
            holder.director.setText(item.getsDirector());
            
            if(position >= (rows.size() - 4) && rows.size() < totalNum && progressBar.getVisibility() == View.GONE){
            	progressBar.setVisibility(View.VISIBLE);
            	loadNextPage();
            }
            return convertView;
        }

    }

    class VideoHolder {
        NetworkImageView videoCover;

        TextView videoName;

        TextView updateNum;

        TextView staring;

        TextView director;
    }

    @Override
    public void onRefresh() {
    	if(isRefresh){
    		return;
    	}
    	pageNum = 0;
    	requestData();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.back:
                onBackClick();
                break;
        }
    }

    private void onBackClick() {
        onBackPressed();
    }
}
