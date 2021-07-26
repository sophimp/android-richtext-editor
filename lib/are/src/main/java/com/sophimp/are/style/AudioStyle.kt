package com.sophimp.are.style

import android.text.Layout
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AlignmentSpan
import android.text.style.ImageSpan
import com.sophimp.are.Constants
import com.sophimp.are.RichEditText
import com.sophimp.are.spans.AudioSpan

class AudioStyle(editText: RichEditText) : BaseFreeStyle<AudioSpan>(editText) {
    //	public static final int REQUEST_CODE = 1001;
    fun insertAudio(
        audioPath: String?,
        audioUrl: String?,
        audioDuration: String?,
        audioName: String?,
        audioSize: String?
    ) {
//
//        View view = LayoutInflater.from(mContext).inflate(R.layout.view_edit_annex, null);
//        ((ImageView) view.findViewById(R.id.edit_annex_icon_iv)).setImageResource(AttachmentType.AUDIO.getResId());
//        ((TextView) view.findViewById(R.id.edit_annex_title_tv)).setText(audioName);
//
//        if (StringUtils.parseLong(audioDuration) <= 0) {
//            ((TextView) view.findViewById(R.id.edit_annex_subtitle_tv)).setText(FileUtils.getFileSizeDesc(StringUtils.parseLong(audioSize)));
//        } else {
//            ((TextView) view.findViewById(R.id.edit_annex_subtitle_tv)).setText(StringUtils.getTimeDurationDesc(StringUtils.parseLong(audioDuration))
//                    + "  " + FileUtils.getFileSizeDesc(StringUtils.parseLong(audioSize)));
//        }
//
//        Bitmap background = Util.view2Bitmap(view);
//        if (background == null) return;
//
//        ImageSpan imageSpan = new DRAudioSpan(mContext, background, audioPath, audioUrl, audioName, audioSize, audioDuration);
//        insertSpan(imageSpan);
//
//        view.destroyDrawingCache();
    }

    private fun insertSpan(imageSpan: ImageSpan) {
        val editable = mEditText.editableText
        val start = mEditText.selectionStart
        val end = mEditText.selectionEnd
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
    }


    override fun targetClass(): Class<AudioSpan> {
        return AudioSpan::class.java
    }
}