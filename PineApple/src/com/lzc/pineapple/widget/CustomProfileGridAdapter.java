package com.lzc.pineapple.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import android.app.Activity;
import android.content.Context;

public abstract class CustomProfileGridAdapter implements BaseCustomGridAdapter{
    
    protected List<Observer> observers = new ArrayList<Observer>();
    protected Context          context;
    
    protected String           imageLoaderKey;
    protected boolean          isFromFlow;

    public CustomProfileGridAdapter() {
    }

    public CustomProfileGridAdapter(Activity context) {
        this.context = context;
    }
    public void register(Observer observer){
        observers.add(observer);
    }
    public void unregister(Observer observer){
        observers.remove(observer);
    }

    public void notifyDateListChanged(){
       for(Observer observer : observers){
           observer.update(null, null);
       }
    }

    public void setImageLoaderKey(String imageLoaderKey) {
        this.imageLoaderKey = imageLoaderKey;
    }
    
    public void setNeedPut(boolean isFromFlow) {
        this.isFromFlow = isFromFlow;
    }
}
