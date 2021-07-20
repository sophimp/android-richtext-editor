package com.sophimp.are.spans

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.drawable.Drawable
import android.text.style.DynamicDrawableSpan
import java.lang.ref.WeakReference

class EmojiSpan(private val mContext: Context, private val mIconId: Int, size: Int) :
    DynamicDrawableSpan(), ISpan {
    private var mDrawable: Drawable? = null
    private var mSize = 50 // Should not be hard-coded
    override fun getDrawable(): Drawable {
        if (null == mDrawable) {
            mDrawable = mContext.resources.getDrawable(mIconId)
            mDrawable?.setBounds(0, 0, mSize, mSize)
        }
        return mDrawable!!
    }

    override fun getSize(
        paint: Paint, text: CharSequence, start: Int,
        end: Int, fontMetrics: FontMetricsInt?
    ): Int {
        if (fontMetrics != null) {
            val paintFontMetrics = paint.fontMetrics
            fontMetrics.top = paintFontMetrics.top.toInt()
            fontMetrics.bottom = paintFontMetrics.bottom.toInt()
        }
        return mSize
    }

    override fun draw(
        canvas: Canvas, text: CharSequence, start: Int,
        end: Int, x: Float, top: Int, y: Int,
        bottom: Int, paint: Paint
    ) {
        val drawable = drawable
        val paintFontMetrics = paint.fontMetrics
        val fontHeight = paintFontMetrics.descent - paintFontMetrics.ascent
        val centerY = y + paintFontMetrics.descent - fontHeight / 2
        val transitionY = centerY - mSize / 2
        canvas.save()
        canvas.translate(x, transitionY)
        drawable.draw(canvas)
        canvas.restore()
    }

    private val cachedDrawable: Drawable?
        private get() {
            val wr = mDrawableRef
            var d: Drawable? = null
            if (wr != null) d = wr.get()
            if (d == null) {
                d = drawable
                mDrawableRef = WeakReference(d)
            }
            return d
        }

    override val html: String
        get() = "<emoji src=\"$mIconId\" />"

    private var mDrawableRef: WeakReference<Drawable?>? = null

    init {
        mSize = size
    }
}