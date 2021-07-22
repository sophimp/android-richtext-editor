package com.sophimp.are.style

import com.sophimp.are.RichEditText
import com.sophimp.are.spans.ListBulletSpan
import com.sophimp.are.spans.ListNumberSpan
import com.sophimp.are.spans.TodoSpan

/**
 *
 * @author: sfx
 * @since: 2021/7/22
 */
class ListNumberStyle(editText: RichEditText) :
    IListStyle<ListNumberSpan, ListBulletSpan, TodoSpan>(
        editText,
        ListNumberSpan::class.java,
        ListBulletSpan::class.java,
        TodoSpan::class.java
    ) 