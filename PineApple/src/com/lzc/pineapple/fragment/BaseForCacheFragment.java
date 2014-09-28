package com.lzc.pineapple.fragment;

import java.util.List;

import com.lzc.pineapple.entity.CacheVideoInfo;

import android.os.Bundle;

public abstract class BaseForCacheFragment extends BaseFragment{
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	public abstract List<CacheVideoInfo> getSelectedCacheVideo();
}
