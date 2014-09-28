
package com.lzc.pineapple.entity;

import java.io.Serializable;
import java.util.List;

public class VideoInfoEntity implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int iRet;

    private String sMsg;

    private int seqid;

    private VideoBasic stVideoBasic;

    private List<DoubanComment> vDoubanComment;
    
    private PlatUrl mPlatUrl;
    

    public PlatUrl getmPlatUrl() {
		return mPlatUrl;
	}

	public void setmPlatUrl(PlatUrl mPlatUrl) {
		this.mPlatUrl = mPlatUrl;
	}

	public int getiRet() {
        return iRet;
    }

    public void setiRet(int iRet) {
        this.iRet = iRet;
    }

    public String getsMsg() {
        return sMsg;
    }

    public void setsMsg(String sMsg) {
        this.sMsg = sMsg;
    }

    public int getSeqid() {
        return seqid;
    }

    public void setSeqid(int seqid) {
        this.seqid = seqid;
    }

    public VideoBasic getStVideoBasic() {
        return stVideoBasic;
    }

    public void setStVideoBasic(VideoBasic stVideoBasic) {
        this.stVideoBasic = stVideoBasic;
    }

    public List<DoubanComment> getvDoubanComment() {
        return vDoubanComment;
    }

    public void setvDoubanComment(List<DoubanComment> vDoubanComment) {
        this.vDoubanComment = vDoubanComment;
    }

}
