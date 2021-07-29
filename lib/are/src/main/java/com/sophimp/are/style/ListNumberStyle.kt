package com.sophimp.are.style

import com.sophimp.are.RichEditText
import com.sophimp.are.spans.ISpan
import com.sophimp.are.spans.ListNumberSpan

/**
 *
 * @author: sfx
 * @since: 2021/7/22
 */
class ListNumberStyle(editText: RichEditText) : BaseListStyle<ListNumberSpan>(editText) {
    override fun newSpan(inheritSpan: ISpan?): ISpan? {
        return ListNumberSpan()
    }

    override fun targetClass(): Class<ListNumberSpan> {
        return ListNumberSpan::class.java
    }
}