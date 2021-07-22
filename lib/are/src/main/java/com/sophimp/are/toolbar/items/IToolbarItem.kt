package com.sophimp.are.toolbar.items

import android.widget.ImageView
import com.sophimp.are.spans.ISpan
import com.sophimp.are.style.IStyle

/**
 *
 * @author: sfx
 * @since: 2021/7/20
 */
interface IToolbarItem<T : ISpan> {

    /**
     * item icon ImageView
     */
    val iconView: ImageView

    /**
     * item icon resId
     */
    val srcResId: Int

    /**
     * item style handle
     */
    val mStyle: IStyle<T>

    /**
     * icon click handle, some style should handle both in toolbar item and style
     */
    fun iconClickHandle() {
        mStyle.toolItemIconClick()
    }
}