package com.sophimp.are.spans

import android.graphics.Color
import android.text.TextPaint
import android.text.style.CharacterStyle

/**
 * 搜索高亮
 * @author: sfx
 * @since: 2021/7/20
 */
class SearchHighlightSpan(var colorStr: String = "#802899FB") : CharacterStyle(), IDynamicSpan {
    var mColor = Color.parseColor(colorStr)
    override val dynamicFeature: String
        get() = colorStr

    override fun updateDrawState(tp: TextPaint?) {
    }

}