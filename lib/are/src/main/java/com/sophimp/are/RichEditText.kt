package com.sophimp.are

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

/**
 * rich text editor
 * @author: sfx
 * @since: 2021/7/20
 */
class RichEditText(context: Context, attr: AttributeSet) : AppCompatEditText(context, attr) {
    var isChange: Boolean = false

    init {

    }
}