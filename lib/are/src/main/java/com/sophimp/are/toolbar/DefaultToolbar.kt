package com.sophimp.are.toolbar

import android.content.Context
import android.text.Layout
import android.util.AttributeSet
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import com.sophimp.are.RichEditText
import com.sophimp.are.Util
import com.sophimp.are.style.*
import com.sophimp.are.toolbar.items.*

/**
 *
 * @author: sfx
 * @since: 2021/7/21
 */
class DefaultToolbar(context: Context, attrs: AttributeSet?) :
    HorizontalScrollView(context, attrs) {

    private var mTopContainer: LinearLayout? = null
    private var mBottomContainer: LinearLayout? = null

    private val mToolItems: MutableList<IToolbarItem> = arrayListOf()

    init {
        val mRootContainer = LinearLayout(context)
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        mRootContainer.layoutParams = params
        mRootContainer.orientation = LinearLayout.VERTICAL

        val height: Int = (resources.displayMetrics.density * 44).toInt()
        mTopContainer = LinearLayout(context)
        val topParams = LayoutParams(LayoutParams.MATCH_PARENT, height)
        mTopContainer?.layoutParams = topParams
        mTopContainer?.orientation = LinearLayout.HORIZONTAL

        mBottomContainer = LinearLayout(context)
        val bottomParams = LayoutParams(LayoutParams.MATCH_PARENT, height)
        mBottomContainer?.layoutParams = bottomParams
        mBottomContainer?.orientation = LinearLayout.HORIZONTAL

        mRootContainer.addView(mTopContainer)
        mRootContainer.addView(mBottomContainer)

        this.addView(mRootContainer)
    }

    fun initDefaultToolItem(editText: RichEditText) {
        // top
        addToolbarItem(ImageToolItem(ImageStyle(editText)), true)
        addToolbarItem(VideoToolItem(VideoStyle(editText)), true)
        addToolbarItem(FontColorToolItem(FontColorStyle(editText)), true)
        addToolbarItem(FontBackgroundColorToolItem(FontBackgroundStyle(editText)), true)
        addToolbarItem(FontSizeToolItem(FontSizeStyle(editText)), true)

        addToolbarItem(IndentRightToolItem(IndentRightStyle(editText)), true)
        addToolbarItem(IndentLeftToolItem(IndentLeftStyle(editText)), true)

        addToolbarItem(
            AlignmentLeftToolItem(AlignmentStyle(editText, Layout.Alignment.ALIGN_NORMAL)), true
        )
        addToolbarItem(
            AlignmentCenterToolItem(AlignmentStyle(editText, Layout.Alignment.ALIGN_CENTER)), true
        )
        addToolbarItem(
            AlignmentRightToolItem(AlignmentStyle(editText, Layout.Alignment.ALIGN_OPPOSITE)), true
        )

        addToolbarItem(QuoteToolItem(QuoteStyle(editText)), true)

        // bottom
        addToolbarItem(BoldToolItem(BoldStyle(editText)), false)
        addToolbarItem(UnderlineToolItem(UnderlineStyle(editText)), false)
        addToolbarItem(ItalicToolItem(ItalicStyle(editText)), false)
        addToolbarItem(StrikeThroughToolItem(StrikethroughStyle(editText)), false)
        addToolbarItem(ListNumberToolItem(ListNumberStyle(editText)), false)
        addToolbarItem(ListBulletToolItem(ListBulletStyle(editText)), false)
        addToolbarItem(TodoToolItem(TodoStyle(editText)), false)

        addToolbarItem(SubscriptToolItem(SubscriptStyle(editText)), false)
        addToolbarItem(SuperscriptToolItem(SuperscriptStyle(editText)), false)
        addToolbarItem(LineSpaceToolItem(LineSpaceStyle(editText, true)), false)
        addToolbarItem(LineSpaceToolItem(LineSpaceStyle(editText, false)), false)
        addToolbarItem(HrToolItem(HrStyle(editText)), false)
        addToolbarItem(LinkToolItem(LinkStyle(editText)), false)

        // add styles to richtext
        for (item: IToolbarItem in mToolItems) {
            editText.addStyle(item.mStyle)
        }
    }

    fun addToolbarItem(toolbarItem: IToolbarItem, addTop: Boolean) {
        Util.log("addTop: $addTop")
        if (addTop)
            mTopContainer?.addView(toolbarItem.iconView)
        else
            mBottomContainer?.addView(toolbarItem.iconView)
        mToolItems.add(toolbarItem)
    }

}