package com.lzc.pineapple.entity;

import java.io.Serializable;
import java.util.List;

public class RealVideoInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int datanum;
	private String type;
	private int ret_code;
	private List<RealVideoEntity> rows;
//	private RealVideoEntity rows;
//	
//	
//	public RealVideoEntity getRows() {
//		return rows;
//	}
//	public void setRows(RealVideoEntity rows) {
//		this.rows = rows;
//	}
	public int getDatanum() {
		return datanum;
	}
	public void setDatanum(int datanum) {
		this.datanum = datanum;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getRet_code() {
		return ret_code;
	}
	public void setRet_code(int ret_code) {
		this.ret_code = ret_code;
	}
	public List<RealVideoEntity> getRows() {
		return rows;
	}
	public void setRows(List<RealVideoEntity> rows) {
		this.rows = rows;
	}
}
