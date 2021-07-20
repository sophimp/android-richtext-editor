package com.sophimp.are.spans

import android.text.style.BackgroundColorSpan

/**
 *
 * @author: sfx
 * @since: 2021/7/20
 */
class FontBackgroundColorSpan(color: Int) : BackgroundColorSpan(color), IDynamicSpan {
    override var dynamicFeature: Int
        get() = this.backgroundColor
        set(dynamicFeature) {}
}