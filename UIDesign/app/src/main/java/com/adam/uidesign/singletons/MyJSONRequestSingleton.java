package com.adam.uidesign.singletons;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Adam on 3/12/2016.
 */
public class MyJSONRequestSingleton {
    private static MyJSONRequestSingleton mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    private MyJSONRequestSingleton(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized MyJSONRequestSingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MyJSONRequestSingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request){
        getRequestQueue().add(request);
    }
}
