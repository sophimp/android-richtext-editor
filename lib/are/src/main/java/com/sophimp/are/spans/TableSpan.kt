package com.sophimp.are.spans

import android.graphics.drawable.Drawable
import android.text.style.ImageSpan

/**
 * @author: sfx
 * @since: 2021/7/20
 */
class TableSpan(
    var htmlStr: String,
    drawable: Drawable
) : ImageSpan(drawable), IClickableSpan, ISpan