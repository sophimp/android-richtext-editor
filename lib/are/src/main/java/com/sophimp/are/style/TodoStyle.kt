package com.sophimp.are.style

import com.sophimp.are.R
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.ISpan
import com.sophimp.are.spans.TodoSpan

class TodoStyle(editText: RichEditText) : BaseListStyle<TodoSpan>(editText) {
    override fun setSpan(span: ISpan, start: Int, end: Int) {
        (span as TodoSpan).drawable =
            context.resources.getDrawable(R.mipmap.icon_checkbox_unchecked)
        super.setSpan(span, start, end)
    }

    override fun newSpan(inheritSpan: ISpan?): ISpan? {
        return TodoSpan()
    }

    override fun targetClass(): Class<TodoSpan> {
        return TodoSpan::class.java
    }
}
