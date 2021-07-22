package com.sophimp.are.style

import android.text.*
import android.text.style.AlignmentSpan
import android.text.style.ImageSpan
import com.sophimp.are.AttachmentType
import com.sophimp.are.Constants
import com.sophimp.are.RichEditText

class AttachmentStyle(editText: RichEditText) : BaseStyle(editText) {
    fun insertAttachment(
        attachmentPath: String?,
        attachmentUrl: String?,
        attachmentName: String?,
        attachmentSize: String?,
        attachmentType: AttachmentType?
    ) {
//        View view = LayoutInflater.from(mContext).inflate(R.layout.view_edit_annex, null);
//        ((ImageView) view.findViewById(R.id.edit_annex_icon_iv)).setImageResource(attachmentType.getResId());
//        ((TextView) view.findViewById(R.id.edit_annex_title_tv)).setText(attachmentName);
//
//        ((TextView) view.findViewById(R.id.edit_annex_subtitle_tv)).setText(FileUtils.getFileSizeDesc(StringUtils.parseLong(attachmentSize)));
//
//        Bitmap background = Util.view2Bitmap(view);
//        if (background == null) return;
//
//        ImageSpan imageSpan = new DRAttachmentSpan(mContext, background, attachmentPath, attachmentUrl, attachmentName, attachmentSize, attachmentType.getAttachmentValue());
//        insertSpan(imageSpan);
//
//        view.destroyDrawingCache();
    }

    private fun insertSpan(imageSpan: ImageSpan) {
        val editable: Editable = mEditText.editableText
        val start: Int = mEditText.selectionStart
        val end: Int = mEditText.selectionEnd
        val centerSpan: AlignmentSpan =
            AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER)
        val ssb = SpannableStringBuilder()
        ssb.append(Constants.CHAR_NEW_LINE)
        ssb.append(Constants.ZERO_WIDTH_SPACE_STR)
        ssb.append(Constants.CHAR_NEW_LINE)
        ssb.append(Constants.ZERO_WIDTH_SPACE_STR)
        ssb.setSpan(imageSpan, 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ssb.setSpan(centerSpan, 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        val leftSpan: AlignmentSpan =
            AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL)
        ssb.setSpan(leftSpan, 3, 4, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        editable.replace(start, end, ssb)
        mEditText.isChange = true
    }

    override fun applyStyle(
        editable: Editable,
        event: IStyle.TextEvent?,
        changedText: String?,
        beforeSelectionStart: Int,
        start: Int,
        end: Int
    ) {
    }

    override fun bindEditText(editText: RichEditText) {}

}