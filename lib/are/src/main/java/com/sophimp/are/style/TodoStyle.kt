package com.sophimp.are.style

import com.sophimp.are.R
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.ISpan
import com.sophimp.are.spans.ListBulletSpan
import com.sophimp.are.spans.ListNumberSpan
import com.sophimp.are.spans.TodoSpan

class TodoStyle(editText: RichEditText) :
    IListStyle<TodoSpan, ListNumberSpan, ListBulletSpan>(
        editText,
        TodoSpan::class.java,
        ListNumberSpan::class.java,
        ListBulletSpan::class.java
    ) {
    override fun setSpan(span: ISpan, start: Int, end: Int) {
        (span as TodoSpan).drawable =
            context.resources.getDrawable(R.mipmap.icon_checkbox_unchecked)
        super.setSpan(span, start, end)
    }
}
