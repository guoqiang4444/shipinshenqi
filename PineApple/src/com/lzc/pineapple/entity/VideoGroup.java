
package com.lzc.pineapple.entity;

import java.io.Serializable;

public class VideoGroup implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int iGenWay ;
    private int iGroupId;
    private int iShowStyle;
    private int iTotalCnt;
    private int iVideoType;
    private String sGroupName;
    private String sIcon;
    
    public int getiGenWay() {
        return iGenWay;
    }
    public void setiGenWay(int iGenWay) {
        this.iGenWay = iGenWay;
    }
    public int getiGroupId() {
        return iGroupId;
    }
    public void setiGroupId(int iGroupId) {
        this.iGroupId = iGroupId;
    }
    public int getiShowStyle() {
        return iShowStyle;
    }
    public void setiShowStyle(int iShowStyle) {
        this.iShowStyle = iShowStyle;
    }
    public int getiTotalCnt() {
        return iTotalCnt;
    }
    public void setiTotalCnt(int iTotalCnt) {
        this.iTotalCnt = iTotalCnt;
    }
    public int getiVideoType() {
        return iVideoType;
    }
    public void setiVideoType(int iVideoType) {
        this.iVideoType = iVideoType;
    }
    public String getsGroupName() {
        return sGroupName;
    }
    public void setsGroupName(String sGroupName) {
        this.sGroupName = sGroupName;
    }
    public String getsIcon() {
        return sIcon;
    }
    public void setsIcon(String sIcon) {
        this.sIcon = sIcon;
    }

}
