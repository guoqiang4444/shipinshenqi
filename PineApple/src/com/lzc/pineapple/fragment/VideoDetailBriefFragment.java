package com.lzc.pineapple.fragment;

import com.lzc.pineapple.R;
import com.lzc.pineapple.entity.VideoInfoEntity;
import com.lzc.pineapple.search.LocalSearchCacheFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 视频简介Fragment
 * @author zengchan.lzc
 *
 */
public class VideoDetailBriefFragment extends Fragment{
	private final static String VIDEO_INFO_KEY = "video_info_key";
	private VideoInfoEntity videoInfo;
	private TextView briefText;
    public static final Fragment newInstance(VideoInfoEntity videoInfo) {
        Fragment fragment = new VideoDetailBriefFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(VIDEO_INFO_KEY, videoInfo);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO aAuto-generated method stub
    	getValues();
        View view = inflater.inflate(R.layout.fragment_video_detail_brief_layout, null);
        initViews(view);
        return view;
    }
    private void getValues(){
    	videoInfo = (VideoInfoEntity) this.getArguments().getSerializable(VIDEO_INFO_KEY);
    }
    private void initViews(View view){
    	briefText = (TextView)view.findViewById(R.id.brief_text);
    	if(videoInfo != null){
    		String brief = videoInfo.getStVideoBasic().getsBrief();
    		briefText.setText(brief);
    	}
    	
    }
}
