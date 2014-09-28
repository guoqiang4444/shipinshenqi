package com.lzc.pineapple.entity;

import java.io.Serializable;
import java.util.List;

public class WrapperCacheVideoInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<CacheVideoInfo> list;

	public List<CacheVideoInfo> getList() {
		return list;
	}

	public void setList(List<CacheVideoInfo> list) {
		this.list = list;
	}
	
}
