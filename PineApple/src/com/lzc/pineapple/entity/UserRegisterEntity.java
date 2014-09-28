package com.lzc.pineapple.entity;

public class UserRegisterEntity {
	//0表示成功，非0 表示失败 -1表示输入参数不全 -2 表示该用户已存在
	private int ret_code;

	public int getRet_code() {
		return ret_code;
	}

	public void setRet_code(int ret_code) {
		this.ret_code = ret_code;
	}
	
}
