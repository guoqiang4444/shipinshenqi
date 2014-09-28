
package com.lzc.pineapple.entity;

import java.util.List;

public class StartPageEntity {

    private int iRet;

    private int seqid;

    private List<Banner> vBanners;

    private List<String> vHotWords;

    private TodayHotVideo wTodayHotVideo;

    private List<GroupData> vGroupData;

    public int getiRet() {
        return iRet;
    }

    public void setiRet(int iRet) {
        this.iRet = iRet;
    }

    public int getSeqid() {
        return seqid;
    }

    public void setSeqid(int seqid) {
        this.seqid = seqid;
    }

    public List<Banner> getvBanners() {
        return vBanners;
    }

    public void setvBanners(List<Banner> vBanners) {
        this.vBanners = vBanners;
    }

    public List<String> getvHotWords() {
        return vHotWords;
    }

    public void setvHotWords(List<String> vHotWords) {
        this.vHotWords = vHotWords;
    }

    public TodayHotVideo getwTodayHotVideo() {
        return wTodayHotVideo;
    }

    public void setwTodayHotVideo(TodayHotVideo wTodayHotVideo) {
        this.wTodayHotVideo = wTodayHotVideo;
    }

    public List<GroupData> getvGroupData() {
        return vGroupData;
    }

    public void setvGroupData(List<GroupData> vGroupData) {
        this.vGroupData = vGroupData;
    }

}
