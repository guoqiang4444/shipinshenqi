package com.lzc.pineapple.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzc.pineapple.FeaturesAreaActivity;
import com.lzc.pineapple.MainActivity;
import com.lzc.pineapple.R;
import com.lzc.pineapple.SortVideoListActivity;
import com.lzc.pineapple.entity.GroupData;
import com.lzc.pineapple.entity.StartPageEntity;
import com.lzc.pineapple.entity.VideoGroup;
import com.lzc.pineapple.volley.VolleyError;
import com.lzc.pineapple.widget.pulltorefresh.SwipeRefreshLayout;

public class SortFragment extends BaseTabFragment implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout   swipeLayout;
    private ListView             listview;
    private Adapter              adapter;

    private List<VideoGroup>         sortList = new ArrayList<VideoGroup>();

    private LayoutInflater       inflater;

    private boolean              isRefresh = false;

    private MainActivity         activity;

    private final static int     SUB_NUM   = 18;

    public static final Fragment newInstance() {
        Fragment fragment = new SortFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        View view = inflater.inflate(R.layout.fragment_sort, null);
        activity = (MainActivity) getActivity();
        initViews(view);
        initSortList();
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        sortList.clear();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void refreshCompleted(StartPageEntity response) {
        // TODO Auto-generated method stub
        super.refreshCompleted(response);
        initSortList();
    }

    @Override
    public void refreshError(VolleyError error) {
        // TODO Auto-generated method stub
        super.refreshError(error);
    }

    private void initSortList() {
        sortList.clear();
        StartPageEntity entity = activity.getStartPageEntity();

        if (entity != null) {
            for(GroupData groupData : entity.getvGroupData()){
                sortList.add(groupData.getStVideoGroup());
            }
        }
        VideoGroup videoGroup = new VideoGroup();
        videoGroup.setsGroupName(getActivity().getString(R.string.feature_area));
        sortList.add(0, videoGroup);
        adapter.notifyDataSetChanged();
    }

    private void initViews(View view) {
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        //加载颜色是循环播放的，只要没有完成刷新就会一直循环，color1>color2>color3>color4  
        swipeLayout.setColorScheme(android.R.color.white, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        listview = (ListView) view.findViewById(R.id.listview);
        adapter = new Adapter(getActivity(), sortList);
        listview.setAdapter(adapter);
    }
    class Adapter extends BaseAdapter {
        private Context              context;
        private List<VideoGroup> list;

        public Adapter(Context context, List<VideoGroup> list) {
            this.context = context;
            this.list = list;
        }

        public void updateList(List<VideoGroup> list) {
            this.list = list;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.sort_list_content_item_layout, null);
                holder.item = (RelativeLayout)convertView.findViewById(R.id.item_layout);
                holder.text = (TextView) convertView.findViewById(R.id.sort_text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final VideoGroup videoGroup = list.get(position);
            holder.text.setText(videoGroup.getsGroupName());
            holder.item.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if(position == 0){//特色专区
                        FeaturesAreaActivity.launch(getActivity());
                    }else{
                    	SortVideoListActivity.launch(getActivity(),videoGroup);
                    }
                }
            });
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

    class ViewHolder {
        RelativeLayout item;
        TextView  text;
    }

    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        if (!isRefresh) {
            isRefresh = true;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    swipeLayout.setRefreshing(false);
                    isRefresh = false;
                }
            }, 3000);
        }

    }
}
