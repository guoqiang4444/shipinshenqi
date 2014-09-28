package com.lzc.pineapple.entity;

public class FilterInfo {
	 private int iClassifyId;

     private String sClassifyName;
     
     private boolean isSelected ;

     public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

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
