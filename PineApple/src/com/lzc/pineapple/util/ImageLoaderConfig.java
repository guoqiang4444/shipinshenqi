package com.lzc.pineapple.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.lzc.pineapple.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class ImageLoaderConfig {
    private ImageLoaderConfig() {
    };


    private final static DisplayImageOptions posterImageOption = new DisplayImageOptions.Builder().showStubImage(R.drawable.bg_h_cover)
            .showImageForEmptyUri(R.drawable.bg_h_cover).cacheInMemory().cacheOnDisc().build();
    
    private final static DisplayImageOptions verticalImageOption = new DisplayImageOptions.Builder().showStubImage(R.drawable.bg_cover)
            .showImageForEmptyUri(R.drawable.bg_cover).cacheInMemory().cacheOnDisc().build();

    private final static ImageLoadingListener videoListener = new SimpleImageLoadingListener() {
        @Override
        public void onLoadingStarted(String imageUri, View view) {
            if (view instanceof ImageView) {
                ((ImageView) view).setScaleType(ScaleType.FIT_XY);
            }
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null && view instanceof ImageView) {
                ((ImageView) view).setScaleType(ScaleType.CENTER_CROP);
            }
        }
    };

    public final static DisplayImageOptions getPosterImageOption() {
        return posterImageOption;
    }
    public final static DisplayImageOptions getVerticalImageOption(){
    	return verticalImageOption;
    }
    public final static ImageLoadingListener getImageListener() {
        return videoListener;
    }
    
}
