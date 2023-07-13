package com.sophimp.are.spans

import android.graphics.Color
import android.text.style.BackgroundColorSpan

/**
 * @author: sfx
 * @since: 2021/7/20
 */
class FontBackgroundColorSpan(var colorStr: String) : BackgroundColorSpan(Color.TRANSPARENT), IDynamicSpan {
    var mColor = Color.parseColor(colorStr)
    override val dynamicFeature: String
        get() = colorStr

}