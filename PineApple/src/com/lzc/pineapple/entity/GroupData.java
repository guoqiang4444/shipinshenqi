
package com.lzc.pineapple.entity;

import java.util.List;

public class GroupData {

    private VideoGroup stVideoGroup;

    private ClassifyInfo stClassify;

    private List<VideoBasic> vVideoBasic;

    public VideoGroup getStVideoGroup() {
        return stVideoGroup;
    }

    public void setStVideoGroup(VideoGroup stVideoGroup) {
        this.stVideoGroup = stVideoGroup;
    }

    public ClassifyInfo getStClassify() {
        return stClassify;
    }

    public void setStClassify(ClassifyInfo stClassify) {
        this.stClassify = stClassify;
    }

    public List<VideoBasic> getvVideoBasic() {
        return vVideoBasic;
    }

    public void setvVideoBasic(List<VideoBasic> vVideoBasic) {
        this.vVideoBasic = vVideoBasic;
    }

}
