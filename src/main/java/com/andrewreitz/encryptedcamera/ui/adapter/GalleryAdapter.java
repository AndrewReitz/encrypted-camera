package com.andrewreitz.encryptedcamera.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class GalleryAdapter extends BindableAdapter<File> {
    private List<File> images = Collections.emptyList();

    private final Context context;

    public GalleryAdapter(@NotNull Context context, List<File> images) {
        super(context);
        this.context = context;
        this.images = images;
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
        return new ImageView(context);
    }

    @Override public void bindView(File item, int position, View view) {
        Bitmap bitmap = BitmapFactory.decodeFile(item.getAbsolutePath());
        ((ImageView) view).setImageBitmap(bitmap);
    }
}
