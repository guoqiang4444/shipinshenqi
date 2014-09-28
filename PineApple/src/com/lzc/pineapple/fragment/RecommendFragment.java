package com.lzc.pineapple.fragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.cyberplayer.utils.ac;
import com.lzc.pineapple.FeatureAreaVideoDetailActivity;
import com.lzc.pineapple.FeaturesAreaActivity;
import com.lzc.pineapple.MainActivity;
import com.lzc.pineapple.R;
import com.lzc.pineapple.SortVideoListActivity;
import com.lzc.pineapple.VideoDetailActivity;
import com.lzc.pineapple.VideoPlayActivity;
import com.lzc.pineapple.db.PlayCacheInfo;
import com.lzc.pineapple.entity.Banner;
import com.lzc.pineapple.entity.GroupData;
import com.lzc.pineapple.entity.RecommendVideo;
import com.lzc.pineapple.entity.RecommendVideos;
import com.lzc.pineapple.entity.StartPageEntity;
import com.lzc.pineapple.entity.VideoBasic;
import com.lzc.pineapple.entity.VideoGroup;
import com.lzc.pineapple.util.Constant;
import com.lzc.pineapple.util.ImageLoaderConfig;
import com.lzc.pineapple.util.NetworkRequest;
import com.lzc.pineapple.util.UrlDecodeUtils;
import com.lzc.pineapple.util.UrlHelper;
import com.lzc.pineapple.util.Utils;
import com.lzc.pineapple.volley.Response;
import com.lzc.pineapple.volley.VolleyError;
import com.lzc.pineapple.widget.AutoScrollViewPager;
import com.lzc.pineapple.widget.SubGridView;
import com.lzc.pineapple.widget.pulltorefresh.SwipeRefreshLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 推荐页面
 * @author zengchan.lzc
 *
 */
public class RecommendFragment extends BaseTabFragment implements SwipeRefreshLayout.OnRefreshListener {
    private AutoScrollViewPager posterPager;
    private RelativeLayout      indicatorLayout;
    private LinearLayout        pointerLayout;
    private TextView            videoName;
    private Context context;
    private int index = 0;
    private SwipeRefreshLayout          swipeLayout;
    private ExpandableListView          listview;
    private ExpandAdapter               videoAdapter;
    private static final int            NUM       = 3;
    private static final int            SUB_NUM   = 6;

    private View               emptyView;
    private View               headerView;

    private LayoutInflater     inflater;
    
    private List<Banner> banners = new ArrayList<Banner>();
    
    private List<GroupData> videoList = new ArrayList<GroupData>();

    /**
     * 标记点集合
     */
    private List<ImageView> points = new ArrayList<ImageView>();
    /**
     * 广告个数
     */
    private int count = 0;
    /**
     * 循环间隔
     */
    private static final int DELAY_TIME = 4000;
    
    private MainActivity activity;
    
    private StartPageEntity startPageEntity;
    
    private RecommendVideos featureAreaVideo;
    
    private Handler handler = new Handler();
    
    public static final Fragment newInstance() {
        Fragment fragment = new RecommendFragment();
        Bundle bundle = new Bundle();
        // bundle.putString(Constant.DEVICE_NAME_KEY, deviceName);
        fragment.setArguments(bundle);
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        this.inflater = inflater;
        View view = inflater.inflate(R.layout.fragment_recommend, null);
        activity = (MainActivity) getActivity();
        initViews(view);
        initRefreshState();
        startPageEntity = activity.getStartPageEntity();
        featureAreaVideo = activity.getRecommendVideos();
        updateViews();
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        posterPager.startAutoScroll();
    }

    @Override
    public void onPause() {
        super.onPause();
        posterPager.stopAutoScroll();
    }
    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        clearData();
        super.onDestroyView();
    }
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
    @Override
    public void refreshRecommendCompleted(StartPageEntity response,RecommendVideos featureAreaVideo) {
        // TODO Auto-generated method stub
        super.refreshCompleted(response);
        setRefreshState(false);
        clearData();
        startPageEntity = response;
        this.featureAreaVideo = featureAreaVideo;
        
        updateViews();
    }
    @Override
    public void refreshError(VolleyError error,int type) {
        // TODO Auto-generated method stub
        super.refreshError(error);
        setRefreshState(false);
    }
   
   
    private void transformToGroupData(){
        if(featureAreaVideo == null){
            return;
        }
        GroupData groupData = new GroupData();
        VideoGroup videoGroup = new VideoGroup();
        videoGroup.setiVideoType(-1);
        videoGroup.setsGroupName(getActivity().getResources().getString(R.string.feature_area));
        groupData.setStVideoGroup(videoGroup);
        
        List<VideoBasic> videoBasic = new ArrayList<VideoBasic>();
        List<RecommendVideo> rows = decryptOriginalUrl();
        if(rows != null && rows.size() > 6){//首页只显示6张
        	rows = rows.subList(0, 6);
        }
        for(RecommendVideo rv : rows){
            VideoBasic vb = new VideoBasic();
            vb.setsActor(rv.getActor());
            vb.setsDirector(rv.getDirector());
            vb.setsPicUrl(rv.getImg());
            vb.setBuy(rv.getBuy());
            vb.setfScore(rv.getPrice());
            vb.setlVideoId(rv.getId());
            List<String> srcList = rv.getSrc();
            if(srcList != null && srcList.size() > 0){
                vb.setsHost(srcList.get(0));
            }
            vb.setsVideoName(rv.getTitle());
            videoBasic.add(vb);
        }
        groupData.setvVideoBasic(videoBasic);
        if(videoBasic.size() > 0){
            videoList.add(0,groupData);
            videoAdapter.notifyDataSetChanged();
        }
        expandView();
    }
    private List<RecommendVideo>  decryptOriginalUrl(){
    	List<RecommendVideo> rows = featureAreaVideo.getRows();
    	for(RecommendVideo recommendVideo :rows){
    		List<String> srcs = recommendVideo.getSrc();
    		if (srcs != null && srcs.size() > 0) {
    			String original = recommendVideo.getSrc().get(0);
    			String tempUrl = UrlDecodeUtils.decrypt(Constant.key, original);
				recommendVideo.getSrc().clear();
				recommendVideo.getSrc().add(tempUrl);
    		}
    	}
    	return rows;
    }
    private void updateViews(){
        if(startPageEntity == null){
            return;
        }
        updateBanner(startPageEntity.getvBanners());
        updateListView();
        transformToGroupData();
    }
    private void updateBanner(List<Banner> banners){
        this.banners .addAll(banners);
        videoName.setVisibility(View.VISIBLE);
        initPoints();
        initPoster();
    }
    private void updateListView(){
    	
        List<GroupData> groupDatas = new ArrayList<GroupData>();
        groupDatas.addAll(startPageEntity.getvGroupData());
        Iterator iterator = groupDatas.iterator();
        while(iterator.hasNext()){
            GroupData groupData = (GroupData) iterator.next();
            String name = groupData.getStVideoGroup().getsGroupName();
            int size = groupData.getvVideoBasic().size();
            if(groupData.getvVideoBasic().size() <= 0){
                iterator.remove();
            }
        }
      
        videoList.addAll(groupDatas);
        videoAdapter.notifyDataSetChanged();
        
        expandView();
    }
    private void initRefreshState(){
        if(activity.isRefresh()){
            setRefreshState(true);
        }
    }
    private void setRefreshState(boolean isRefreshing){
        swipeLayout.setRefreshing(isRefreshing);
    }
    private void initViews(View view){
        swipeLayout = (SwipeRefreshLayout)view. findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        //加载颜色是循环播放的，只要没有完成刷新就会一直循环，color1>color2>color3>color4  
        swipeLayout.setColorScheme(android.R.color.white, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        
        listview = (ExpandableListView)view.findViewById(R.id.listview);
        listview.addHeaderView(initHeaderView());
        videoAdapter = new ExpandAdapter(getActivity());
        listview.setAdapter(videoAdapter);
        
       
    }
    private void expandView() {  
        for (int i = 0; i < videoList.size(); i++) {  
            listview.expandGroup(i);  
        }  
    }  
    private View initHeaderView(){
        View headerView = inflater.inflate(R.layout.recommend_list_header_layout, null);
        posterPager = (AutoScrollViewPager)headerView.findViewById(R.id.poster_pager);
        indicatorLayout = (RelativeLayout)headerView.findViewById(R.id.indicator_layout);
        pointerLayout = (LinearLayout)headerView.findViewById(R.id.pointer_layout);
        videoName = (TextView)headerView.findViewById(R.id.indicator_text);
        
        return headerView;
    }
    private void initPoints() {
        count = banners.size();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(8, 15, 8, 15);
        
        for (int i = 0; i < count; i++) {
            // 添加标记点
            ImageView point = new ImageView(getActivity());

            if (i == index % count){
                point.setBackgroundResource(R.drawable.feature_point_cur);
            } else {
                point.setBackgroundResource(R.drawable.feature_point);
            }
            point.setLayoutParams(lp);

            points.add(point);
            pointerLayout.addView(point);
        }
    }
    private void initPoster() {
        posterPager.setAdapter(new PosterPagerAdapter());
        posterPager.setCurrentItem(count * 500);
        posterPager.setInterval(DELAY_TIME);
        posterPager.setOnPageChangeListener(new PosterPageChange());
        posterPager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
        posterPager.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    posterPager.stopAutoScroll();

                    break;
                case MotionEvent.ACTION_MOVE:
                    posterPager.startAutoScroll();

                    break;
                case MotionEvent.ACTION_UP:
                    posterPager.startAutoScroll();

                    break;

                default:
                    break;
                }

                return false;
            }

        });
    
    }
    class ExpandAdapter extends BaseExpandableListAdapter {
        private Context context;
        private LayoutInflater inflater;

        public ExpandAdapter(Context context) {
            
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getGroupCount() {
            // TODO Auto-generated method stub
            return videoList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            // TODO Auto-generated method stub
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            // TODO Auto-generated method stub
            return videoList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return videoList.get(groupPosition).getvVideoBasic().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            // TODO Auto-generated method stub
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return groupPosition << 32 + childPosition;
        }

        @Override
        public boolean hasStableIds() {
            // TODO Auto-generated method stub
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View groupView = null;
            if (convertView == null) {
                groupView = newGroupView(parent);
            } else {
                groupView = convertView;
            }
            bindGroupView(groupPosition, groupView);
            return groupView;
        }

        private View newGroupView(ViewGroup parent) {
            return inflater.inflate(R.layout.expandlist_group_item_layout, null);
        }

        private void bindGroupView(final int groupPosition, View groupView) {
            
            TextView tv = (TextView) groupView.findViewById(R.id.hl_ItemHeader_Text);
            tv.setText(videoList.get(groupPosition).getStVideoGroup().getsGroupName());
            Button button = (Button) groupView
                    .findViewById(R.id.ib_ItemHeader_cate);
            button.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    onViewMoreClick(groupPosition);
                }

            });
            
        }
        private void onViewMoreClick(int groupPosition){
            if(featureAreaVideo != null && featureAreaVideo.getRows().size() > 0 && groupPosition == 0){//特色专区单独处理
                FeaturesAreaActivity.launch(getActivity(),featureAreaVideo);
            }else{
            	SortVideoListActivity.launch(getActivity(),videoList.get(groupPosition).getStVideoGroup());
            }
           
        }
        @Override
        public View getChildView(int groupPosition, int childPosition,boolean isLastChild,
                                 View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View childView = null;
            if (convertView == null) {
                childView = newChildView(parent, groupPosition);
            } else {
                childView = convertView;
            }
            bindChildView(groupPosition, childPosition, childView);
            return childView;
        }

        private View newChildView(ViewGroup parent, final int groupPosition) {
            View v = inflater.inflate(R.layout.expandlistview_child_item_layout, null);
           
            return v;
        }

        private void bindChildView(final int groupPosition, int childPosition,
                View groupView) {
        	
        	 SubGridView gridView = (SubGridView) groupView.findViewById(R.id.gridview);
             List<VideoBasic> vbList = videoList.get(groupPosition).getvVideoBasic();
             final GridAdapter adapter = new GridAdapter(RecommendFragment.this.getActivity(),vbList,groupPosition);
             gridView.setAdapter(adapter);// 设置菜单Adapter
             gridView.setOnItemClickListener(new OnItemClickListener() {

                 @Override
                 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                     // TODO Auto-generated method stub
                	 if(featureAreaVideo != null && featureAreaVideo.getRows().size() > 0 && groupPosition == 0){//特色专区单独处理
                        FeatureAreaVideoDetailActivity.launch(getActivity(),featureAreaVideo.getRows().get(position));
                     }else{
                     	VideoBasic videoBasic = videoList.get(groupPosition).getvVideoBasic().get(position);
                     	VideoDetailActivity.launch(getActivity(),String.valueOf(videoBasic.getlVideoId()),videoBasic.getsVideoName());
                     }
                 }
             });
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return true;
        }

    }
   
    
    class GridAdapter extends BaseAdapter {
        private Context      context;
        private List<VideoBasic> list;
        private int groupPos;

        public GridAdapter(Context context, List<VideoBasic> list,int groupPos) {
            this.context = context;
            this.list = list;
            this.groupPos = groupPos;
        }

        public void updateList(List<VideoBasic> list) {
            this.list = list;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            GridViewHolder holder = null;
            if(convertView == null){
                holder = new GridViewHolder();
                convertView = inflater.inflate(R.layout.recommend_grid_content_item_layout, null);
                holder.cover  = (ImageView)convertView.findViewById(R.id.cover);
                holder.title  = (TextView)convertView.findViewById(R.id.title_text);
                holder.content = (TextView)convertView.findViewById(R.id.content_text);
                holder.text   = (TextView)convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            }else{
                holder = (GridViewHolder) convertView.getTag();
            }
            VideoBasic vb = list.get(position);
            displayImage(vb.getsPicUrl(), holder.cover);
            
            if(groupPos == 0 || vb.getfScore() <= 0){//首页的特色专区不显示需要的积分
            	holder.text.setVisibility(View.GONE);
            }else{
            	holder.text.setVisibility(View.VISIBLE);
            	holder.text.setText(vb.getfScore()+"");
            }
            
            holder.title.setText(vb.getsVideoName());
            holder.content.setText(vb.getsOneDes());
            holder.content.setVisibility(View.VISIBLE);
            return convertView;
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

    }
    private void displayImage(String url,final ImageView imageView){
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
            	imageView.setScaleType(ScaleType.FIT_XY);
            }
            
            @Override
            public void onLoadingCancelled(String arg0, View arg1) {
                // TODO Auto-generated method stub
                
            }
        });
    }
    class GridViewHolder {
        ImageView cover;
        TextView  text;
        TextView  title;
        TextView  content;
    }
    class PosterPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            ImageView imageView = new ImageView(getActivity());
            imageView.setAdjustViewBounds(true);
            // TODO 调整图片大小
            imageView.setScaleType(ScaleType.FIT_XY);

            android.view.ViewGroup.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
            imageView.setLayoutParams(params);
            displayImage(banners.get(position % count).getsPicUrl(),imageView);

            ((ViewPager) container).addView(imageView);

            imageView.setOnClickListener(new PosterClickListener(position % count));

            return imageView;
        }
       
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            ((ViewPager) container).removeView((ImageView) object);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    }

    class PosterPageChange implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int position) {
            index = position;
            for (int i = 0; i < count; i++) {
                points.get(i).setBackgroundResource(R.drawable.feature_point);
            }

            points.get(position % count).setBackgroundResource(R.drawable.feature_point_cur);
            videoName.setText(banners.get(position % count).getsTitle());
        }

    }
    
    class PosterClickListener implements OnClickListener {

        private int position;
        
        private final String detail = "#p=detail";

        public PosterClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {

            posterPager.stopAutoScroll();
//            #p=detail&vId=2551438&vType=2
//            topic?id=508&ch=002303 //播放页
            Banner banner = banners.get(position);
            String url = banner.getsUrl();
            if(url.startsWith(detail)){//跳转至详情页
            	String[] sa = url.split("&");
            	String vid = sa[1].substring(4, sa[1].length());
            	VideoDetailActivity.launch(getActivity(), vid,banner.getsTitle());
            }else{
            	String playUrl = "http://v.html5.qq.com/"+url;
            	PlayCacheInfo playCacheInfo = new PlayCacheInfo();
        		playCacheInfo.setType(2);
        		playCacheInfo.setCover(banner.getsPicUrl());
        		playCacheInfo.setVideoName(banner.getsTitle());
        		playCacheInfo.setTime(Utils.getSysNowTime());
        		playCacheInfo.setVideoId(""+System.currentTimeMillis());
        		playCacheInfo.setUrl(playUrl);
            	VideoPlayActivity.launch(getActivity(), playUrl,playCacheInfo);
            }
//            

        }

    }

    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        if (!activity.isRefresh()) {
            activity.refresh();
        }
    }
    private void clearData(){
        banners.clear();
        videoList.clear();
        points.clear();
        pointerLayout.removeAllViews();
    }
}

