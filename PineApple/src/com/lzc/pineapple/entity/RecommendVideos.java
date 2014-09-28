package com.lzc.pineapple.entity;

import java.io.Serializable;
import java.util.List;

import com.lzc.pineapple.search.SearChListener;

/**
 * 特色专区
 * @author zengchan.lzc
 *
 */
public class RecommendVideos implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List<RecommendVideo> rows;

    private int ret;

    private int datanum;

    public List<RecommendVideo> getRows() {
        return rows;
    }

    public void setRows(List<RecommendVideo> rows) {
        this.rows = rows;
    }

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public int getDatanum() {
        return datanum;
    }

    public void setDatanum(int datanum) {
        this.datanum = datanum;
    }

}
