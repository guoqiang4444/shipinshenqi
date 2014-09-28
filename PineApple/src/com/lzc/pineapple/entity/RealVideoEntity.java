package com.lzc.pineapple.entity;

import java.io.Serializable;
import java.util.List;

public class RealVideoEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String quality;
	private String title;
	private String type;
	private String quality_type;
	

	private List<RealVideo> videos;
	
	public String getQuality_type() {
		return quality_type;
	}
	public void setQuality_type(String quality_type) {
		this.quality_type = quality_type;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getQuality() {
		return quality;
	}
	public void setQuality(String quality) {
		this.quality = quality;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<RealVideo> getVideos() {
		return videos;
	}
	public void setVideos(List<RealVideo> videos) {
		this.videos = videos;
	}
	
	
}
