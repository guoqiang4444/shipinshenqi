package com.lzc.pineapple;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzc.pineapple.entity.RecommendVideo;
import com.lzc.pineapple.entity.RecommendVideos;
import com.lzc.pineapple.util.Constant;
import com.lzc.pineapple.util.NetworkRequest;
import com.lzc.pineapple.util.UrlDecodeUtils;
import com.lzc.pineapple.util.UrlHelper;
import com.lzc.pineapple.util.Utils;
import com.lzc.pineapple.volley.Response;
import com.lzc.pineapple.volley.VolleyError;
import com.lzc.pineapple.widget.pulltorefresh.SwipeRefreshLayout;
import com.umeng.analytics.MobclickAgent;

/**
 * 特色专区展示Activity
 * @author zengchan.lzc
 *
 */
public class FeaturesAreaActivity extends BaseFragmentActivity implements OnClickListener,SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout          swipeLayout;
    private GridView                    gridview;
    private ImageView                   back;
    private TextView                    title;
    
    private boolean                     isRefresh = false;                  //是否刷新中 
    
    private VideoAdapter                videoAdapter;
    
    private final static String FEATURE_AREA_KEY = "featrue_area_key";
    
    private RecommendVideos featureAeraVideo;
    
    private List<RecommendVideo> rows = new ArrayList<RecommendVideo>();
    
    public static void launch(Context c) {
        Intent intent = new Intent(c, FeaturesAreaActivity.class);
        c.startActivity(intent);
    }
    public static void launch(Context c,RecommendVideos recommednVideo){
        Intent intent = new Intent(c, FeaturesAreaActivity.class);
        intent.putExtra(FEATURE_AREA_KEY, recommednVideo);
        c.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature_area_layout);
       
        initViews();
        
        initValues();
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
    final OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			RecommendVideo recommendVideo = rows.get(position);
			List<String> srcs = recommendVideo.getSrc();
    		if (srcs != null && srcs.size() > 0) {
    			String original = recommendVideo.getSrc().get(0);
    			String tempUrl = UrlDecodeUtils.decrypt(Constant.key, original);
				recommendVideo.getSrc().clear();
				recommendVideo.getSrc().add(tempUrl);
    		}
			FeatureAreaVideoDetailActivity.launch(FeaturesAreaActivity.this, recommendVideo);
		}
	};
    private void initValues(){
        featureAeraVideo = (RecommendVideos) getIntent().getSerializableExtra(FEATURE_AREA_KEY);
        if(null == featureAeraVideo){
            requestFeatureData();
        }else{
            updateView();
        }
    }
    private void updateView(){
        if(featureAeraVideo.getRows() != null){
            rows = featureAeraVideo.getRows();
            videoAdapter.notifyDataSetChanged();
        }
    }
    private void setRefreshState(boolean isRefreshing){
        swipeLayout.setRefreshing(isRefreshing);
    }
    private void requestFeatureData(){
    	isRefresh = true;
    	StringBuilder sb = new StringBuilder();
    	sb.append("&user_id="+Utils.getDeviceId(this));
    	sb.append("&device_id="+Utils.getDeviceId(this));
    	sb.append("&user_type=4&user_site=0");
    	String url = UrlHelper.URL_RECOMMEND_LIST + sb.toString();
        NetworkRequest.get(url, RecommendVideos.class, new Response.Listener<RecommendVideos>() {

            @Override
            public void onResponse(final RecommendVideos response) {
                // TODO Auto-generated method stub
            	isRefresh = false;
            	setRefreshState(false);
                featureAeraVideo = response;
                updateView();
            }
            
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
            	isRefresh = false;
            	setRefreshState(false);
            }
            
        });
    }
    private void initViews(){
        back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);
        title = (TextView)findViewById(R.id.title);
        title.setText(R.string.feature_area);
        swipeLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        //加载颜色是循环播放的，只要没有完成刷新就会一直循环，color1>color2>color3>color4  
        swipeLayout.setColorScheme(android.R.color.white, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        gridview = (GridView)findViewById(R.id.gridview);
        videoAdapter = new VideoAdapter();
        gridview.setAdapter(videoAdapter);
        gridview.setOnItemClickListener(onItemClickListener);
    }
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
            ViewHolder holder ;
            if(convertView == null){
                holder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.sort_grid_content_item_layout, null);
                holder.cover = (ImageView)convertView.findViewById(R.id.cover);
                holder.title = (TextView)convertView.findViewById(R.id.title_text);
                holder.price = (TextView)convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            RecommendVideo recommendVideo = rows.get(position);
            Utils.displayImage(recommendVideo.getImg(), holder.cover);
            holder.title.setText(recommendVideo.getTitle());
            if(recommendVideo.getBuy() == 1){//用户购买过
            	holder.price.setVisibility(View.GONE);
            }else{
            	holder.price.setVisibility(View.VISIBLE);
            	holder.price.setText(recommendVideo.getPrice()+"积分");
            }
            return convertView;
        }
        
    }
    class ViewHolder{
        ImageView cover;
        TextView  title;
        TextView  price;
    }
    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        if(isRefresh){
        	return;
        }
        requestFeatureData();
    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch(v.getId()){
            case R.id.back:
                onBackClick();
                break;
        }
    }
    private void onBackClick(){
        onBackPressed();
    }
}
