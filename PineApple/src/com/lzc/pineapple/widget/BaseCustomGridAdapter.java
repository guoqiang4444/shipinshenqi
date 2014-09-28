package com.lzc.pineapple.widget;

import java.util.Observer;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public interface BaseCustomGridAdapter {
    public View getView(int position, View convertView, ViewGroup parent);
    public int getCount();
    public void register(Observer observer);
    public void unregister(Observer observer);
}
