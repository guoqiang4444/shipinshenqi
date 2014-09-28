package com.lzc.pineapple.entity;

import java.io.Serializable;

public class RealVideo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String seconds;
	private String url;
	private String size;
	
	public String getSeconds() {
		return seconds;
	}
	public void setSeconds(String seconds) {
		this.seconds = seconds;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	
}
