package com.sophimp.are.style

import com.sophimp.are.RichEditText
import com.sophimp.are.spans.ISpan
import com.sophimp.are.spans.ListBulletSpan
import com.sophimp.are.spans.ListNumberSpan
import com.sophimp.are.spans.TodoSpan

/**
 * @author: sfx
 * @since: 2021/7/22
 */
class ListBulletStyle(editText: RichEditText) :
    BaseListStyle<ListBulletSpan, ListNumberSpan, TodoSpan>(
        editText,
        ListBulletSpan::class.java,
        ListNumberSpan::class.java,
        TodoSpan::class.java
    ) {
    override fun newSpan(): ISpan? {
        return ListBulletSpan()
    }

    override fun targetClass(): Class<ListBulletSpan> {
        return ListBulletSpan::class.java
    }
}
