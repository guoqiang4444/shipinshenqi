package com.lzc.pineapple;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;

import com.lzc.pineapple.util.NetworkRequest;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class PApplication  extends Application {
    private List<Activity> activityList = null;

    private static PApplication instance;

    public PApplication() {
        activityList = new LinkedList<Activity>();
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        initImageLoaderConfig();
        initVolley();
    }
    private void initImageLoaderConfig() {

        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer())
                .showImageOnFail(R.drawable.bg_cover)
                .build();
        
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(displayImageOptions)
                .diskCacheExtraOptions(480, 800, null)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .writeDebugLogs()
                .build();

        ImageLoader.getInstance().init(config);

    }
    private void initVolley(){
        NetworkRequest.init(this);   
    }
    public static PApplication getInstance() {
        if (instance == null) {
            instance = new PApplication();
        }
        return instance;
    }

    public void addActivity(Activity activity) {
        if (activityList != null && activityList.size() > 0) {
            if (!activityList.contains(activity)) {
                activityList.add(activity);
            }
        } else {
            activityList.add(activity);
        }
    }

    public void removeActivity(Activity activity) {
        if (activityList != null && activityList.size() > 0) {
            if (activityList.contains(activity)) {
                activityList.remove(activity);
            }
        }
    }

    public void exit() {
        clearActivity();
        System.exit(0);
    }

    public void clearActivity() {
        if (activityList != null && activityList.size() > 0) {
            for (Activity activity : activityList) {
                activity.finish();
            }
        }
    }

}

