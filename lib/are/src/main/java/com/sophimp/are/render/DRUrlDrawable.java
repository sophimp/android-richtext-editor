package com.sophimp.are.render;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.sophimp.are.R;

/**
 *
 */
public class DRUrlDrawable extends BitmapDrawable {
    protected Drawable defaultDrawable;

    public Drawable mDrawable;
    public int w;
    public int h;

    private Context mContext;

    @SuppressWarnings("deprecation")
    public DRUrlDrawable(Context context, int width, int height) {
        this.mContext = context;
        w = width;
        h = height;
        defaultDrawable = context.getResources().getDrawable(R.mipmap.default_image);
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeResource(this.mContext.getResources(), R.drawable.ic_launcher, options);
//        this.w = options.outWidth;
//        this.h = options.outHeight;
        defaultDrawable.setBounds(0, 0, w, h);

        Rect rect = new Rect(0, 0, w, h);
        this.setBounds(rect);
    }

    @Override
    public void draw(Canvas canvas) {
        Drawable drawable = mDrawable == null ? defaultDrawable : mDrawable;
        boolean isLoading = mDrawable == null;
        if (drawable != null) {
            canvas.save();
            drawable.draw(canvas);
            canvas.restore();

            if (isLoading) {
                Paint p = new Paint();
                p.setColor(Color.WHITE);
                p.setTextSize(30);
                String loading = "Loading... 0%";
                Rect bounds = new Rect();
                p.getTextBounds(loading, 0, loading.length(), bounds);

                float loadingWidth = bounds.width();
                float x = 0.0f;
                if (loadingWidth < this.w) {
                    x = (this.w - loadingWidth) / 2;
                }

                float loadingHeight = bounds.height();
                float y = 0.0f;
                if (loadingHeight < this.h) {
                    y = this.h / 2;
                }
                canvas.drawText(loading, 0, loading.length(), x, y, p);
            }
        }
    }

    public void setDrawable(Drawable drawable) {
        mDrawable = drawable;
    }
}
