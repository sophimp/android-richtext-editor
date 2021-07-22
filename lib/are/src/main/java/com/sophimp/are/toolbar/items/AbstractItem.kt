package com.sophimp.are.toolbar.items

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import com.sophimp.are.Util.getPixelByDp
import com.sophimp.are.Util.log
import com.sophimp.are.spans.ISpan
import com.sophimp.are.style.IStyle

/**
 * @author: sfx
 * @since: 2021/7/21
 */
abstract class AbstractItem<T : ISpan>(protected var style: IStyle<T>) : IToolbarItem<T> {
    val context: Context = style.mEditText.context.applicationContext
    val imageView: ImageView
    val mEditText = style.mEditText

    init {
        imageView = ImageView(context)
        val size = getPixelByDp(context, 40)
        val pading = getPixelByDp(context, 8)
        val params = LinearLayout.LayoutParams(size, size)
        imageView.layoutParams = params
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        imageView.setPadding(pading, pading, pading, pading)
        imageView.bringToFront()
        imageView.setOnClickListener { iconClickHandle() }
    }

    protected fun <T> printSpans(clazz: Class<T>) {
        val editable = mStyle.mEditText.editableText
        val spans = editable.getSpans(0, editable.length, clazz)
        for (span in spans) {
            val start = editable.getSpanStart(span)
            val end = editable.getSpanEnd(span)
            log("Span -- $clazz, start = $start, end == $end")
        }
    }

    override val iconView: ImageView
        get() = imageView

    override val mStyle: IStyle<T>
        get() = style

}