package com.sophimp.are.render;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.sophimp.are.inner.Html;

import java.util.Observer;

public class DRImageGetter implements Html.ImageGetter {

    private Observer glideObserver;
    private Context mContext;

    private TextView mTextView;

    public static RequestManager glideRequest;
    private int sWidth;
    private int sHeight;

    public DRImageGetter(Context context, TextView textView, Observer glideObserver) {
        mContext = context;
        mTextView = textView;
        glideRequest = Glide.with(mContext);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float density = displayMetrics.density;
        sWidth = displayMetrics.widthPixels - Math.round(density * 32);
        sHeight = displayMetrics.heightPixels;
//        sWidth = Util.getScreenWidthAndHeight(textView.getContext())[0];
//        sHeight = Util.getScreenWidthAndHeight(textView.getContext())[1];
        this.glideObserver = glideObserver;
    }

    @Override
    public GlideResDrawable getDrawable(String source, String width, String height, boolean isVideoImg) {

        int w = 0, h = 0;
        if (isVideoImg) {
            // 默认按16:9展示
            w = sWidth;
            h = sWidth * 9 / 16;
        } else {
            if (width != null && TextUtils.isDigitsOnly(width))
                w = Integer.parseInt(width);
            if (height != null && TextUtils.isDigitsOnly(height))
                h = Integer.parseInt(height);
            if (w > 0 && h > 0) {
                // 兼容以前的图片尺寸，再作一次等比适配
                if (w > sWidth) {
                    float ratio = (float) h / (float) w;
//                    float density = mContext.getResources().getDisplayMetrics().density;
                    w = sWidth;
                    h = (int) (sWidth * ratio + 0.5f);
                }
            } else {
                // 默认按16:9展示
                w = sWidth;
                h = sWidth * 9 / 16;
            }
        }
        GlideResDrawable resDrawable = new GlideResDrawable(mContext, w, h, sWidth, sHeight, isVideoImg, glideObserver);
        resDrawable = glideRequest.asBitmap().load(source).override(resDrawable.w, resDrawable.h).encodeQuality(10).into(resDrawable);
        return resDrawable;
    }
}
