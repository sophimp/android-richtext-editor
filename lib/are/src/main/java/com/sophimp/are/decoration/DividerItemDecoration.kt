package com.sophimp.are.decoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView

abstract class DividerItemDecoration(var context: Context) : RecyclerView.ItemDecoration() {

    private var mPaint: Paint? = null

    init {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.style = Paint.Style.FILL
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        //left, top, right, bottom
        val childCount: Int = parent.getChildCount()
        for (i in 0 until childCount) {
            val child: View = parent.getChildAt(i)
            val itemPosition: Int = (child.layoutParams as RecyclerView.LayoutParams).getViewLayoutPosition()
            val divider: LineDivider = getDivider(itemPosition)
            divider.leftSideLine.apply {
                if (isHave) {
                    drawChildLeftVertical(child,
                        c,
                        parent,
                        color,
                        Dp2Px.convert(context, widthDp),
                        Dp2Px.convert(context, startPaddingDp),
                        Dp2Px.convert(context, endPaddingDp))
                }
            }
            divider.topSideLine.apply {
                if (isHave) {
                    drawChildTopHorizontal(child,
                        c,
                        parent,
                        color,
                        Dp2Px.convert(context, widthDp),
                        Dp2Px.convert(context, startPaddingDp),
                        Dp2Px.convert(context, endPaddingDp))
                }
            }
            divider.rightSideLine.apply {
                if (isHave) {
                    drawChildRightVertical(child,
                        c,
                        parent,
                        color,
                        Dp2Px.convert(context, widthDp),
                        Dp2Px.convert(context, startPaddingDp),
                        Dp2Px.convert(context, endPaddingDp))
                }
            }
            divider.bottomSideLine.apply {
                if (isHave) {
                    drawChildBottomHorizontal(child,
                        c,
                        parent,
                        color,
                        Dp2Px.convert(context, widthDp),
                        Dp2Px.convert(context, startPaddingDp),
                        Dp2Px.convert(context, endPaddingDp))
                }
            }
        }
    }

    private fun drawChildBottomHorizontal(
        child: View,
        c: Canvas,
        parent: RecyclerView,
        @ColorInt color: Int,
        lineWidthPx: Int,
        startPaddingPx: Int,
        endPaddingPx: Int
    ) {
        var leftPadding = 0
        var rightPadding = 0
        leftPadding = if (startPaddingPx <= 0) {
            //padding<0当作==0处理
            //上下左右默认分割线的两头都出头一个分割线的宽度，避免十字交叉的时候，交叉点是空白
            -lineWidthPx
        } else {
            startPaddingPx
        }
        rightPadding = if (endPaddingPx <= 0) {
            lineWidthPx
        } else {
            -endPaddingPx
        }
        val params: RecyclerView.LayoutParams = child
            .layoutParams as RecyclerView.LayoutParams
        val left: Int = child.left - params.leftMargin + leftPadding
        val right: Int = child.right + params.rightMargin + rightPadding
        val top: Int = child.bottom + params.bottomMargin
        val bottom = top + lineWidthPx
        mPaint!!.color = color
        c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint!!)
    }

    private fun drawChildTopHorizontal(
        child: View,
        c: Canvas,
        parent: RecyclerView,
        @ColorInt color: Int,
        lineWidthPx: Int,
        startPaddingPx: Int,
        endPaddingPx: Int
    ) {
        var leftPadding = 0
        var rightPadding = 0
        leftPadding = if (startPaddingPx <= 0) {
            //padding<0当作==0处理
            //上下左右默认分割线的两头都出头一个分割线的宽度，避免十字交叉的时候，交叉点是空白
            -lineWidthPx
        } else {
            startPaddingPx
        }
        rightPadding = if (endPaddingPx <= 0) {
            lineWidthPx
        } else {
            -endPaddingPx
        }
        val params: RecyclerView.LayoutParams = child
            .layoutParams as RecyclerView.LayoutParams
        val left: Int = child.left - params.leftMargin + leftPadding
        val right: Int = child.right + params.rightMargin + rightPadding
        val bottom: Int = child.top - params.topMargin
        val top = bottom - lineWidthPx
        mPaint!!.color = color
        c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint!!)
    }

    private fun drawChildLeftVertical(
        child: View,
        c: Canvas,
        parent: RecyclerView,
        @ColorInt color: Int,
        lineWidthPx: Int,
        startPaddingPx: Int,
        endPaddingPx: Int
    ) {
        var topPadding = 0
        var bottomPadding = 0
        topPadding = if (startPaddingPx <= 0) {
            //padding<0当作==0处理
            //上下左右默认分割线的两头都出头一个分割线的宽度，避免十字交叉的时候，交叉点是空白
            -lineWidthPx
        } else {
            startPaddingPx
        }
        bottomPadding = if (endPaddingPx <= 0) {
            lineWidthPx
        } else {
            -endPaddingPx
        }
        val params: RecyclerView.LayoutParams = child
            .layoutParams as RecyclerView.LayoutParams
        val top: Int = child.top - params.topMargin + topPadding
        val bottom: Int = child.bottom + params.bottomMargin + bottomPadding
        val right: Int = child.left - params.leftMargin
        val left = right - lineWidthPx
        mPaint!!.color = color
        c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint!!)
    }

    private fun drawChildRightVertical(
        child: View,
        c: Canvas,
        parent: RecyclerView,
        @ColorInt color: Int,
        lineWidthPx: Int,
        startPaddingPx: Int,
        endPaddingPx: Int
    ) {
        var topPadding = 0
        var bottomPadding = 0
        topPadding = if (startPaddingPx <= 0) {
            //padding<0当作==0处理
            //上下左右默认分割线的两头都出头一个分割线的宽度，避免十字交叉的时候，交叉点是空白
            -lineWidthPx
        } else {
            startPaddingPx
        }
        bottomPadding = if (endPaddingPx <= 0) {
            lineWidthPx
        } else {
            -endPaddingPx
        }
        val params: RecyclerView.LayoutParams = child
            .layoutParams as RecyclerView.LayoutParams
        val top: Int = child.top - params.topMargin + topPadding
        val bottom: Int = child.bottom + params.bottomMargin + bottomPadding
        val left: Int = child.right + params.rightMargin
        val right = left + lineWidthPx
        mPaint!!.color = color
        c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint!!)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        //outRect 看源码可知这里只是把Rect类型的outRect作为一个封装了left,right,top,bottom的数据结构,
        //作为传递left,right,top,bottom的偏移值来用的
        val itemPosition: Int = (view.layoutParams as RecyclerView.LayoutParams).getViewLayoutPosition()
        val divider: LineDivider = getDivider(itemPosition)
        val left = if (divider.leftSideLine.isHave) Dp2Px.convert(context, divider.leftSideLine.widthDp) else 0
        val top = if (divider.topSideLine.isHave) Dp2Px.convert(context, divider.topSideLine.widthDp) else 0
        val right = if (divider.rightSideLine.isHave) Dp2Px.convert(context, divider.rightSideLine.widthDp) else 0
        val bottom = if (divider.bottomSideLine.isHave) Dp2Px.convert(context, divider.bottomSideLine.widthDp) else 0
        outRect.set(left, top, right, bottom)
    }


    abstract fun getDivider(itemPosition: Int): LineDivider

}