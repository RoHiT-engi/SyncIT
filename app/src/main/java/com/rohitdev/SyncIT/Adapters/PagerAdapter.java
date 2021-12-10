package com.rohitdev.SyncIT.Adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;


import com.rohitdev.SyncIT.R;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PagerAdapter extends androidx.viewpager.widget.PagerAdapter {
    // Context object
    Context context;

    // Array of images
    List<File> images ;

    // Layout Inflater
    LayoutInflater mLayoutInflater;

     public PagerAdapter(Context context, File[] images){
        this.context = context;
        this.images = Arrays.asList(images);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.pager_layout,container,false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.pager_Image);
        imageView.setImageBitmap(BitmapFactory.decodeFile(images.get(position).getAbsolutePath()));
        Objects.requireNonNull(container).addView(itemView);
        return itemView;
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((ConstraintLayout) object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);
    }


}
