package com.sophimp.are.toolbar.items

import android.widget.ImageView
import com.sophimp.are.style.IStyle

/**
 *
 * @author: sfx
 * @since: 2021/7/20
 */
interface IToolbarItem {

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
    val mStyle: IStyle

}