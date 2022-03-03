package com.sophimp.are.spans

import android.graphics.Color
import android.text.style.ForegroundColorSpan

/**
 *
 * @author: sfx
 * @since: 2021/7/20
 */
class FontForegroundColorSpan(var colorString: String) : ForegroundColorSpan(Color.parseColor(colorString)), IDynamicSpan {
    private var mColor = Color.parseColor(colorString)
    override val dynamicFeature: String
        get() = colorString

}