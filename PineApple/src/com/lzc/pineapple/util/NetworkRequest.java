package com.lzc.pineapple.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lzc.pineapple.volley.AuthFailureError;
import com.lzc.pineapple.volley.Cache;
import com.lzc.pineapple.volley.DefaultRetryPolicy;
import com.lzc.pineapple.volley.Request;
import com.lzc.pineapple.volley.RequestQueue;
import com.lzc.pineapple.volley.Response;
import com.lzc.pineapple.volley.toolbox.BitmapLruCache;
import com.lzc.pineapple.volley.toolbox.GsonRequest;
import com.lzc.pineapple.volley.toolbox.ImageLoader;
import com.lzc.pineapple.volley.toolbox.RequestFuture;
import com.lzc.pineapple.volley.toolbox.StringRequest;
import com.lzc.pineapple.volley.toolbox.Volley;

import android.app.ActivityManager;
import android.content.Context;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by duanjunlei on 14-6-23.
 */
public class NetworkRequest {
    private static RequestQueue mRequestQueue;
    private static ImageLoader mImageLoader;

    /** prevent make many instances */
    private NetworkRequest() {
    }

    /**
     * must call this to init at application
     *
     * @param context
     */
    public static void init(Context context) {
        int memoryClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE))
                .getMemoryClass();

        mRequestQueue = Volley.newRequestQueue(context);
        mImageLoader = new ImageLoader(mRequestQueue, new BitmapLruCache(1024 * 1024 * memoryClass / 6));
    }

    public static RequestQueue getRequestQueue() {
        if (null != mRequestQueue) {
            return mRequestQueue;
        } else {
            throw new IllegalStateException("RequestQueue not initialized");
        }
    }

    public static ImageLoader getImageLoader() {
        if (null != mImageLoader) {
            return mImageLoader;
        } else {
            throw new IllegalStateException("ImageLoader not initialized");
        }
    }

    public static Request<String> get(String url,
                                      Response.Listener<String> listener,
                                      Response.ErrorListener errorListener) {
        RequestQueue queue = getRequestQueue();
        return queue.add(new StringRequest(url, listener, errorListener));
    }

    public static <T> Request<T> get(String url,
                                     Class<T> clazz,
                                     Response.Listener<T> listener,
                                     Response.ErrorListener errorListener) {
        return get(url, clazz, listener, errorListener, true);
    }

    public static <T> Request<T> get(String url,
                                     Class<T> clazz,
                                     Response.Listener<T> listener,
                                     Response.ErrorListener errorListener,
                                     boolean needUrlDecode) {
        RequestQueue queue = getRequestQueue();
        GsonRequest<T> request = new GsonRequest<T>(url, clazz, listener, errorListener, needUrlDecode);
        request.setRetryPolicy(new DefaultRetryPolicy(5000, 5, 2.f));
        return queue.add(request);
    }

    public static String getSync(String url) {
        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest request = new StringRequest(url, future, future);
        RequestQueue queue = getRequestQueue();
        queue.add(request);

        String response;
        try {
            response = future.get();
        } catch (InterruptedException e) {
            response = "";
        } catch (ExecutionException e) {
            response = "";
        }

        return response;
    }

    public static void post(String url,
                            final Map<String, String> params,
                            Response.Listener<String> listener,
                            Response.ErrorListener errorListener) {
        RequestQueue queue = getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, url, listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        queue.add(request);
    }

    public static Request<String> post(String url,
                                       final byte[] buffer,
                                       Response.Listener<String> listener,
                                       Response.ErrorListener errorListener) {
        RequestQueue queue = getRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, url, listener, errorListener) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return buffer;
            }
        };
        return queue.add(request);
    }

    public static <T> Request<T> post(String url,
                                      final Map<String, String> params,
                                      Class<T> clazz,
                                      Response.Listener<T> listener,
                                      Response.ErrorListener errorListener) {
        RequestQueue queue = getRequestQueue();
        GsonRequest<T> request = new GsonRequest<T>(url, clazz, listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        return queue.add(request);
    }

    public static String getCache(String url) {
        RequestQueue queue = getRequestQueue();
        Cache.Entry cache = queue.getCache().get(url);

        if (null != cache) {
            return new String(cache.data);
        }

        return null;
    }

    public static <T> T getCache(String url, Class<T> clazz) {
        RequestQueue queue = getRequestQueue();
        Cache.Entry cache = queue.getCache().get(url);

        if (null != cache) {
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(new String(cache.data), clazz);
        }

        return null;
    }
}
