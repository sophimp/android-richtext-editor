package com.sophimp.are.toolbar

import android.content.Context
import android.text.Layout
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import com.sophimp.are.RichEditText
import com.sophimp.are.style.*
import com.sophimp.are.toolbar.items.*

/**
 *
 * @author: sfx
 * @since: 2021/7/21
 */
class DefaultToolbar(context: Context, attrs: AttributeSet?) :
    HorizontalScrollView(context, attrs) {

    private var mContainer: LinearLayout? = null

    private val mToolItems: MutableList<IToolbarItem> = arrayListOf()

    init {
        mContainer = LinearLayout(context)
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        mContainer?.gravity = Gravity.CENTER_VERTICAL
        mContainer?.layoutParams = params
        this.addView(mContainer)
    }

    fun initDefaultToolItem(editText: RichEditText) {
        addToolbarItem(FontColorToolItem(FontColorStyle(editText)))
        addToolbarItem(FontSizeToolItem(FontSizeStyle(editText)))
        addToolbarItem(FontBackgroundColorToolItem(FontBackgroundStyle(editText)))

        addToolbarItem(ImageToolItem(ImageStyle(editText)))
        addToolbarItem(VideoToolItem(VideoStyle(editText)))

        addToolbarItem(BoldToolItem(BoldStyle(editText)))
        addToolbarItem(StrikeThroughToolItem(StrikethroughStyle(editText)))
        addToolbarItem(UnderlineToolItem(UnderlineStyle(editText)))
        addToolbarItem(ItalicToolItem(ItalicStyle(editText)))
        addToolbarItem(ListNumberToolItem(ListNumberStyle(editText)))
        addToolbarItem(ListBulletToolItem(ListBulletStyle(editText)))
        addToolbarItem(TodoToolItem(TodoStyle(editText)))

        addToolbarItem(
            AlignmentLeftToolItem(
                AlignmentStyle(
                    editText,
                    Layout.Alignment.ALIGN_NORMAL
                )
            )
        )
        addToolbarItem(
            AlignmentCenterToolItem(
                AlignmentStyle(
                    editText,
                    Layout.Alignment.ALIGN_CENTER
                )
            )
        )
        addToolbarItem(
            AlignmentRightToolItem(
                AlignmentStyle(
                    editText,
                    Layout.Alignment.ALIGN_OPPOSITE
                )
            )
        )

        addToolbarItem(IndentLeftToolItem(IndentLeftStyle(editText)))
        addToolbarItem(IndentRightToolItem(IndentRightStyle(editText)))

        addToolbarItem(LineSpaceToolItem(LineSpaceStyle(editText, true)))
        addToolbarItem(LineSpaceToolItem(LineSpaceStyle(editText, false)))
        addToolbarItem(HrToolItem(HrStyle(editText)))
        addToolbarItem(LinkToolItem(LinkStyle(editText)))
    }

    open fun addToolbarItem(toolbarItem: IToolbarItem) {
        val view: View? = toolbarItem.iconView
        if (view != null) {
            mContainer?.addView(view)
        }
        mToolItems.add(toolbarItem)
    }

}