package com.sophimp.are.spans

import android.text.style.AbsoluteSizeSpan

/**
 *
 * @author: sfx
 * @since: 2021/7/20
 */
class FontSizeSpan(size: Int) : AbsoluteSizeSpan(size, true), IDynamicSpan {
    override var dynamicFeature: Int
        get() = this.size
        set(dynamicFeature) {
            this.dynamicFeature = dynamicFeature
        }

}