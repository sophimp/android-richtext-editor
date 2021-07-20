package com.sophimp.are.spans

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.text.style.ReplacementSpan
import com.sophimp.are.models.AtItem

/**
 *
 * @author: sfx
 * @since: 2021/7/20
 */
class AtSpan : ReplacementSpan, ISpan, IClickableSpan {
    /**
     * Will be used when generating HTML code for @
     */
    var userKey: String
        private set
    var userName: String
        private set
    private var mColor: Int

    constructor(atItem: AtItem) {
        userKey = atItem.mKey
        userName = atItem.mName
        mColor = atItem.mColor
    }

    constructor(mUserKey: String, mUserName: String, mColor: Int) {
        userKey = mUserKey
        userName = mUserName
        this.mColor = mColor
    }

    override fun getSize(
        paint: Paint, text: CharSequence, start: Int, end: Int,
        fm: FontMetricsInt?
    ): Int {
        return paint.measureText(text, start, end).toInt()
    }

    override fun draw(
        canvas: Canvas, text: CharSequence, start: Int, end: Int,
        x: Float, top: Int, y: Int, bottom: Int, paint: Paint
    ) {
        paint.color = Color.TRANSPARENT
        val width = paint.measureText(text.toString(), start, end)
        canvas.drawRect(x, top.toFloat(), x + width, bottom.toFloat(), paint)
        paint.color = -0x1000000 or mColor
        canvas.drawText(text, start, end, x, y.toFloat(), paint)
    }

    override val html: String?
        get() {
            val html = StringBuffer()
            html.append("<a")
            html.append(" href=\"#\"")
            html.append(" uKey=\"$userKey\"")
            html.append(" uName=\"$userName\"")
            html.append(String.format(" style=\"color:#%06X;\"", 0xFFFFFF and mColor))
            html.append(">")
            html.append("@$userName")
            html.append("</a>")
            return html.toString()
        }

    /**
     * 上层要实现监听事件，重写此方法
     * @param key id 或链接
     * @param name 名称
     */
    fun onClick(key: String?, name: String?) {
        // 交由上层去实现
    }

    companion object {
        private const val KEY_ATTR = "key"
    }
}