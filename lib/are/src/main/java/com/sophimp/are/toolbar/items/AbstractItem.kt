package com.sophimp.are.toolbar.items

import android.content.Context
import androidx.core.content.ContextCompat
import com.sophimp.are.style.IStyle
import com.sophimp.are.toolbar.IToolbarItemClickAction
import com.sophimp.are.toolbar.ItemView
import com.sophimp.are.utils.Util.log

/**
 * @author: sfx
 * @since: 2021/7/21
 */
abstract class AbstractItem(protected var style: IStyle, protected var itemClickAction: IToolbarItemClickAction?) : IToolbarItem {
    val context: Context = style.mEditText.context
    val itemView: ItemView
    val mEditText = style.mEditText

    init {
        itemView = ItemView(context)
        ContextCompat.getDrawable(context, srcResId)?.let { itemView.setIconImage(it) }
        itemView.setOnClickListener { iconClickHandle() }
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

    override val iconView: ItemView
        get() = itemView

    override val mStyle: IStyle
        get() = style

    /**
     * icon click handle, some style should handle both in toolbar item and style
     */
    open fun iconClickHandle() {
        mStyle.toolItemIconClick()
        itemClickAction?.onItemClick(this)
    }
}