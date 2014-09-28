package com.lzc.pineapple;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lzc.pineapple.entity.ClassifyInfo.Info;
import com.lzc.pineapple.entity.FilterInfo;
import com.lzc.pineapple.entity.GroupListEntity;
import com.lzc.pineapple.entity.VideoBasic;
import com.lzc.pineapple.entity.VideoGroup;
import com.lzc.pineapple.util.ImageLoaderConfig;
import com.lzc.pineapple.util.NetworkRequest;
import com.lzc.pineapple.util.UrlHelper;
import com.lzc.pineapple.util.Utils;
import com.lzc.pineapple.volley.Response;
import com.lzc.pineapple.volley.VolleyError;
import com.lzc.pineapple.widget.HorizontalListView;
import com.lzc.pineapple.widget.pulltorefresh.SwipeRefreshLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;

/**
 * 分类列表的视频展示Activity
 * 
 * @author zengchan.lzc
 */
public class SortVideoListActivity extends BaseFragmentActivity implements OnClickListener ,SwipeRefreshLayout.OnRefreshListener{
    
    private SwipeRefreshLayout          swipeLayout;
    private GridView                    gridview;
    private ImageView                   back;
    private TextView                    title;
    
    private TextView                    hotText;
    private TextView                    newText;
    
    private ProgressBar                 progressBar;
    
    private boolean                     isRefresh = false;                  //是否刷新中 
    
    private VideoAdapter                videoAdapter;
    private TextView                    filter;
    
    private List<VideoBasic>            rows        = new ArrayList<VideoBasic>();
    
    private List<FilterInfo>                contentList = new ArrayList<FilterInfo>();
    private List<FilterInfo>                countryList = new ArrayList<FilterInfo>();
    private List<FilterInfo>                yearList    = new ArrayList<FilterInfo>();
    
    private FilterPopupWindow           popupWindow;
    
    private static final String         VIDEO_GROUP_KEY = "video_group_key";
    private static final int            HOT_TYPE = 2;
    private static final int            NEW_TYPE = 3;
    private VideoGroup                  videoGroup;
    private GroupListEntity             groupListEntity;
    
    private int                         currentPageNum = 0;//用这个来判断是不是下拉刷新
    
    private int groupId = 0;
	private int pageNum = 0;
	private int pageSize = 20;
	private int seqId = 0;
	private String siftcon = "0_0_0";
	private int sort = 2;
	private String[] siftArray = new String[3];
	
	private int totalNum = 0;
	
	private int screenWidth;
    
    public static void launch(Context c) {
        Intent intent = new Intent(c, SortVideoListActivity.class);
        c.startActivity(intent);
    }
    
    public static void launch(Context c,VideoGroup videoGroup) {
        Intent intent = new Intent(c, SortVideoListActivity.class);
        intent.putExtra(VIDEO_GROUP_KEY, videoGroup);
        c.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_video_list_layout);
        initValues();
        initViews();
        setListener();
        setSortSelected();
        setRefreshState(true);
        requesData();
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
    private void initValues(){
    	videoGroup = (VideoGroup) getIntent().getSerializableExtra(VIDEO_GROUP_KEY);
    	groupId = videoGroup.getiGroupId();
    	screenWidth  = getWindowManager().getDefaultDisplay().getWidth(); 
    	
    	for(int i = 0;i < siftArray.length;i++){
    		siftArray[i] = "0";
    	}
    	spilceSift();
    }
    private void initViews(){
        back = (ImageView)findViewById(R.id.back);
        title = (TextView)findViewById(R.id.title);
        title.setText(videoGroup.getsGroupName());
        filter = (TextView)findViewById(R.id.right_text);
        filter.setText(R.string.filter_video);
        filter.setVisibility(View.VISIBLE);
        hotText = (TextView)findViewById(R.id.hot_text);
        newText = (TextView)findViewById(R.id.new_text);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        swipeLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
        //加载颜色是循环播放的，只要没有完成刷新就会一直循环，color1>color2>color3>color4  
        swipeLayout.setColorScheme(android.R.color.white, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        gridview = (GridView)findViewById(R.id.gridview);
        videoAdapter = new VideoAdapter();
        gridview.setAdapter(videoAdapter);
    }
    private void setListener(){
    	 back.setOnClickListener(this);
    	 filter.setOnClickListener(this);
    	 hotText.setOnClickListener(this);
    	 newText.setOnClickListener(this);
    	 swipeLayout.setOnRefreshListener(this);
     	 gridview.setOnItemClickListener(onItemClickListener);
    }
    private void setSortSelected(){
    	hotText.setSelected(true);
    	newText.setSelected(false);
    }
    private void setNewSelected(){
    	hotText.setSelected(false);
    	newText.setSelected(true);
    }
    private void setRefreshState(boolean isRefreshing){
        swipeLayout.setRefreshing(isRefreshing);
    }
    private void requesData(){
    	requestData(groupId, pageNum, pageSize, seqId, siftcon, sort);
    }
    private void requestData(int groupId,final int pageNum,int pageSize,int seqId,String siftcon,int sort){
    	isRefresh = true;
        String url = UrlHelper.URL_GROUP_LIST_BYID;
        StringBuilder sb = new StringBuilder();
        sb.append("&groupid=").append(groupId);
        sb.append("&pagenum=").append(pageNum);
        sb.append("&pagesize=").append(pageSize);
        sb.append("&seqid=").append(seqId);
        sb.append("&siftcon=").append(siftcon);
        sb.append("&sort=").append(sort);
        url += sb.toString();
        Log.d("lzc","sort url=====>"+url);
        NetworkRequest.get(url, GroupListEntity.class, new Response.Listener<GroupListEntity>() {

            @Override
            public void onResponse(final GroupListEntity response) {
                // TODO Auto-generated method stub
            	Log.d("lzc","onRespones=====>");
            	isRefresh = false;
            	setRefreshState(false);
            	progressBar.setVisibility(View.GONE);
            	currentPageNum = pageNum;
            	totalNum = response.getiTotal();
            	groupListEntity = response;
            	
            	updateView();
            }
            
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
            	Log.d("lzc","error=====>"+error);
            	isRefresh = false;
            	setRefreshState(false);
            	progressBar.setVisibility(View.GONE);
            }
            
        });
    }
    private void loadNextPage(){
    	pageNum ++;
    	requesData();
    }
    private void updateView(){
    	if(currentPageNum <= 0){
    		rows.clear();
    	}
    	rows.addAll(groupListEntity.getvVideoBasic());
    	Log.d("lzc","size===>"+rows.size());
    	videoAdapter.notifyDataSetChanged();
    }
   
    final OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			// TODO Auto-generated method stub
		    VideoBasic videoBasic = rows.get(position);
			VideoDetailActivity.launch(SortVideoListActivity.this,String.valueOf(videoBasic.getlVideoId()), videoBasic.getsVideoName());
		}
	};
    class VideoAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return rows.size();
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
            GridViewHolder holder = null;
            if(convertView == null){
                holder = new GridViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.recommend_grid_content_item_layout, null);
                holder.cover  = (ImageView)convertView.findViewById(R.id.cover);
                holder.title  = (TextView)convertView.findViewById(R.id.title_text);
                holder.text   = (TextView)convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            }else{
                holder = (GridViewHolder) convertView.getTag();
            }
            Utils.displayImage(rows.get(position).getsPicUrl(), holder.cover);
            float score = rows.get(position).getfScore();
            if(score == 0){
            	holder.text.setVisibility(View.GONE);
            }else{
            	holder.text.setVisibility(View.VISIBLE);
            	holder.text.setText(""+score);
            }
            holder.title.setText(rows.get(position).getsVideoName());
            if(position >= (rows.size() - 4) && rows.size() < totalNum && progressBar.getVisibility() == View.GONE){
            	progressBar.setVisibility(View.VISIBLE);
            	loadNextPage();
            }
            return convertView;
        }
        
    }
    class GridViewHolder {
        ImageView cover;
        TextView  text;
        TextView  title;
        TextView  content;
    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch(v.getId()){
            case R.id.back:
                onBackClick();
                break;
            case R.id.right_text:
                onFilterClick();
                break;
            case R.id.hot_text:
            	onHotClick();
            	break;
            case R.id.new_text:
            	onNewClick();
            	break;
        }
    }
    private void onBackClick(){
        onBackPressed();
    }
    private void onFilterClick(){
    	showPopupWindow();
    }
    private void onHotClick(){
    	if(HOT_TYPE == sort){
    		return;
    	}
    	sort = HOT_TYPE;
    	pageNum = 0;
    	setSortSelected();
    	setRefreshState(true);
    	requesData();
    }
    private void onNewClick(){
    	if(NEW_TYPE == sort){
    		return;
    	}
    	sort = NEW_TYPE;
    	pageNum = 0;
    	setNewSelected();
    	setRefreshState(true);
    	requesData();
    }
    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
    	if(isRefresh){
    		return;
    	}
    	pageNum = 0;
    	requesData();
    }
    
    private void showPopupWindow(){
    	if(groupListEntity.getvClassifyInfo() == null 
    			|| groupListEntity.getvClassifyInfo().size() <3
    			|| groupListEntity.getvClassifyInfo().get(0).getvInfo() == null
    			|| groupListEntity.getvClassifyInfo().get(0).getvInfo().size() <= 0){
    		return;
    	}
    	if(popupWindow == null){
    		popupWindow = new FilterPopupWindow();
    	}
        popupWindow.showAsDropDown(filter, 0, 0);
    }
    private void dismissPopupWindow(){
        if(popupWindow != null && popupWindow.isShowing()){
            popupWindow.dismiss();
        }
    }
    private final static int KOREN_VIDEO = 34;
    private final static int USA_VIDEO = 36;
    private void spilceSift(){
    	StringBuilder sb = new StringBuilder();
    	for(int i = 0; i < 2 ; i++){
    		sb.append(siftArray[i]).append("_");
    	}
    	sb.append(siftArray[2]);
    	siftcon = sb.toString();
    	if(groupId == KOREN_VIDEO){//韩剧
    		siftcon = "0_204_0";
    	}else if(groupId == USA_VIDEO){
    		siftcon = "0_201_0";
    	}
    }
    class FilterPopupWindow extends PopupWindow{
        private View               rootView;
        private HorizontalListView contentListView;
        private HorizontalListView countryListView;
        private HorizontalListView yearListView;
        
        private FilterAdapter      contentAdapter;
        private FilterAdapter      countryAdapter;
        private FilterAdapter      yearAdapter;

        public FilterPopupWindow() {
            super(SortVideoListActivity.this);
           
            rootView  = (View) getLayoutInflater().inflate(R.layout.filter_popup_layout, null);
            initListView(rootView);
            setOutsideTouchable(true);
            setFocusable(true);
            setContentView(rootView);
           
            setWidth(screenWidth);
            setHeight(Utils.dip2px(SortVideoListActivity.this, 150));
        }
        private void initListView(View rootView){
            contentListView = (HorizontalListView)rootView.findViewById(R.id.content_list);
            countryListView = (HorizontalListView)rootView.findViewById(R.id.country_list);
            yearListView    = (HorizontalListView)rootView.findViewById(R.id.year_list);
            
            if(groupId == USA_VIDEO || groupId == KOREN_VIDEO){
            	countryListView.setVisibility(View.GONE);
            }
            
            initContentList();
            initCountryList();
            initYearList();
            
            contentAdapter = new FilterAdapter(contentList);
            countryAdapter = new FilterAdapter(countryList);
            yearAdapter    = new FilterAdapter(yearList);
            
            contentListView.setAdapter(contentAdapter);
            countryListView.setAdapter(countryAdapter);
            yearListView.setAdapter(yearAdapter);
            
            contentListView.setOnItemClickListener(contentItemClickListener);
            countryListView.setOnItemClickListener(countryItemClickListener);
            yearListView.setOnItemClickListener(yearItemClickListener);
        }
        private void initContentList(){
        	List<Info> infos = groupListEntity.getvClassifyInfo().get(0).getvInfo();
        	for(int i = 0; i < infos.size() ;i++){
        		FilterInfo fi = new FilterInfo();
        		if(i == 0){
        			fi.setSelected(true);
        		}else {
					fi.setSelected(false);
				}
        		fi.setiClassifyId(infos.get(i).getiClassifyId());
        		fi.setsClassifyName(infos.get(i).getsClassifyName());
        	    contentList.add(fi);	
        	}
        }
        private void initCountryList(){
        	List<Info> infos = groupListEntity.getvClassifyInfo().get(1).getvInfo();
        	for(int i = 0; i < infos.size() ;i++){
        		FilterInfo fi = new FilterInfo();
        		if(i == 0){
        			fi.setSelected(true);
        		}else {
					fi.setSelected(false);
				}
        		fi.setiClassifyId(infos.get(i).getiClassifyId());
        		fi.setsClassifyName(infos.get(i).getsClassifyName());
        	    countryList.add(fi);	
        	}
        }
        private void initYearList(){
        	List<Info> infos = groupListEntity.getvClassifyInfo().get(2).getvInfo();
        	for(int i = 0; i < infos.size() ;i++){
        		FilterInfo fi = new FilterInfo();
        		if(i == 0){
        			fi.setSelected(true);
        		}else {
					fi.setSelected(false);
				}
        		fi.setiClassifyId(infos.get(i).getiClassifyId());
        		fi.setsClassifyName(infos.get(i).getsClassifyName());
        	    yearList.add(fi);	
        	}
        }
        private void updateContentList(int position){
        	for(int i = 0;i< contentList.size();i++){
        		if(i == position){
        			contentList.get(i).setSelected(true);
        		}else{
        			contentList.get(i).setSelected(false);
        		}
        	}
        	contentAdapter.notifyDataSetChanged();
        }
        private void updateCountryList(int position){
        	for(int i = 0;i < countryList.size();i++){
        		if(position == i){
        			countryList.get(i).setSelected(true);
        		}else{
        			countryList.get(i).setSelected(false);
        		}
        	}
        	countryAdapter.notifyDataSetChanged();
        }
        private void updateYearList(int position){
        	for(int i = 0;i < yearList.size();i++){
        		if(position == i){
        			yearList.get(i).setSelected(true);
        		}else{
        			yearList.get(i).setSelected(false);
        		}
        	}
        	yearAdapter.notifyDataSetChanged();
        }
       
        final OnItemClickListener contentItemClickListener = new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                updateContentList(position);
                siftArray[0] = String.valueOf(contentList.get(position).getiClassifyId());
                spilceSift();
                pageNum = 0;
                setRefreshState(true);
                requesData();
            }
        };
        final OnItemClickListener countryItemClickListener = new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                updateCountryList(position);
                siftArray[1] = String.valueOf(countryList.get(position).getiClassifyId());
                spilceSift();
                pageNum = 0;
                setRefreshState(true);
                requesData();
            }
        };
        final OnItemClickListener yearItemClickListener = new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                updateYearList(position);
                siftArray[2] = String.valueOf(yearList.get(position).getiClassifyId());
                spilceSift();
                pageNum = 0;
                setRefreshState(true);
                requesData();
            }
        };
        

    }
    class FilterAdapter extends BaseAdapter{
        private List<FilterInfo> list ;
        
        public FilterAdapter(List<FilterInfo> list){
            this.list = list;
        }

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
            FilterViewHolder holder ;
            if(convertView == null){
                holder = new FilterViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.only_text_layout, null);
                holder.text = (TextView)convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            }else{
                holder = (FilterViewHolder) convertView.getTag();
            }
            holder.text.setText(list.get(position).getsClassifyName());
            holder.text.setSelected(list.get(position).isSelected());
            return convertView;
        }
        
    }
    class FilterViewHolder{
        TextView text;
    }
}
