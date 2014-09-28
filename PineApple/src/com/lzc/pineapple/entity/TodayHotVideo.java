
package com.lzc.pineapple.entity;

import java.util.List;

public class TodayHotVideo {

    private List<VideoBasic> vVideoData;

    private List<MobileTenTodayHot> wMobileTenTodayHots;

    public List<VideoBasic> getvVideoData() {
        return vVideoData;
    }

    public void setvVideoData(List<VideoBasic> vVideoData) {
        this.vVideoData = vVideoData;
    }

    public List<MobileTenTodayHot> getwMobileTenTodayHots() {
        return wMobileTenTodayHots;
    }

    public void setwMobileTenTodayHots(List<MobileTenTodayHot> wMobileTenTodayHots) {
        this.wMobileTenTodayHots = wMobileTenTodayHots;
    }

    public class MobileTenTodayHot {
        private String sTitle;

        private String sUrl;

        public String getsTitle() {
            return sTitle;
        }

        public void setsTitle(String sTitle) {
            this.sTitle = sTitle;
        }

        public String getsUrl() {
            return sUrl;
        }

        public void setsUrl(String sUrl) {
            this.sUrl = sUrl;
        }

    }
}
