package com.sophimp.are.table

import android.content.Context
import android.graphics.Color
import com.sophimp.are.decoration.DividerItemDecoration
import com.sophimp.are.decoration.LineDivider
import com.sophimp.are.decoration.SideLine

/**
 * @des:
 * @since: 2021/6/24
 * @version: 0.1
 * @author: sfx
 */
class ItemDecoration(context: Context?, private val editTableViewModel: EditTableViewModel) : DividerItemDecoration(
    context!!) {
    override fun getDivider(itemPosition: Int): LineDivider {
        return if (itemPosition % editTableViewModel.col == editTableViewModel.col - 1) {
            if (itemPosition == editTableViewModel.cellCount - 1) {
                // 最后一个
                LineDivider().apply {
                    leftSideLine = SideLine(true, Color.parseColor("#303c3c43"), 1f, 0f, 0f)
                    topSideLine = SideLine(true, Color.parseColor("#303c3c43"), 1f, 0f, 0f)
                    rightSideLine = SideLine(true, Color.parseColor("#303c3c43"), 1f, 0f, 0f)
                    bottomSideLine = SideLine(true, Color.parseColor("#303c3c43"), 1f, 0f, 0f)
                }
            } else {
                // 行尾
                LineDivider().apply {
                    leftSideLine = SideLine(true, Color.parseColor("#303c3c43"), 1f, 0f, 0f)
                    topSideLine = SideLine(true, Color.parseColor("#303c3c43"), 1f, 0f, 0f)
                    rightSideLine = SideLine(true, Color.parseColor("#303c3c43"), 1f, 0f, 0f)
                }
            }
        } else if (itemPosition > editTableViewModel.cellCount - editTableViewModel.col - 1) {
            // 最后一行
            LineDivider().apply {
                leftSideLine = SideLine(true, Color.parseColor("#303c3c43"), 1f, 0f, 0f)
                topSideLine = SideLine(true, Color.parseColor("#303c3c43"), 1f, 0f, 0f)
                bottomSideLine = SideLine(true, Color.parseColor("#303c3c43"), 1f, 0f, 0f)
            }
        } else {
            LineDivider().apply {
                leftSideLine = SideLine(true, Color.parseColor("#303c3c43"), 1f, 0f, 0f)
                topSideLine = SideLine(true, Color.parseColor("#303c3c43"), 1f, 0f, 0f)
            }
        }
    }
}