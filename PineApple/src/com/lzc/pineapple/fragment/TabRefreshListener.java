package com.lzc.pineapple.fragment;

import com.lzc.pineapple.entity.RecommendVideos;
import com.lzc.pineapple.entity.StartPageEntity;
import com.lzc.pineapple.volley.VolleyError;

public interface TabRefreshListener {
    public void refreshCompleted(StartPageEntity response);
    public void refreshError(VolleyError error);
    public void refreshError(VolleyError error,int type);
    public void refreshRecommendCompleted(StartPageEntity response, RecommendVideos featureAreaVideo);
}
