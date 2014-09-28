
package com.lzc.pineapple.entity;

import java.io.Serializable;

public class DoubanComment implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private float fScore;

    private long lCommentTime;

    private long lVideoId;

    private String sAuthor;

    private String sBrief;

    private String sTitle;

    private String sUrl;

    public float getfScore() {
        return fScore;
    }

    public void setfScore(float fScore) {
        this.fScore = fScore;
    }

    public long getlCommentTime() {
        return lCommentTime;
    }

    public void setlCommentTime(long lCommentTime) {
        this.lCommentTime = lCommentTime;
    }

    public long getlVideoId() {
        return lVideoId;
    }

    public void setlVideoId(long lVideoId) {
        this.lVideoId = lVideoId;
    }

    public String getsAuthor() {
        return sAuthor;
    }

    public void setsAuthor(String sAuthor) {
        this.sAuthor = sAuthor;
    }

    public String getsBrief() {
        return sBrief;
    }

    public void setsBrief(String sBrief) {
        this.sBrief = sBrief;
    }

    public String getsTitle() {
        return sTitle;
    }

    public void setsTitle(String sTitle) {
        this.sTitle = sTitle;
    }

    public String getsUrl() {
        return sUrl;
    }

    public void setsUrl(String sUrl) {
        this.sUrl = sUrl;
    }

}
