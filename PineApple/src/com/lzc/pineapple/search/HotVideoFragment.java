package com.lzc.pineapple.search;

import java.util.ArrayList;
import java.util.Arrays;
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
import android.widget.TextView;

import com.lzc.pineapple.R;
import com.lzc.pineapple.util.Constant;

public class HotVideoFragment extends Fragment implements OnClickListener{
    private GridView gridview;
    private TextView changeHotWord;
    private GridAdapter adapter;
    private List<String> list = new ArrayList<String>();
    private LayoutInflater inflater;
    
    private SearChListener searchListener;
    
    private static final String HOT_WORD_KEY = "hot_word_key";
    
    public static final Fragment newInstance(List<String> hotWords) {
        Fragment fragment = new HotVideoFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(HOT_WORD_KEY, (ArrayList<String>) hotWords);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_search_hot_video_layout, null);
        this.inflater = inflater;
        initValue();
        initViews(view);
        return view;
    }
    private void initValue(){
        list = getArguments().getStringArrayList(HOT_WORD_KEY);
        if(list == null){
            list = new ArrayList<String>();
        }
    }
    public void updateHotWords(List<String> hotWords){
        list = hotWords;
        adapter.notifyDataSetChanged();
    }
    private void initViews(View view){
        gridview = (GridView)view.findViewById(R.id.gridview);
        gridview.setOnItemClickListener(onItemClickListener);
        adapter = new GridAdapter();
        gridview.setAdapter(adapter);
        changeHotWord = (TextView)view.findViewById(R.id.change_hot_word);
        changeHotWord.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.change_hot_word:
                onChangeHotWordClick();
                break;

            default:
                break;
        }
    }
    private void onChangeHotWordClick(){
        
    }
    public void setSearchListener(SearChListener searchListener){
        this.searchListener = searchListener;
    }
    final OnItemClickListener onItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // TODO Auto-generated method stub
            searchListener.search(list.get(position));
        }
    };
    class GridAdapter extends BaseAdapter{

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
            if(convertView == null){
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.hot_search_grid_item_layout, null);
                holder.text = (TextView)convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            holder.text.setText(list.get(position));
            return convertView;
        }
        
    }
    class ViewHolder{
        TextView text;
    }
} 
