package com.lzc.pineapple.search;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzc.pineapple.MainActivity;
import com.lzc.pineapple.R;
import com.lzc.pineapple.db.LocalSearchCacheInfo;
import com.lzc.pineapple.db.SearchDBHelper;
import com.lzc.pineapple.entity.StartPageEntity;
import com.lzc.pineapple.fragment.BaseTabFragment;
import com.lzc.pineapple.util.Utils;
import com.lzc.pineapple.volley.VolleyError;

public class SearchFragment extends BaseTabFragment implements SearChListener,SearchDBListener, OnClickListener {

    private SearchDBHelper     dbHelper;
    public static final String HOT_VIDEO_KEY_WORD       = "HOT_VIDEO_KEY_WORD";

    public static final int    HOT_VIDEO_FRAME          = 0;
    public static final int    LOCAL_SEARCH_CACHE_FRAME = 1;

    private int                fragmentType             = HOT_VIDEO_FRAME;

    private ImageView          search;
    private ImageView          delete;
    private EditText           editText;
    private TextView           searchTextView;
    
    private MainActivity       activity;
    
    private List<String>       hotWords = new ArrayList<String>();
    
    private Fragment fragment = null;
 
    public static final Fragment newInstance() {
        Fragment fragment = new SearchFragment();
        Bundle bundle = new Bundle();
        // bundle.putString(Constant.DEVICE_NAME_KEY, deviceName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_search, null);
        activity = (MainActivity) getActivity();
        openSearchDBhelper();
        initViews(view);
        setListener();
        initValues();
        changeFrame(fragmentType);
        return view;
    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
        closeSearchDBHelper();
    }
    @Override
    public void refreshCompleted(StartPageEntity response) {
        // TODO Auto-generated method stub
        super.refreshCompleted(response);
        hotWords = response.getvHotWords();
        if(HOT_VIDEO_FRAME == fragmentType){
            ((HotVideoFragment) fragment).updateHotWords(hotWords);
        }
    }
    @Override
    public void refreshError(VolleyError error) {
        // TODO Auto-generated method stub
        super.refreshError(error);
    }
    private void initValues(){
        StartPageEntity entity = activity.getStartPageEntity();
        
        if(entity != null){
            hotWords = entity.getvHotWords();
        }
           
    }
    private void initViews(View view) {
        searchTextView = (TextView) view.findViewById(R.id.search);

        delete = (ImageView) view.findViewById(R.id.image_delete);

        search = (ImageView) view.findViewById(R.id.image_search);
        editText = (EditText) view.findViewById(R.id.editext_content);
        editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

    }
    
    private void setListener() {

        searchTextView.setOnClickListener(this);
        search.setOnClickListener(this);
        editText.setOnClickListener(this);
        delete.setOnClickListener(this);
    }

    private void openSearchDBhelper() {
        dbHelper = new SearchDBHelper(this.getActivity());

        dbHelper.open();
    }

    private void closeSearchDBHelper() {
        dbHelper.close();
    }

    @Override
    public void changeFragment(int type) {
        // TODO Auto-generated method stub
        changeFrame(type);
    }

    private void insertRecordToDataBase(String nowSearch) {
        // TODO Auto-generated method stub
        String createTime = Utils.getSysNowTime();

        LocalSearchCacheInfo localSearchCacheInfo = new LocalSearchCacheInfo();
        localSearchCacheInfo.setCreateTime(createTime);

        localSearchCacheInfo.setName(nowSearch);
        dbHelper.insertVideoDataToTable(localSearchCacheInfo);
    }

    

    private void changeFrame(int frameClass) {
//        if (fragmentType == frameClass) {
//            return;
//        }
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = null;
        if (fm != null) {
            ft = fm.beginTransaction();
        } else {
            return;
        }
        
        switch (frameClass) {
            case HOT_VIDEO_FRAME:
                fragment = HotVideoFragment.newInstance(hotWords);
                ((HotVideoFragment) fragment).setSearchListener(this);
                ft.replace(R.id.fragment_content, fragment);
                break;
            case LOCAL_SEARCH_CACHE_FRAME:
                fragment = LocalSearchCacheFragment.newInstance();
                ((LocalSearchCacheFragment) fragment).setSearchListener(this);
                ((LocalSearchCacheFragment) fragment).setSearchDBListener(this);
                ft.replace(R.id.fragment_content, fragment);
                break;
        }
        
        ft.commit();
        fragmentType = frameClass;
    }

    @Override
    public void search(String nowSearch) {
        // TODO Auto-generated method stub
        insertRecordToDataBase(nowSearch);
        searchKeyState(nowSearch);
    }

    private void searchKeyState(String nowSearch) {

        if (!Utils.hasInternet(getActivity())) {
            Utils.showTips(getActivity(),
                    getActivity().getResources().getString(R.string.none_network));
            return;
        }
        
        VideoSearchedActivity.launch(getActivity(), nowSearch);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {

            case R.id.search:
                search();
                break;
            case R.id.image_search:
                break;
            case R.id.editext_content:
                String nowSearch = editText.getText().toString();
                goToLocalSearchCache();
                break;
            case R.id.image_delete:
                clearEditContent();
                break;
        }
    }
    private void search(){
        String nowSearch = editText.getText().toString();
        if (nowSearch == null || nowSearch.trim().length() == 0) {
            // 输入的搜索内容无效
            Utils.showTips(getActivity(),getActivity().getResources().getString(R.string.more_search_input_invalid));
            return;
        }

        search(nowSearch);
    }
    private void goToLocalSearchCache(){
        changeFragment(LOCAL_SEARCH_CACHE_FRAME);
    }
    private void clearEditContent(){
        editText.setText("");   
    }

    @Override
    public Cursor getCacheCursor() {
        // TODO Auto-generated method stub
        return dbHelper.getVideoTopFive();
    }

    @Override
    public void clearCache() {
        // TODO Auto-generated method stub
        dbHelper.clearVideoTable();
    }
}
