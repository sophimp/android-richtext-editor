package com.sophimp.are.spans

import android.content.Context
import android.graphics.Bitmap
import android.text.style.ImageSpan

/**
 * @author: sfx
 * @since: 2021/7/20
 */
class TableSpan(
    context: Context,
    var htmlStr: String,
    drawable: Bitmap
) : ImageSpan(context, drawable), IClickableSpan, ISpan