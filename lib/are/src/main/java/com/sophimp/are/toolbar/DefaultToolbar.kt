package com.sophimp.are.toolbar

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import com.sophimp.are.Constants
import com.sophimp.are.R
import com.sophimp.are.RichEditText
import com.sophimp.are.activity.IMediaChooseListener
import com.sophimp.are.activity.VideoAndImageGallery
import com.sophimp.are.models.MediaInfo
import com.sophimp.are.style.*
import com.sophimp.are.toolbar.items.*
import com.sophimp.are.window.ColorPickerWindow
import com.sophimp.are.window.FontSizeWindow
import com.sophimp.are.window.PickerListener

/**
 * demo toolbar， this also is the usage of each style
 * @author: sfx
 * @since: 2021/7/21
 */
class DefaultToolbar(context: Context, attrs: AttributeSet?) :
    HorizontalScrollView(context, attrs) {

    private var mTopContainer: LinearLayout? = null
    private var mBottomContainer: LinearLayout? = null

    private val mToolItems: MutableList<IToolbarItem> = arrayListOf()

    private var colorWindow: ColorPickerWindow
    private var fontWindow: FontSizeWindow
    private var curPopItem: IToolbarItem? = null

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

        colorWindow = ColorPickerWindow(context)
        colorWindow.pickerListener = object : PickerListener {
            override fun onPickValue(feature: Int) {
                curPopItem?.iconView?.background = null
                curPopItem?.iconView?.setMarkVisible(if (feature == Constants.DEFAULT_FEATURE) View.GONE else View.VISIBLE)
                curPopItem?.iconView?.setMarkBackgroundColor(feature)
                (curPopItem?.mStyle as DynamicCharacterStyle<*>).onFeatureChanged(feature)
                colorWindow.dismiss()
            }
        }

        fontWindow = FontSizeWindow(context)
        fontWindow.pickerListener = object : PickerListener {
            override fun onPickValue(feature: Int) {
                curPopItem?.iconView?.background = null
                curPopItem?.iconView?.setMarkVisible(if (feature == Constants.DEFAULT_FEATURE) View.GONE else View.VISIBLE)
                curPopItem?.iconView?.setMarkText("$feature")
                (curPopItem?.mStyle as DynamicCharacterStyle<*>).onFeatureChanged(feature)
                fontWindow.dismiss()
            }
        }
    }

    fun initDefaultToolItem(editText: RichEditText) {
        fontWindow.fontSizes = Array(50) { i -> (editText.textSize / context.resources.displayMetrics.scaledDensity + i + 1).toInt() }
        // top
        addToolbarItem(EmojiToolItem(EmojiStyle(editText)), true)
        addToolbarItem(ImageToolItem(ImageStyle(editText), object : IToolbarItemClickAction {
            override fun onItemClick(item: IToolbarItem) {
                VideoAndImageGallery.startActivity(context, object : IMediaChooseListener {
                    override fun onMediaChoose(mediaInfos: List<MediaInfo>) {
                        (item.mStyle as ImageStyle).apply {
                            for (info in mediaInfos) {
                                addImageSpan(info.data!!)
                            }
                        }
                    }
                })
            }
        }), true)

        addToolbarItem(VideoToolItem(VideoStyle(editText)), true)

        val dynamicItemClickAction = object : IToolbarItemClickAction {
            override fun onItemClick(item: IToolbarItem) {
                if (colorWindow.isShowing || fontWindow.isShowing) return
                item.iconView.setBackgroundResource(R.drawable.shape_board_bg)
                curPopItem = item
                colorWindow.showAsDropDown(item.iconView,
                    -(item.iconView.x).toInt(),
                    -(context.resources.displayMetrics.density * 80 + item.iconView.height + 10).toInt(),
                    Gravity.TOP)
            }
        }
        addToolbarItem(FontColorToolItem(FontColorStyle(editText), dynamicItemClickAction), true)
        addToolbarItem(FontBackgroundColorToolItem(FontBackgroundStyle(editText), dynamicItemClickAction), true)
        addToolbarItem(FontSizeToolItem(FontSizeStyle(editText), object : IToolbarItemClickAction {
            override fun onItemClick(item: IToolbarItem) {
                if (colorWindow.isShowing || fontWindow.isShowing) return
                curPopItem = item
                fontWindow.showAsDropDown(item.iconView,
                    -(item.iconView.x).toInt(),
                    -(context.resources.displayMetrics.density * 80 + item.iconView.height + 10).toInt(),
                    Gravity.TOP)
            }
        }), true)

        addToolbarItem(IndentRightToolItem(IndentRightStyle(editText)), true)
        addToolbarItem(IndentLeftToolItem(IndentLeftStyle(editText)), true)

        addToolbarItem(AlignmentLeftToolItem(AlignmentLeftStyle(editText)), true)
        addToolbarItem(AlignmentCenterToolItem(AlignmentCenterStyle(editText)), true)
        addToolbarItem(AlignmentRightToolItem(AlignmentRightStyle(editText)), true)

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
        addToolbarItem(LineSpaceEnlargeToolItem(LineSpaceEnlargeStyle(editText)), false)
        addToolbarItem(LineSpaceReduceToolItem(LineSpaceReduceStyle(editText)), false)
        addToolbarItem(HrToolItem(HrStyle(editText)), false)
        addToolbarItem(LinkToolItem(LinkStyle(editText)), false)

        // add styles to richtext
        for (item: IToolbarItem in mToolItems) {
            editText.addStyle(item.mStyle)
        }
    }

    fun addToolbarItem(toolbarItem: IToolbarItem, addTop: Boolean) {
//        Util.log("addTop: $addTop")
        if (addTop)
            mTopContainer?.addView(toolbarItem.iconView)
        else
            mBottomContainer?.addView(toolbarItem.iconView)
        mToolItems.add(toolbarItem)
    }

}