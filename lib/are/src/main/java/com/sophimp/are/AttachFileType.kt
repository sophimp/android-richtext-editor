package com.sophimp.are

import android.text.TextUtils
import androidx.annotation.DrawableRes

enum class AttachFileType(
    val attachmentValue: String,
    val dataType: String,
    @param:DrawableRes val resId: Int,
    var suffixs: List<String>
) {
    VIDEO("01", "video/*", R.mipmap.icon_video_play, listOf("m4a", "mp4", "avi", "mpg", "mov", "dat", "swf", "rm", "rmvb", "3gp", "mpeg", "mkv")),
    AUDIO("02", "audio/*", R.mipmap.icon_file_audio, listOf<String>("wav", "aif", "au", "mp3", "ram", "wma", "mmf", "amr", "aac", "flac")),
    EXCEL("03", "application/vnd.ms-excel", R.mipmap.icon_file_excel, listOf("xls", "xlsx")),
    WORD("04", "application/msword", R.mipmap.icon_file_word, listOf("doc", "docx")),
    PPT("05", "application/vnd.ms-powerpoint", R.mipmap.icon_file_ppt, listOf("ppt", "pptx")),
    PDF("06", "application/pdf", R.mipmap.icon_file_pdf, listOf("pdf")),
    ZIP("07", "*/*", R.mipmap.icon_file_zip, listOf("rar", "zip", "arj", "gz", "tar", "tar.gz", "7z")),
    TXT("08", "text/plain", R.mipmap.icon_file_txt, listOf("txt")),
    IMG("10", "image/*", 0, listOf("bmp", "gif", "jpg", "jpeg", "tif", "png")),
    OTHER("09", "*/*", R.mipmap.icon_file_other, listOf());

    companion object {
        fun getAttachmentTypeBySuffix(suffix: String): AttachFileType {
            when {
                VIDEO.suffixs.contains(suffix) -> {
                    return VIDEO
                }
                AUDIO.suffixs.contains(suffix) -> {
                    return AUDIO
                }
                EXCEL.suffixs.contains(suffix) -> {
                    return EXCEL
                }
                WORD.suffixs.contains(suffix) -> {
                    return WORD
                }
                PPT.suffixs.contains(suffix) -> {
                    return PPT
                }
                PDF.suffixs.contains(suffix) -> {
                    return PDF
                }
                ZIP.suffixs.contains(suffix) -> {
                    return ZIP
                }
                TXT.suffixs.contains(suffix) -> {
                    return TXT
                }
                IMG.suffixs.contains(suffix) -> {
                    return IMG
                }
                else -> return OTHER
            }
        }

        fun getAttachmentTypeByPath(path: String): AttachFileType {
            var suffix = ""
            if (!TextUtils.isEmpty(path)) {
                val index = path.lastIndexOf(".")
                if (index + 1 < path.length - 1) {
                    suffix = path.substring(index + 1)
                }
            }
            return getAttachmentTypeBySuffix(suffix)
        }

        @JvmStatic
        fun getAttachmentTypeByValue(attachmentValue: String?): AttachFileType {
            if (VIDEO.attachmentValue.equals(attachmentValue, ignoreCase = true)) {
                return VIDEO
            } else if (AUDIO.attachmentValue.equals(attachmentValue, ignoreCase = true)) {
                return AUDIO
            } else if (EXCEL.attachmentValue.equals(attachmentValue, ignoreCase = true)) {
                return EXCEL
            } else if (WORD.attachmentValue.equals(attachmentValue, ignoreCase = true)) {
                return WORD
            } else if (PPT.attachmentValue.equals(attachmentValue, ignoreCase = true)) {
                return PPT
            } else if (PDF.attachmentValue.equals(attachmentValue, ignoreCase = true)) {
                return PDF
            } else if (ZIP.attachmentValue.equals(attachmentValue, ignoreCase = true)) {
                return ZIP
            } else if (TXT.attachmentValue.equals(attachmentValue, ignoreCase = true)) {
                return TXT
            } else if (IMG.attachmentValue.equals(attachmentValue, ignoreCase = true)) {
                return IMG
            }
            return OTHER
        }
    }
}