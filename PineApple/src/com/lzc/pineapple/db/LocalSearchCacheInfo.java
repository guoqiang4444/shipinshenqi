package com.lzc.pineapple.db;

public class LocalSearchCacheInfo {
    private String name;
    private String createTime;
    private int type;
    
    
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
    
    public void setCreateTime(String createTime){
        this.createTime = createTime;
    }
    
    public String getCreateTime(){
        return createTime;
    }
    
    public void setType(int type){
        this.type = type;
    }
    public int getType(){
        return type;
    }
}
