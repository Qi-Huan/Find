package com.qihuan.commonmodule.imageloader.strategy;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.qihuan.commonmodule.imageloader.GlideApp;
import com.qihuan.commonmodule.imageloader.ImageLoader;
import com.qihuan.commonmodule.imageloader.LoaderStrategy;

/**
 * GlideStrategy
 *
 * @author Qi
 * @date 2017/9/18
 */

public class GlideStrategy implements LoaderStrategy {

    private Context context;

    @Override
    public void load(Context context, String url, ImageView target, int placeHolder, int option, ImageLoader.OnImageLoadListener loadListener) {
        this.context = context;

        if (loadListener != null) {
            loadListener.onStart();
        }

        RequestOptions requestOptions = new RequestOptions();
        switch (option) {
            case ImageLoader.CENTER_CROP:
                requestOptions.centerCrop();
                break;
            case ImageLoader.FIT_CENTER:
                requestOptions.fitCenter();
                break;
            default:
                requestOptions.centerCrop();
                break;
        }

        GlideApp.with(context)
                .load(url)
                .load(url)
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if (loadListener != null) {
                            loadListener.onFinish(false);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (loadListener != null) {
                            loadListener.onFinish(true);
                        }
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(target);
    }

    @Override
    public void clearDiskCache() {
        GlideApp.get(context).clearDiskCache();
    }

    @Override
    public void clearMemory() {
        GlideApp.get(context).clearMemory();
    }

    @Override
    public void trimMemory(int level) {
        if (context == null) {
            return;
        }
        GlideApp.get(context).trimMemory(level);
    }

    @Override
    public void clearAllMemoryCaches() {
        if (context == null) {
            return;
        }
        GlideApp.get(context).onLowMemory();
    }
}