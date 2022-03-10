package com.sophimp.are.style

import com.sophimp.are.RichEditText
import com.sophimp.are.spans.ISpan
import com.sophimp.are.spans.TodoSpan

class TodoStyle(editText: RichEditText) : BaseListStyle<TodoSpan>(editText) {

    override fun newSpan(inheritSpan: ISpan?): ISpan? {
        if (inheritSpan is TodoSpan) {
            return TodoSpan(inheritSpan.isCheck)
        }
        return TodoSpan(false)
    }

    override fun targetClass(): Class<TodoSpan> {
        return TodoSpan::class.java
    }
}
