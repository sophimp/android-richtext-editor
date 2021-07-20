package com.sophimp.are.render;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.sophimp.are.R;
import com.sophimp.are.Util;

import java.util.Observer;

/**
 * 支持Glide 异步加载，传入observer观察数据变化
 */
public class GlideResDrawable extends CustomTarget<Bitmap> {
    private Context ctx;
    private boolean isVideoDrawable;
    private Resources res;
    public DRUrlDrawable drawable;
    public int w;
    public int h;

    public boolean hasReady;

    private int maxWidth;
    private int maxHeight;
    Observer observer;

    public GlideResDrawable(Context ctx, int width, int height, int maxWidth, int maxHeight, boolean isVideoImg, Observer observer) {
        super(maxWidth, maxHeight);
        this.ctx = ctx;
        res = ctx.getResources();
        w = width;
        h = height;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;

        drawable = new DRUrlDrawable(ctx, this.w, this.h);
        this.isVideoDrawable = isVideoImg;
        this.observer = observer;
    }

    @Override
    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
        hasReady = true;

        if (isVideoDrawable) {
            Bitmap playIcon = BitmapFactory.decodeResource(res, R.mipmap.icon_list_video_play);
            Bitmap videoCompose = Util.INSTANCE.mergeBitMapWithLimit(resource, playIcon, w, h);
            BitmapDrawable vd = new BitmapDrawable(res, videoCompose);
            vd.setBounds(new Rect(0, 0, w, h));
            drawable.setDrawable(vd);
            if (observer != null) {
                observer.update(null, drawable);
            }

            return;
        }

        int bw = resource.getWidth();
        int bh = resource.getHeight();
        Bitmap compressBitmap = null;
        if (bw > maxWidth * 1.75) {
            int newWidth = maxWidth, newHeight = (int) ((float) bh / (float) bw * newWidth + 0.5f);
            compressBitmap = Util.INSTANCE.compressByScale(resource, newWidth, newHeight, false);
        }
        if (bw > maxWidth) {
            w = maxWidth;
            h = (int) ((float) bh / (float) bw * w + 0.5f);
        } else {
            w = bw;
            h = bh;
        }

        Rect rect = new Rect(0, 0, w, h);
        drawable.w = w;
        drawable.h = h;
        drawable.defaultDrawable.setBounds(rect);
        drawable.invalidateSelf();
        drawable.defaultDrawable.invalidateSelf();
        BitmapDrawable sd = new BitmapDrawable(res, compressBitmap == null ? resource : compressBitmap);
        sd.setBounds(rect);
        drawable.setDrawable(sd);
        if (observer != null) {
            observer.update(null, drawable);
        }

    }

    @Override
    public void onLoadCleared(@Nullable Drawable placeholder) {

    }
}
