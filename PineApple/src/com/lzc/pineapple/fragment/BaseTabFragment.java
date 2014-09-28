package com.lzc.pineapple.fragment;

import com.lzc.pineapple.entity.RecommendVideos;
import com.lzc.pineapple.entity.StartPageEntity;
import com.lzc.pineapple.volley.VolleyError;

import android.support.v4.app.Fragment;

public class BaseTabFragment extends BaseFragment implements TabRefreshListener{

    @Override
    public void refreshCompleted(StartPageEntity response) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void refreshError(VolleyError error) {
        // TODO Auto-generated method stub
        
    }

	@Override
	public void refreshError(VolleyError error, int type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refreshRecommendCompleted(StartPageEntity response,
			RecommendVideos featureAreaVideo) {
		// TODO Auto-generated method stub
		
	}

}
