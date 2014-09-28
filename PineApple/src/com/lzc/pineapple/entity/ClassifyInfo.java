
package com.lzc.pineapple.entity;

import java.util.List;

public class ClassifyInfo {

    private int iClassifyType;

    private List<Info> vInfo;

    public int getiClassifyType() {
        return iClassifyType;
    }

    public void setiClassifyType(int iClassifyType) {
        this.iClassifyType = iClassifyType;
    }

    public List<Info> getvInfo() {
        return vInfo;
    }

    public void setvInfo(List<Info> vInfo) {
        this.vInfo = vInfo;
    }

    public class Info {
        private int iClassifyId;

        private String sClassifyName;

        public int getiClassifyId() {
            return iClassifyId;
        }

        public void setiClassifyId(int iClassifyId) {
            this.iClassifyId = iClassifyId;
        }

        public String getsClassifyName() {
            return sClassifyName;
        }

        public void setsClassifyName(String sClassifyName) {
            this.sClassifyName = sClassifyName;
        }

    }
}
