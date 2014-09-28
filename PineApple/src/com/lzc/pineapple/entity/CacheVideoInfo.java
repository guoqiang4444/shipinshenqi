package com.lzc.pineapple.entity;

import java.io.Serializable;

public class CacheVideoInfo implements Serializable {
	public static int START = 0x01;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String cover;
	private String name;
	private String url;
	private String src;
	private int cacheStatus;
	private String filePath;
	private int cacheSize;
	private int totalSize;
	private String cacheSpeed;
	private int progress;
	private String sSetNum;
	private String brief;
	
	private boolean isChanged;
	

	public boolean isChanged() {
		return isChanged;
	}

	public void setChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public String getsSetNum() {
		return sSetNum;
	}

	public void setsSetNum(String sSetNum) {
		this.sSetNum = sSetNum;
	}

	private boolean isSelected;
	private String videoId;

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public int getCacheStatus() {
		return cacheStatus;
	}

	public void setCacheStatus(int cacheStatus) {
		this.cacheStatus = cacheStatus;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public int getCacheSize() {
		return cacheSize;
	}

	public void setCacheSize(int cacheSize) {
		this.cacheSize = cacheSize;
	}

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	public String getCacheSpeed() {
		return cacheSpeed;
	}

	public void setCacheSpeed(String cacheSpeed) {
		this.cacheSpeed = cacheSpeed;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}
}
