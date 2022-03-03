package com.sophimp.are.spans

import android.text.style.AbsoluteSizeSpan

/**
 *
 * wrap in future, now just to know there is a FontSizeSpan
 * @author: sfx
 * @since: 2021/7/20
 */
class FontSizeSpan(size: Int) : AbsoluteSizeSpan(size, true), IDynamicSpan {
    private var mFontSize = size
    override val dynamicFeature: String
        get() = "$mFontSize"

}