package com.sophimp.are.toolbar.items

import android.text.TextUtils
import com.sophimp.are.R
import com.sophimp.are.dialog.LinkInputDialog
import com.sophimp.are.spans.UrlSpan
import com.sophimp.are.style.IStyle
import com.sophimp.are.style.LinkStyle

/**
 * @author: sfx
 * @since: 2021/7/22
 */
class LinkToolItem(style: IStyle) : AbstractItem(style) {

    override val srcResId: Int
        get() = R.mipmap.icon_toolitem_link

    override fun iconClickHandle() {
        openLinkDialog()
    }

    private fun openLinkDialog() {
        LinkInputDialog(context).setLinkLister(object : LinkInputDialog.OnInertLinkListener {
            override fun onLinkInert(linkAddr: String, linkName: String) {
                insertLink(linkAddr, linkName)
            }
        }).show()
    }

    private fun insertLink(linkAddr: String, linkName: String) {
        var linkAddr = linkAddr
        if (TextUtils.isEmpty(linkAddr)) {
            return
        }
        if (!linkAddr.startsWith(LinkStyle.HTTP) && !linkAddr.startsWith(
                LinkStyle.HTTPS
            )
        ) {
            linkAddr = LinkStyle.HTTP + linkAddr
        }
        var insertStr = linkAddr
        if (!TextUtils.isEmpty(linkName)) {
            insertStr = linkName
        }

        val editable = mEditText.editableText
        val start = mEditText.selectionStart
        var end = mEditText.selectionEnd
        if (start == end) {
            editable.insert(start, insertStr)
            end = start + insertStr.length
        }
        mStyle.setSpan(UrlSpan(linkAddr), start, end)
    }
}