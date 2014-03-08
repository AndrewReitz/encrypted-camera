package com.andrewreitz.encryptedcamera.ui.adapter;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.andrewreitz.encryptedcamera.R;
import com.andrewreitz.encryptedcamera.cache.ThumbnailCache;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import timber.log.Timber;

public class GalleryAdapter extends BindableAdapter<File> {
    private List<File> images = Collections.emptyList();
    private final int viewSize;
    private final LruCache<String, Bitmap> cache;

    public GalleryAdapter(@NotNull Context context, @NotNull List<File> images) {
        super(context);
        this.viewSize = context.getResources().getDimensionPixelSize(R.dimen.gridview_image);
        this.images = images;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int memoryClassBytes = am.getMemoryClass() * 1024 * 1024;
        this.cache = new ThumbnailCache(memoryClassBytes);
    }

    @Override public int getCount() {
        return images.size();
    }

    @Override public File getItem(int position) {
        return images.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        return inflater.inflate(R.layout.gallery_item, null);
    }

    @Override public void bindView(File file, int position, View view) {
        ImageView imageView = ButterKnife.findById(view, R.id.gallery_imageview);
        // Cancel any pending thumbnail task, since this view is now bound
        // to new thumbnail
        final ThumbnailAsyncTask oldTask = (ThumbnailAsyncTask) view.getTag();
        if (oldTask != null) {
            oldTask.cancel(false);
        }

        // Cache enabled, try looking for cache hit
        final Bitmap cachedResult = cache.get(file.getName());
        if (cachedResult != null) {
            imageView.setImageBitmap(cachedResult);
            return;
        }

        // If we arrived here, either cache is disabled or cache miss, so we
        // need to kick task to load manually
        final ThumbnailAsyncTask task = new ThumbnailAsyncTask(imageView);
        imageView.setImageBitmap(null);
        imageView.setTag(task);
        task.execute(file);
    }

    // TODO PULL THE CACHE OUT
    public void clearCache() {
        cache.evictAll();
    }

    public void trimCache(int size) {
        cache.trimToSize(size);
    }

    public int getCacheSize() {
        return cache.size();
    }

    private class ThumbnailAsyncTask extends AsyncTask<File, Void, Bitmap> {
        private final ImageView target;

        public ThumbnailAsyncTask(ImageView target) {
            this.target = target;
        }

        @Override
        protected void onPreExecute() {
            target.setTag(this);
        }

        @Override
        protected Bitmap doInBackground(File... params) {
            Bitmap result = null;
            File file = params[0];
            try {
                FileInputStream fis = new FileInputStream(file.getAbsolutePath());
                Bitmap imageBitmap = BitmapFactory.decodeStream(fis);

                imageBitmap = Bitmap.createScaledBitmap(imageBitmap, viewSize, viewSize, false);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] imageData = baos.toByteArray();
                result = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                //((ImageView) view).setImageBitmap(bitmap);
                //cache.put(item.getName(), bitmap);
            } catch (Exception e) {
                Timber.e(e, "Error resizing image");
            }

            cache.put(file.getName(), result);
            return result;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (target.getTag() == this) {
                target.setImageBitmap(result);
                target.setTag(null);
            }
        }
    }
}
