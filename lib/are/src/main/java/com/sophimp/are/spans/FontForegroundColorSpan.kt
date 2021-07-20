package com.sophimp.are.spans

import android.text.style.ForegroundColorSpan
import androidx.annotation.ColorInt

/**
 *
 * @author: sfx
 * @since: 2021/7/20
 */
class FontForegroundColorSpan(@ColorInt color: Int) : ForegroundColorSpan(color), IDynamicSpan {
    override var dynamicFeature: Int
        get() = foregroundColor
        set(dynamicFeature) {}
}