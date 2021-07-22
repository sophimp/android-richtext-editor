package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.spans.QuoteSpan2
import com.sophimp.are.style.QuoteStyle

class QuoteToolItem(style: QuoteStyle) :
    AbstractItem<QuoteSpan2>(style) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_quote

}