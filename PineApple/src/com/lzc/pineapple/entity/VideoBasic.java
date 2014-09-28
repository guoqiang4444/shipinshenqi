package com.lzc.pineapple.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoBasic implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 得分
     */
    private float        fScore;

    private int          iArea;

    private int          iEdition;

    private int          iIsNotEnd;

    private int          iMonth;

    private int          iRunTime;

    private int          iStatFlag;

    private int          iTotalPlayCnt;

    private int          iType;

    /**
     * 年份
     */
    private int          iYear;

    private long         lEditTime;

    private long         lUpdateTime;

    private long         lVideoId;

    /**
     * 主演
     */
    private String       sActor;

    /**
     * 地区
     */
    private String       sArea;

    /**
     * 简介/详情
     */
    private String       sBrief;

    private String       sColumn;

    /**
     * 导演
     */
    private String       sDirector;

    private String       sHost;

    private String       sOneDes;

    /**
     * 图片URL
     */
    private String       sPicUrl;

    private String       sQVBUrl;

    /**
     * 类型
     */
    private String       sSubType;

    private String       sTV;

    private String       sUpdateContent;

    /**
     * 片名
     */
    private String       sVideoName;

    private String       uiPopularValue;

    private List<SetNum> vSetNum = new ArrayList<SetNum>();

    private int[]        vSubType = new int[1];
    
    private int buy;
    
    public int getBuy() {
		return buy;
	}

	public void setBuy(int buy) {
		this.buy = buy;
	}

	/**
    public VideoBasic(){
        vSetNum = new ArrayList<SetNum>();
        vSubType = new int[1];
    }
    */
    

    public float getfScore() {
        return fScore;
    }

    public void setfScore(float fScore) {
        this.fScore = fScore;
    }

    public int getiArea() {
        return iArea;
    }

    public void setiArea(int iArea) {
        this.iArea = iArea;
    }

    public int getiEdition() {
        return iEdition;
    }

    public void setiEdition(int iEdition) {
        this.iEdition = iEdition;
    }

    public int getiIsNotEnd() {
        return iIsNotEnd;
    }

    public void setiIsNotEnd(int iIsNotEnd) {
        this.iIsNotEnd = iIsNotEnd;
    }

    public int getiMonth() {
        return iMonth;
    }

    public void setiMonth(int iMonth) {
        this.iMonth = iMonth;
    }

    public int getiRunTime() {
        return iRunTime;
    }

    public void setiRunTime(int iRunTime) {
        this.iRunTime = iRunTime;
    }

    public int getiStatFlag() {
        return iStatFlag;
    }

    public void setiStatFlag(int iStatFlag) {
        this.iStatFlag = iStatFlag;
    }

    public int getiTotalPlayCnt() {
        return iTotalPlayCnt;
    }

    public void setiTotalPlayCnt(int iTotalPlayCnt) {
        this.iTotalPlayCnt = iTotalPlayCnt;
    }

    public int getiType() {
        return iType;
    }

    public void setiType(int iType) {
        this.iType = iType;
    }

    public int getiYear() {
        return iYear;
    }

    public void setiYear(int iYear) {
        this.iYear = iYear;
    }

    public long getlEditTime() {
        return lEditTime;
    }

    public void setlEditTime(long lEditTime) {
        this.lEditTime = lEditTime;
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

    public String getsActor() {
        return sActor;
    }

    public void setsActor(String sActor) {
        this.sActor = sActor;
    }

    public String getsArea() {
        return sArea;
    }

    public void setsArea(String sArea) {
        this.sArea = sArea;
    }

    public String getsBrief() {
        return sBrief;
    }

    public void setsBrief(String sBrief) {
        this.sBrief = sBrief;
    }

    public String getsColumn() {
        return sColumn;
    }

    public void setsColumn(String sColumn) {
        this.sColumn = sColumn;
    }

    public String getsDirector() {
        return sDirector;
    }

    public void setsDirector(String sDirector) {
        this.sDirector = sDirector;
    }

    public String getsHost() {
        return sHost;
    }

    public void setsHost(String sHost) {
        this.sHost = sHost;
    }

    public String getsOneDes() {
        return sOneDes;
    }

    public void setsOneDes(String sOneDes) {
        this.sOneDes = sOneDes;
    }

    public String getsPicUrl() {
        return sPicUrl;
    }

    public void setsPicUrl(String sPicUrl) {
        this.sPicUrl = sPicUrl;
    }

    public String getsQVBUrl() {
        return sQVBUrl;
    }

    public void setsQVBUrl(String sQVBUrl) {
        this.sQVBUrl = sQVBUrl;
    }

    public String getsSubType() {
        return sSubType;
    }

    public void setsSubType(String sSubType) {
        this.sSubType = sSubType;
    }

    public String getsTV() {
        return sTV;
    }

    public void setsTV(String sTV) {
        this.sTV = sTV;
    }

    public String getsUpdateContent() {
        return sUpdateContent;
    }

    public void setsUpdateContent(String sUpdateContent) {
        this.sUpdateContent = sUpdateContent;
    }

    public String getsVideoName() {
        return sVideoName;
    }

    public void setsVideoName(String sVideoName) {
        this.sVideoName = sVideoName;
    }

    public String getUiPopularValue() {
        return uiPopularValue;
    }

    public void setUiPopularValue(String uiPopularValue) {
        this.uiPopularValue = uiPopularValue;
    }

    public List<SetNum> getvSetNum() {
        return vSetNum;
    }

    public void setvSetNum(List<SetNum> vSetNum) {
        this.vSetNum = vSetNum;
    }

    public int[] getvSubType() {
        return vSubType;
    }

    public void setvSubType(int[] vSubType) {
        this.vSubType = vSubType;
    }

    /**
    public static final Parcelable.Creator<VideoBasic> CREATOR = new Creator<VideoBasic>() {

       @Override
       public VideoBasic createFromParcel(Parcel source) {
           // TODO Auto-generated method stub
           // 必须按成员变量声明的顺序读取数据，不然会出现获取数据出错
           return new VideoBasic(source);
       }

       @Override
       public VideoBasic[] newArray(int size) {
           // TODO Auto-generated method stub
           return new VideoBasic[size];
       }
    };
   
    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }
    
    public VideoBasic(Parcel source) {
        fScore = (source.readFloat());
        iArea  = (source.readInt());
        iEdition = (source.readInt());
        iIsNotEnd = (source.readInt());
        iMonth = (source.readInt());
        iRunTime = (source.readInt());
        iStatFlag = (source.readInt());
        iTotalPlayCnt = (source.readInt());
        iType = (source.readInt());
        iYear = (source.readInt());
        lEditTime = (source.readLong());
        lUpdateTime = (source.readLong());
        lVideoId = (source.readLong());
        sActor = (source.readString());
        sArea = (source.readString());
        sBrief = (source.readString());
        sColumn = (source.readString());
        sColumn = (source.readString());
        sDirector = (source.readString());
        sHost = (source.readString());
        sOneDes = (source.readString());
        sPicUrl = (source.readString());
        sQVBUrl = (source.readString());
        sSubType = (source.readString());
        sTV = (source.readString());
        sUpdateContent = (source.readString());
        sVideoName = (source.readString());
        uiPopularValue = (source.readString());
        source.readTypedList(vSetNum, SetNum.CREATOR);
//        source.readIntArray(vSubType);
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        dest.writeFloat(fScore);
        dest.writeInt(iArea);
        dest.writeInt(iEdition);
        dest.writeInt(iIsNotEnd);
        dest.writeInt(iMonth);
        dest.writeInt(iRunTime);
        dest.writeInt(iStatFlag);
        dest.writeInt(iTotalPlayCnt);
        dest.writeInt(iType);
        dest.writeInt(iYear);
        dest.writeLong(lEditTime);
        dest.writeLong(lUpdateTime);
        dest.writeLong(lVideoId);
        dest.writeString(sActor);
        dest.writeString(sArea);
        dest.writeString(sBrief);
        dest.writeString(sColumn);
        dest.writeString(sDirector);
        dest.writeString(sHost);
        dest.writeString(sOneDes);
        dest.writeString(sPicUrl);
        dest.writeString(sQVBUrl);
        dest.writeString(sSubType);
        dest.writeString(sTV);
        dest.writeString(sUpdateContent);
        dest.writeString(sVideoName);
        dest.writeString(uiPopularValue);
        dest.writeTypedList(vSetNum);  
//        dest.writeIntArray(vSubType);
    }
    */
}
