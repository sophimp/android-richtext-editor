package com.sophimp.are;

import android.text.TextUtils;

import androidx.annotation.DrawableRes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum AttachmentType {

    VIDEO("01", "video/*", R.mipmap.icon_video_play, "m4a", "mp4", "avi", "mpg", "mov", "dat", "swf", "rm", "rmvb", "3gp", "mpeg", "mkv"),
    AUDIO("02", "audio/*", R.mipmap.icon_file_audio, "wav", "aif", "au", "mp3", "ram", "wma", "mmf", "amr", "aac", "flac"),
    EXCEL("03", "application/vnd.ms-excel", R.mipmap.icon_file_excel, "xls", "xlsx"),
    WORD("04", "application/msword", R.mipmap.icon_file_word, "doc", "docx"),
    PPT("05", "application/vnd.ms-powerpoint", R.mipmap.icon_file_ppt, "ppt", "pptx"),
    PDF("06", "application/pdf", R.mipmap.icon_file_pdf, "pdf"),
    ZIP("07", "*/*", R.mipmap.icon_file_zip, "rar", "zip", "arj", "gz", "tar", "tar.gz", "7z"),
    TXT("08", "text/plain", R.mipmap.icon_file_txt, "txt"),
    OTHER("09", "*/*", R.mipmap.icon_file_other),
    IMG("10", "image/*", 0, "bmp", "gif", "jpg", "jpeg", "tif", "png");

    private String attachmentValue;
    private int resId;
    private String[] suffixs;
    private String dataType;
    private static Map<String, String> suffixMap;

    static {
        suffixMap = new HashMap<>();
        suffixMap.put("m4a", "0");
        suffixMap.put("mp4", "1");
        suffixMap.put("avi", "2");
        suffixMap.put("mpg", "3");
        suffixMap.put("mov", "4");
        suffixMap.put("dat", "5");
        suffixMap.put("swf", "6");
        suffixMap.put("rm", "7");
        suffixMap.put("rmvb", "8");
        suffixMap.put("3gp", "9");
        suffixMap.put("mpeg", "10");
        suffixMap.put("mkv", "11");
        suffixMap.put("wav", "12");
        suffixMap.put("aif", "13");
        suffixMap.put("au", "14");
        suffixMap.put("mp3", "15");
        suffixMap.put("ram", "16");
        suffixMap.put("wma", "17");
        suffixMap.put("mmf", "18");
        suffixMap.put("amr", "19");
        suffixMap.put("aac", "20");
        suffixMap.put("flac", "21");
        suffixMap.put("xls", "22");
        suffixMap.put("xlsx", "23");
        suffixMap.put("doc", "24");
        suffixMap.put("docx", "25");
        suffixMap.put("ppt", "26");
        suffixMap.put("pptx", "27");
        suffixMap.put("pdf", "28");
        suffixMap.put("rar", "29");
        suffixMap.put("zip", "30");
        suffixMap.put("arj", "31");
        suffixMap.put("gz", "32");
        suffixMap.put("tar", "33");
        suffixMap.put("tar.gz", "34");
        suffixMap.put("7z", "35");
        suffixMap.put("txt", "36");
        suffixMap.put("bmp", "37");
        suffixMap.put("gif", "38");
        suffixMap.put("jpg", "39");
        suffixMap.put("jpeg", "40");
        suffixMap.put("tif", "41");
        suffixMap.put("png", "42");
    }

    AttachmentType(String attachmentValue, String dataType, @DrawableRes int resId, String... suffixs) {
        this.attachmentValue = attachmentValue;
        this.dataType = dataType;
        this.resId = resId;
        this.suffixs = suffixs;
    }

    public String getAttachmentValue() {
        return attachmentValue;
    }

    public int getResId() {
        return resId;
    }

    public String getDataType() {
        return dataType;
    }

    public static AttachmentType getAttachmentTypeBySuffix(String suffix) {

        if (Arrays.asList(VIDEO.suffixs).contains(suffix)) {
            return VIDEO;
        } else if (Arrays.asList(AUDIO.suffixs).contains(suffix)) {
            return AUDIO;
        } else if (Arrays.asList(EXCEL.suffixs).contains(suffix)) {
            return EXCEL;
        } else if (Arrays.asList(WORD.suffixs).contains(suffix)) {
            return WORD;
        } else if (Arrays.asList(PPT.suffixs).contains(suffix)) {
            return PPT;
        } else if (Arrays.asList(PDF.suffixs).contains(suffix)) {
            return PDF;
        } else if (Arrays.asList(ZIP.suffixs).contains(suffix)) {
            return ZIP;
        } else if (Arrays.asList(TXT.suffixs).contains(suffix)) {
            return TXT;
        } else if (Arrays.asList(IMG.suffixs).contains(suffix)) {
            return IMG;
        }


        return OTHER;
    }

    public static AttachmentType getAttachmentTypeByPath(String path) {
        String suffix = "";
        if (!TextUtils.isEmpty(path)) {
            int index = path.lastIndexOf(".");
            if (index + 1 < path.length() - 1) {
                suffix = path.substring(index + 1);
            }
        }
        return AttachmentType.getAttachmentTypeBySuffix(suffix);

    }

    public static AttachmentType getAttachmentTypeByValue(String attachmentValue) {
        if (VIDEO.getAttachmentValue().equalsIgnoreCase(attachmentValue)) {
            return VIDEO;
        } else if (AUDIO.getAttachmentValue().equalsIgnoreCase(attachmentValue)) {
            return AUDIO;
        } else if (EXCEL.getAttachmentValue().equalsIgnoreCase(attachmentValue)) {
            return EXCEL;
        } else if (WORD.getAttachmentValue().equalsIgnoreCase(attachmentValue)) {
            return WORD;
        } else if (PPT.getAttachmentValue().equalsIgnoreCase(attachmentValue)) {
            return PPT;
        } else if (PDF.getAttachmentValue().equalsIgnoreCase(attachmentValue)) {
            return PDF;
        } else if (ZIP.getAttachmentValue().equalsIgnoreCase(attachmentValue)) {
            return ZIP;
        } else if (TXT.getAttachmentValue().equalsIgnoreCase(attachmentValue)) {
            return TXT;
        } else if (IMG.getAttachmentValue().equalsIgnoreCase(attachmentValue)) {
            return IMG;
        }
        return OTHER;
    }

}
