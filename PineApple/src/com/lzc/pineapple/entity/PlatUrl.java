package com.lzc.pineapple.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

public class PlatUrl implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SerializedName("1")
	private List<PlatInfo> a1;//sohu
	@SerializedName("2")
	private List<PlatInfo> a2;//qiyi
	@SerializedName("3")
	private List<PlatInfo> a3;//tecent
	@SerializedName("4")
	private List<PlatInfo> a4;//leshi
	@SerializedName("5")
	private List<PlatInfo> a5;//youku
	@SerializedName("6")
	private List<PlatInfo> a6;//tudou
	@SerializedName("7")
	private List<PlatInfo> a7;//风行
	@SerializedName("8")
	private List<PlatInfo> a8;
	@SerializedName("9")
	private List<PlatInfo> a9;//PPTV
	@SerializedName("10")
	private List<PlatInfo> a10;//xunlei
	@SerializedName("11")
	private List<PlatInfo> a11;//PPS
	@SerializedName("15")
	private List<PlatInfo> a15;//CNTV
	@SerializedName("16")
	private List<PlatInfo> a16;//56
	@SerializedName("17")
	private List<PlatInfo> a17;//baidu
	@SerializedName("18")
	private List<PlatInfo> a18;//baofeng
	@SerializedName("25")
	private List<PlatInfo> a25;//hunantv
	
	public List<PlatInfo> getA9() {
		return a9;
	}
	public void setA9(List<PlatInfo> a9) {
		this.a9 = a9;
	}
	public List<PlatInfo> getA11() {
		return a11;
	}
	public void setA11(List<PlatInfo> a11) {
		this.a11 = a11;
	}
	public List<PlatInfo> getA18() {
		return a18;
	}
	public void setA18(List<PlatInfo> a18) {
		this.a18 = a18;
	}
	public List<PlatInfo> getA25() {
		return a25;
	}
	public void setA25(List<PlatInfo> a25) {
		this.a25 = a25;
	}
	public List<PlatInfo> getA1() {
		return a1;
	}
	public void setA1(List<PlatInfo> a1) {
		this.a1 = a1;
	}
	public List<PlatInfo> getA2() {
		return a2;
	}
	public void setA2(List<PlatInfo> a2) {
		this.a2 = a2;
	}
	public List<PlatInfo> getA3() {
		return a3;
	}
	public void setA3(List<PlatInfo> a3) {
		this.a3 = a3;
	}
	public List<PlatInfo> getA4() {
		return a4;
	}
	public void setA4(List<PlatInfo> a4) {
		this.a4 = a4;
	}
	public List<PlatInfo> getA5() {
		return a5;
	}
	public void setA5(List<PlatInfo> a5) {
		this.a5 = a5;
	}
	public List<PlatInfo> getA6() {
		return a6;
	}
	public void setA6(List<PlatInfo> a6) {
		this.a6 = a6;
	}
	public List<PlatInfo> getA7() {
		return a7;
	}
	public void setA7(List<PlatInfo> a7) {
		this.a7 = a7;
	}
	public List<PlatInfo> getA17() {
		return a17;
	}
	public void setA17(List<PlatInfo> a17) {
		this.a17 = a17;
	}
	
	public List<PlatInfo> getA8() {
		return a8;
	}
	public void setA8(List<PlatInfo> a8) {
		this.a8 = a8;
	}
	public List<PlatInfo> getA10() {
		return a10;
	}
	public void setA10(List<PlatInfo> a10) {
		this.a10 = a10;
	}
	public List<PlatInfo> getA15() {
		return a15;
	}
	public void setA15(List<PlatInfo> a15) {
		this.a15 = a15;
	}
	public List<PlatInfo> getA16() {
		return a16;
	}
	public void setA16(List<PlatInfo> a16) {
		this.a16 = a16;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public List<PlatInfo> getPlatInfos(int sourceType){
		switch (sourceType) {
		case 1:
			return a1;
		case 2:
			return a2;
		case 3:
			return a3;
		case 4:
			return a4;
		case 5:
			return a5;
		case 6:
			return a6;
		case 7:
			return a7;
		case 8:
			return a8;
		case 9:
			return a9;
		case 10:
			return a10;
		case 11:
			return a11;
		case 15:
			return a15;
		case 16:
			return a16;
		case 17:
			return a17;
		case 18:
			return a18;
		case 25:
			return a25;
		default:
			break;
		}
		return null;
	}
	public List<PlatInfo> getPlatInfos(){
		if(a1 != null && !a1.isEmpty()){
			return a1;
		}
		else if(a2 != null && ! a2.isEmpty()){
			return a2;
		}
		else if(a3 != null && ! a3.isEmpty()){
			return a3;
		}
		else if(a4!= null && ! a4.isEmpty()){
			return a4;
		}
		else if(a5 != null && ! a5.isEmpty()){
			return a5;
		}
		else if(a6 != null && ! a6.isEmpty()){
			return a6;
		}
		else if(a7 != null && ! a7.isEmpty()){
			return a7;
		}
		else if(a8 != null && ! a8.isEmpty()){
			return a8;
		}
		else if(a9 != null && ! a9.isEmpty()){
			return a9;
		}
		else if(a10 != null && ! a10.isEmpty()){
			return a10;
		}
		else if(a11 != null && ! a11.isEmpty()){
			return a11;
		}
		else if(a15 != null && ! a15.isEmpty()){
			return a15;
		}
		else if(a16 != null && ! a16.isEmpty()){
			return a16;
		}
		else if(a17 != null && ! a17.isEmpty()){
			return a17;
		}
		else if(a18 != null && ! a18.isEmpty()){
			return a18;
		}
		else if(a25 != null && ! a25.isEmpty()){
			return a25;
		}
		else{
			return null;
		}
	}
	public List<List<PlatInfo>> getListPlatInfo(){
		List<List<PlatInfo>> listPi = new ArrayList<List<PlatInfo>>();
		if(a1 != null && !a1.isEmpty()){
			listPi.add(a1);
		}
		if(a2 != null && ! a2.isEmpty()){
			listPi.add(a2);
		}
		if(a3 != null && ! a3.isEmpty()){
			listPi.add(a3);
		}
		if(a4!= null && ! a4.isEmpty()){
			listPi.add(a4);
		}
	    if(a5 != null && ! a5.isEmpty()){
			listPi.add(a5);
		}
		if(a6 != null && ! a6.isEmpty()){
			listPi.add(a6);
		}
		if(a7 != null && ! a7.isEmpty()){
			listPi.add(a7);
		}
	    if(a8 != null && ! a8.isEmpty()){
			listPi.add(a8);
		}
		if(a9 != null && ! a9.isEmpty()){
			listPi.add(a9);
		}
		if(a10 != null && ! a10.isEmpty()){
			listPi.add(a10);
		}
		if(a11 != null && ! a11.isEmpty()){
			listPi.add(a11);
		}
		if(a15 != null && ! a15.isEmpty()){
			listPi.add(a15);
		}
		if(a16 != null && ! a16.isEmpty()){
			listPi.add(a16);
		}
		if(a17 != null && ! a17.isEmpty()){
			listPi.add(a17);
		}
		if(a18 != null && ! a18.isEmpty()){
			listPi.add(a18);
		}
		if(a25 != null && ! a25.isEmpty()){
			listPi.add(a25);
		}
		
		return listPi;
	}
	
}
