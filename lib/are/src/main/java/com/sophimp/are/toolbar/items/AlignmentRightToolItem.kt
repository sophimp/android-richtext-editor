package com.sophimp.are.toolbar.items

import com.sophimp.are.R
import com.sophimp.are.spans.AlignmentRightSpan
import com.sophimp.are.style.AlignmentStyle

class AlignmentRightToolItem(style: AlignmentStyle<AlignmentRightSpan>) :
    AbstractItem<AlignmentRightSpan>(style) {
    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_align_right

}