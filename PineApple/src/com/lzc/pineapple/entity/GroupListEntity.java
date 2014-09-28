
package com.lzc.pineapple.entity;

import java.util.List;

public class GroupListEntity {

    private int iRet;

    private int iIsClassifyReq;

    private int iSortType;

    /**
     * 总结果数（包含片花或短视频）
     */
    private int iTotal;

    private int seqid;

    private List<ClassifyInfo> vClassifyInfo;

    private List<VideoBasic> vVideoBasic;

    public int getiRet() {
        return iRet;
    }

    public void setiRet(int iRet) {
        this.iRet = iRet;
    }

    public int getiIsClassifyReq() {
        return iIsClassifyReq;
    }

    public void setiIsClassifyReq(int iIsClassifyReq) {
        this.iIsClassifyReq = iIsClassifyReq;
    }

    public int getiSortType() {
        return iSortType;
    }

    public void setiSortType(int iSortType) {
        this.iSortType = iSortType;
    }

    public int getiTotal() {
        return iTotal;
    }

    public void setiTotal(int iTotal) {
        this.iTotal = iTotal;
    }

    public int getSeqid() {
        return seqid;
    }

    public void setSeqid(int seqid) {
        this.seqid = seqid;
    }

    public List<VideoBasic> getvVideoBasic() {
        return vVideoBasic;
    }

    public void setvVideoBasic(List<VideoBasic> vVideoBasic) {
        this.vVideoBasic = vVideoBasic;
    }

    public List<ClassifyInfo> getvClassifyInfo() {
        return vClassifyInfo;
    }

    public void setvClassifyInfo(List<ClassifyInfo> vClassifyInfo) {
        this.vClassifyInfo = vClassifyInfo;
    }

}
