package com.lzc.pineapple.entity;

import java.util.List;

public class SearchResultExtra {
	private List<HotVideoBasic> vHotVideoBasic;
	private List<PlatInfo> vPlatUrl;

	public List<HotVideoBasic> getvHotVideoBasic() {
		return vHotVideoBasic;
	}

	public void setvHotVideoBasic(List<HotVideoBasic> vHotVideoBasic) {
		this.vHotVideoBasic = vHotVideoBasic;
	}

	public List<PlatInfo> getvPlatUrl() {
		return vPlatUrl;
	}

	public void setvPlatUrl(List<PlatInfo> vPlatUrl) {
		this.vPlatUrl = vPlatUrl;
	}

}
