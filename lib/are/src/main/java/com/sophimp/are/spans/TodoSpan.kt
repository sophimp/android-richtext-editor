package com.sophimp.are.spans

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.text.Layout
import android.text.Spannable
import android.text.Spanned
import android.text.style.AlignmentSpan
import com.sophimp.are.R
import com.sophimp.are.RichEditText
import kotlin.math.max

class TodoSpan : IClickableSpan, IListSpan {
    var isCheck = false
    lateinit var drawable: Drawable
    var drawableRectf = RectF()

    /**
     * delta to adjust for icon in center of line height
     */
    private val delta = 6

    private var drawableSize = 35

    constructor(ctx: Context, isCheck: Boolean) {
        this.isCheck = isCheck
        drawable = if (isCheck) {
            ctx.resources.getDrawable(R.mipmap.icon_checkbox_checked)
        } else {
            ctx.resources.getDrawable(R.mipmap.icon_checkbox_unchecked)
        }
    }

    override fun getLeadingMargin(first: Boolean): Int {
        return IListSpan.LEADING_MARGIN
    }

    override fun drawLeadingMargin(
        c: Canvas, p: Paint, x: Int, dir: Int, top: Int,
        baseline: Int, bottom: Int, text: CharSequence, start: Int, end: Int,
        first: Boolean, layout: Layout
    ) {
        if ((text as Spanned).getSpanStart(this) == start) {

            drawableSize = max(baseline - top, drawable.intrinsicHeight)
            val dh = drawableSize

            var itop = top + delta
            val alignmentSpans =
                text.getSpans(start, end, AlignmentSpan::class.java)
            if (null != alignmentSpans) {
                for (span in alignmentSpans) {
                    val v = p.measureText(text, start, end)
                    if (span.alignment == Layout.Alignment.ALIGN_CENTER) {
                        val rx = (layout.width - v - dh - IListSpan.STANDARD_GAP_WIDTH).toInt() / 2
                        drawable.setBounds(rx, itop, rx + dh, itop + dh)
                        drawableRectf.left = rx.toFloat()
                        drawableRectf.top = itop.toFloat()
                        drawableRectf.right = rx + dh.toFloat()
                        drawableRectf.bottom = itop + dh.toFloat()
                        drawable.draw(c)
                        return
                    } else if (alignmentSpans[0].alignment == Layout.Alignment.ALIGN_OPPOSITE) {
                        val rx = (layout.width - v - dh - IListSpan.STANDARD_GAP_WIDTH).toInt()
                        drawable.setBounds(rx, itop, rx + dh, itop + dh)
                        drawableRectf.left = rx.toFloat()
                        drawableRectf.top = itop.toFloat()
                        drawableRectf.right = rx + dh.toFloat()
                        drawableRectf.bottom = itop + dh.toFloat()
                        drawable.draw(c)
                        return
                    }
                }
            }
            var margin = 0
            val leadingMarginSpans = text.getSpans(start, end, IndentSpan::class.java)
            if (leadingMarginSpans != null && leadingMarginSpans.isNotEmpty()) {
                margin = leadingMarginSpans[0].getLeadingMargin(true)
                //二次绘制，ix已经偏移了，故不需要再重新加偏移量
                if (x == margin) {
                    margin = 0
                }
            }
            val ix =
                x + margin + IListSpan.LEADING_MARGIN - drawableSize - IListSpan.STANDARD_GAP_WIDTH
            drawable.setBounds(ix, itop, ix + dh, itop + dh)
            drawableRectf.left = ix.toFloat()
            drawableRectf.top = itop.toFloat()
            drawableRectf.right = ix + dh.toFloat()
            drawableRectf.bottom = itop + dh.toFloat()
            drawable.draw(c)
        }
    }

    fun onClick(editText: RichEditText, clickX: Float, beforeSelectionEnd: Int): Boolean {
        if (clickX >= drawableRectf.left && clickX <= drawableRectf.right) {
            try {
                isCheck = !isCheck
                val spanStart = editText.editableText.getSpanStart(this)
                val spanEnd = editText.editableText.getSpanEnd(this)
                editText.editableText.removeSpan(this)
                drawable = if (isCheck) {
                    editText.resources.getDrawable(R.mipmap.icon_checkbox_checked)
                } else {
                    editText.resources.getDrawable(R.mipmap.icon_checkbox_unchecked)
                }
                editText.editableText.setSpan(
                    this,
                    spanStart,
                    spanEnd,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                editText.post {
                    editText.setSelection(beforeSelectionEnd)
                }
                editText.isChange = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return true
        }
        return false
    }

    val attributeStr: String
        get() {
            return ""
        }
}