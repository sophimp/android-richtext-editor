package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.spans.TodoSpan
import com.sophimp.are.style.TodoStyle

class TodoToolItem(style: TodoStyle) :
    AbstractItem<TodoSpan>(style) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_todo

}