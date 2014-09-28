package com.lzc.pineapple.entity;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class SetNum implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private int    iIsOpenSniff;

    /**
     * 播放来源（10个）4：乐视 2：爱奇艺 5：优酷 3：腾讯 迅雷56搜狐风行PPSPPTV
     * 播放源 "1":"搜狐", "2":"爱奇艺", "3":"腾讯", "4":"乐视", "5":"优酷", 
     * "9":"PPTV","10":"迅雷","7":"风行", "11":"PPS", "14":"yezibo", 
     * "16":"56","25":"芒果","15":"中国网络电视台","8":"电影网"
     */
    private int    iSrc;

    /**
     * 更新到第N集
     */
    private String sCurSetNum;

    /**
     * 总集数
     */
    private String sTotalSetNum;

    private String sUpdateSetNum;

    public int getiIsOpenSniff() {
        return iIsOpenSniff;
    }

    public void setiIsOpenSniff(int iIsOpenSniff) {
        this.iIsOpenSniff = iIsOpenSniff;
    }

    public int getiSrc() {
        return iSrc;
    }

    public void setiSrc(int iSrc) {
        this.iSrc = iSrc;
    }

    public String getsCurSetNum() {
        return sCurSetNum;
    }

    public void setsCurSetNum(String sCurSetNum) {
        this.sCurSetNum = sCurSetNum;
    }

    public String getsTotalSetNum() {
        return sTotalSetNum;
    }

    public void setsTotalSetNum(String sTotalSetNum) {
        this.sTotalSetNum = sTotalSetNum;
    }

    public String getsUpdateSetNum() {
        return sUpdateSetNum;
    }

    public void setsUpdateSetNum(String sUpdateSetNum) {
        this.sUpdateSetNum = sUpdateSetNum;
    }
    /**
    // 5.反序列化对象
    public static final Parcelable.Creator<SetNum> CREATOR = new Creator() {

       @Override
       public SetNum createFromParcel(Parcel source) {
           // TODO Auto-generated method stub
           // 必须按成员变量声明的顺序读取数据，不然会出现获取数据出错
           SetNum setNum  = new SetNum();  
           setNum.iIsOpenSniff = source.readInt();  
           setNum.iSrc = source.readInt();  
           setNum.sCurSetNum = source.readString();
           setNum.sTotalSetNum = source.readString();
           setNum.sUpdateSetNum = source.readString();
           
           return setNum;
       }

       @Override
       public SetNum[] newArray(int size) {
           // TODO Auto-generated method stub
           return new SetNum[size];
       }
    };

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        dest.writeInt(iIsOpenSniff);
        dest.writeInt(iSrc);
        dest.writeString(sCurSetNum);
        dest.writeString(sTotalSetNum);
        dest.writeString(sUpdateSetNum);
    }
    */
}
