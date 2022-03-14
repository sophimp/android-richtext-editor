package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.listener.OnSelectionChangeListener
import com.sophimp.are.style.UnderlineStyle
import com.sophimp.are.toolbar.IToolbarItemClickAction

class UnderlineToolItem(style: UnderlineStyle, itemClickAction: IToolbarItemClickAction? = null) : AbstractItem(style, itemClickAction) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_underline_unchecked

    init {
        style.mEditText.registerOnSelectionChangedListener(object : OnSelectionChangeListener {
            override fun onSelectionChanged(selectionStart: Int, selectionEnd: Int) {
                style.onSelectionChanged(selectionEnd)
                iconView.setIconResId(if (style.isChecked) R.mipmap.icon_toolitem_underline_checked else R.mipmap.icon_toolitem_underline_unchecked)
            }
        })
    }

    override fun iconClickHandle() {
        super.iconClickHandle()
        iconView.setIconResId(if (style.isChecked) R.mipmap.icon_toolitem_underline_checked else R.mipmap.icon_toolitem_underline_unchecked)
    }
}