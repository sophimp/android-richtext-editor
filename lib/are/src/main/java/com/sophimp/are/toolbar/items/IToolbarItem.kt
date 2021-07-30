package com.sophimp.are.toolbar.items

import com.sophimp.are.style.IStyle
import com.sophimp.are.toolbar.ItemView

/**
 *
 * @author: sfx
 * @since: 2021/7/20
 */
interface IToolbarItem {

    /**
     * item icon ImageView
     */
    val iconView: ItemView

    /**
     * item icon resId
     */
    val srcResId: Int

    /**
     * item style handle
     */
    val mStyle: IStyle

}