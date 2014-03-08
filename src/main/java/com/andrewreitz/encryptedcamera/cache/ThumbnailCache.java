package com.andrewreitz.encryptedcamera.cache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class ThumbnailCache extends LruCache<String, Bitmap> {
    public ThumbnailCache(int maxSizeBytes) {
        super(maxSizeBytes);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        // Return size of in-memory Bitmap, counted against maxSizeBytes
        return value.getByteCount();
    }
}
