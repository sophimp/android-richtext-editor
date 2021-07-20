package com.sophimp.are.spans

import android.text.style.LeadingMarginSpan

interface IListSpan : LeadingMarginSpan {
    companion object {
        /**
         * 符号绘制区域
         */
        const val LEADING_MARGIN = 90

        /**
         * 文字与符号间的距离
         */
        const val STANDARD_GAP_WIDTH = 20
    }
}