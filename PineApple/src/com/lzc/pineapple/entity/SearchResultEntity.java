
package com.lzc.pineapple.entity;

import java.util.List;

public class SearchResultEntity {

    private int iRet;

    /**
     * 返回片源数
     */
    private int iSrcTotal;

    /**
     * 总结果数（包含片花或短视频）
     */
    private int iTotal;

    private int seqid;

    private List<VideoBasic> vVideoBasic;
    
    private SearchResultExtra stSearchResultExtra;
    

    public SearchResultExtra getStSearchResultExtra() {
		return stSearchResultExtra;
	}

	public void setStSearchResultExtra(SearchResultExtra stSearchResultExtra) {
		this.stSearchResultExtra = stSearchResultExtra;
	}

	public int getiRet() {
        return iRet;
    }

    public void setiRet(int iRet) {
        this.iRet = iRet;
    }

    public int getiSrcTotal() {
        return iSrcTotal;
    }

    public void setiSrcTotal(int iSrcTotal) {
        this.iSrcTotal = iSrcTotal;
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

}
