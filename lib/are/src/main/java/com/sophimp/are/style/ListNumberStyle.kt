package com.sophimp.are.style

import com.sophimp.are.RichEditText
import com.sophimp.are.spans.ISpan
import com.sophimp.are.spans.ListBulletSpan
import com.sophimp.are.spans.ListNumberSpan
import com.sophimp.are.spans.TodoSpan

/**
 *
 * @author: sfx
 * @since: 2021/7/22
 */
class ListNumberStyle(editText: RichEditText) :
    BaseListStyle<ListNumberSpan, ListBulletSpan, TodoSpan>(
        editText,
        ListNumberSpan::class.java,
        ListBulletSpan::class.java,
        TodoSpan::class.java
    ) {
    override fun newSpan(): ISpan? {
        return ListNumberSpan()
    }

    override fun targetClass(): Class<ListNumberSpan> {
        return ListNumberSpan::class.java
    }
}