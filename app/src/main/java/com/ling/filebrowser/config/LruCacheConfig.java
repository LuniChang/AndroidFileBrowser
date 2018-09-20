package com.ling.filebrowser.config;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by Administrator on 2017/3/9.
 */

public class LruCacheConfig {

    //    private final static int cacheSize = 3 * 1024 * 1024; // 3MiB
    private static LruCache<String, Bitmap> instance = null;

    public static int getMemoryCacheSize(Context context) {
        // Get memory class of this device, exceeding this amount will throw an
        // OutOfMemory exception.
        final int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();

        // Use 1/8th of the available memory for this memory cache.
        return 1024 * 1024 * memClass / 8;
    }

    public static synchronized LruCache<String, Bitmap> getInstance(Context context) {
        if (instance == null) {
            instance = new LruCache<String, Bitmap>(getMemoryCacheSize(context)) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getByteCount();
                }

                @Override
                protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                    super.entryRemoved(evicted, key, oldValue, newValue);
                    oldValue.recycle();
                }
            };
        }

        return instance;
    }

}
