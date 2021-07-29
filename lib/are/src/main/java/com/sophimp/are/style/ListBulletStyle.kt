package com.sophimp.are.style

import com.sophimp.are.RichEditText
import com.sophimp.are.spans.ISpan
import com.sophimp.are.spans.ListBulletSpan

/**
 * @author: sfx
 * @since: 2021/7/22
 */
class ListBulletStyle(editText: RichEditText) :
    BaseListStyle<ListBulletSpan>(editText) {
    override fun newSpan(inheritSpan: ISpan?): ISpan? {
        return ListBulletSpan()
    }

    override fun targetClass(): Class<ListBulletSpan> {
        return ListBulletSpan::class.java
    }
}
