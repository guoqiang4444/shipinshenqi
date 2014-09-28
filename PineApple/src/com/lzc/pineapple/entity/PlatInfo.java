package com.lzc.pineapple.entity;

import java.io.Serializable;

public class PlatInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int iRunTime;
	private int iSrc;
	private long lUpdateTime;
	private long lVideoId;
	private String sBrief;
	private String sDateTime;
	private String sDetail;
	private String sPicUrl;
	private String sSetNum;
	private String sUrl;
	
	public int getiRunTime() {
		return iRunTime;
	}
	public void setiRunTime(int iRunTime) {
		this.iRunTime = iRunTime;
	}
	public int getiSrc() {
		return iSrc;
	}
	public void setiSrc(int iSrc) {
		this.iSrc = iSrc;
	}
	public long getlUpdateTime() {
		return lUpdateTime;
	}
	public void setlUpdateTime(long lUpdateTime) {
		this.lUpdateTime = lUpdateTime;
	}
	public long getlVideoId() {
		return lVideoId;
	}
	public void setlVideoId(long lVideoId) {
		this.lVideoId = lVideoId;
	}
	public String getsBrief() {
		return sBrief;
	}
	public void setsBrief(String sBrief) {
		this.sBrief = sBrief;
	}
	public String getsDateTime() {
		return sDateTime;
	}
	public void setsDateTime(String sDateTime) {
		this.sDateTime = sDateTime;
	}
	public String getsDetail() {
		return sDetail;
	}
	public void setsDetail(String sDetail) {
		this.sDetail = sDetail;
	}
	public String getsPicUrl() {
		return sPicUrl;
	}
	public void setsPicUrl(String sPicUrl) {
		this.sPicUrl = sPicUrl;
	}
	public String getsSetNum() {
		return sSetNum;
	}
	public void setsSetNum(String sSetNum) {
		this.sSetNum = sSetNum;
	}
	public String getsUrl() {
		return sUrl;
	}
	public void setsUrl(String sUrl) {
		this.sUrl = sUrl;
	}
	
}
