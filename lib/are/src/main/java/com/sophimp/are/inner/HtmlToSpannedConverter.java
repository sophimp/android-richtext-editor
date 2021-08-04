package com.sophimp.are.inner;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AlignmentSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.ParagraphStyle;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TypefaceSpan;

import com.sophimp.are.AttachFileType;
import com.sophimp.are.Constants;
import com.sophimp.are.R;
import com.sophimp.are.Util;
import com.sophimp.are.models.AtItem;
import com.sophimp.are.spans.AtSpan;
import com.sophimp.are.spans.BoldSpan;
import com.sophimp.are.spans.EmojiSpan;
import com.sophimp.are.spans.FontBackgroundColorSpan;
import com.sophimp.are.spans.FontForegroundColorSpan;
import com.sophimp.are.spans.FontSizeSpan;
import com.sophimp.are.spans.HrSpan;
import com.sophimp.are.spans.ImageSpan2;
import com.sophimp.are.spans.IndentSpan;
import com.sophimp.are.spans.LineSpaceSpan;
import com.sophimp.are.spans.ListBulletSpan;
import com.sophimp.are.spans.ListNumberSpan;
import com.sophimp.are.spans.QuoteSpan2;
import com.sophimp.are.spans.TodoSpan;
import com.sophimp.are.spans.UnderlineSpan2;
import com.sophimp.are.spans.UrlSpan;
import com.sophimp.are.spans.VideoSpan;
import com.sophimp.are.style.ImageStyle;
import com.sophimp.are.style.VideoStyle;

import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class HtmlToSpannedConverter implements ContentHandler {

    private static final float[] HEADING_SIZES = {
            1.5f, 1.4f, 1.3f, 1.2f, 1.1f, 1f,
    };

    private String mSource;
    private XMLReader mReader;
    public SpannableStringBuilder mSpannableStringBuilder;
    private Html.ImageGetter mImageGetter;
    private Html.TagHandler mTagHandler;
    private int mFlags;

    private static Pattern sTextAlignPattern;
    private static Pattern sIndentPattern;
    private static Pattern sFontSizePattern;
    private static Pattern sLineSpacePattern;
    private static Pattern sForegroundColorPattern;
    private static Pattern sBackgroundColorPattern;
    private static Pattern sTextDecorationPattern;

    /**
     * Name-value mapping of HTML/CSS colors which have different values in {@link Color}.
     */
    private static final Map<String, Integer> sColorMap;

    private List<String> richTableStrs = new ArrayList<>();

    static {
        sColorMap = new HashMap();
        sColorMap.put("darkgray", 0xFFA9A9A9);
        sColorMap.put("gray", 0xFF808080);
        sColorMap.put("lightgray", 0xFFD3D3D3);
        sColorMap.put("darkgrey", 0xFFA9A9A9);
        sColorMap.put("grey", 0xFF808080);
        sColorMap.put("lightgrey", 0xFFD3D3D3);
        sColorMap.put("green", 0xFF008000);
    }

    private Bitmap defTableBitmap;

    private static Pattern getTextAlignPattern() {
        if (sTextAlignPattern == null) {
            sTextAlignPattern = Pattern.compile("(?:\\s+|\\A)text-align\\s*:\\s*(\\S*)\\b");
        }
        return sTextAlignPattern;
    }

    private static Pattern getIndentPattern() {
        if (sIndentPattern == null) {
            sIndentPattern = Pattern.compile("(?:\\s+|\\A)padding-left\\s*:\\s*(\\S*)\\b");
        }
        return sIndentPattern;
    }

    private static Pattern getForegroundColorPattern() {
        if (sForegroundColorPattern == null) {
            sForegroundColorPattern = Pattern.compile(
                    "(?:\\s+|\\A)color\\s*:\\s*(\\S*)\\b");
        }
        return sForegroundColorPattern;
    }

    private static Pattern getBackgroundColorPattern() {
        if (sBackgroundColorPattern == null) {
            sBackgroundColorPattern = Pattern.compile(
                    "(?:\\s+|\\A)background(?:-color)?\\s*:\\s*(\\S*)\\b");
        }
        return sBackgroundColorPattern;
    }

    private static Pattern getTextDecorationPattern() {
        if (sTextDecorationPattern == null) {
            sTextDecorationPattern = Pattern.compile(
                    "(?:\\s+|\\A)text-decoration\\s*:\\s*(\\S*)\\b");
        }
        return sTextDecorationPattern;
    }

    private static Pattern getFontSizePattern() {
        if (sFontSizePattern == null) {
            sFontSizePattern = Pattern.compile("(?:\\s+|\\A)font-size\\s*:\\s*(\\S*)\\b");
        }
        return sFontSizePattern;
    }

    private static Pattern getLineSpacePattern() {
        if (sLineSpacePattern == null) {
            sLineSpacePattern = Pattern.compile("(?:\\s+|\\A)line-height\\s*:\\s*(\\S*)\\b");
        }
        return sLineSpacePattern;
    }

    public HtmlToSpannedConverter(String source, Html.ImageGetter imageGetter,
                                  Html.TagHandler tagHandler, Parser parser, int flags) {
        // 先过滤表格，将内容缓存， 将所有表格标签及内容替换成<table/> 空标签，
        // 后续再解析此标签时，直接添加DRTableSpan，
        // 在解析完html后，最后由 DREditText 渲染前， 再将DRTableSpan 反显成有内容的图片
        StringBuilder tableFilter = new StringBuilder();
        int index = 0;
        richTableStrs.clear();
        while (index < source.length()) {
            int startElementStart = source.indexOf("<table", index);
            if (startElementStart >= 0) {
                if (startElementStart > index) {
                    tableFilter.append(source.substring(index, startElementStart));
                }// else 相等，不必处理
                // 查找<table> 结尾
                int startElementEnd = source.indexOf(">", startElementStart + 1);
                if (startElementEnd > startElementStart) {
                    int endElementStart = source.indexOf("</table>", startElementEnd + 1);
                    if (endElementStart > startElementEnd) {
                        // 缓存表格
                        index = endElementStart + "</table>".length();
                        richTableStrs.add(source.substring(startElementStart, index));
                        // 将此表格替换成 <table/> 标签
                        tableFilter.append("<table/>");
                    } else {
                        // 标签有误，跳过，继续后续的表格过滤
                        index = startElementEnd + 1;
                    }
                } else {
                    // 标签有误， 跳过, 继续后续的表格过滤
                    index = startElementStart + 1;
                    continue;
                }
            } else {
                // 没有了table
                tableFilter.append(source.substring(index));
                break;
            }
        }

        mSource = tableFilter.toString();

        mSpannableStringBuilder = new SpannableStringBuilder();
        mImageGetter = imageGetter;
        mTagHandler = tagHandler;
        mReader = parser;
        mFlags = flags;
    }

    public Spanned convert() {

        mReader.setContentHandler(this);
        try {
            mReader.parse(new InputSource(new StringReader(mSource)));
        } catch (IOException e) {
            // We are reading from a string. There should not be IO problems.
            throw new RuntimeException(e);
        } catch (SAXException e) {
            // TagSoup doesn't throw parse exceptions.
            throw new RuntimeException(e);
        }

        // Fix flags and range for paragraph-type markup.
        Object[] obj = mSpannableStringBuilder.getSpans(0, mSpannableStringBuilder.length(), ParagraphStyle.class);
        for (int i = 0; i < obj.length; i++) {
            int start = mSpannableStringBuilder.getSpanStart(obj[i]);
            int end = mSpannableStringBuilder.getSpanEnd(obj[i]);

            // If the last line of the range is blank, back off by one.
            if (end - 2 >= 0) {
                if (mSpannableStringBuilder.charAt(end - 1) == '\n' &&
                        mSpannableStringBuilder.charAt(end - 2) == '\n') {
                    end--;
                }
            }

            if (end == start) {
                mSpannableStringBuilder.removeSpan(obj[i]);
            } else {
                if (obj[i] instanceof LeadingMarginSpan) {
                    if (mSpannableStringBuilder.charAt(start) == '\n') {
                        start = start + 1;
                    }
                    if (mSpannableStringBuilder.charAt(start) != Constants.ZERO_WIDTH_SPACE_INT) {
                        mSpannableStringBuilder.insert(start, Constants.ZERO_WIDTH_SPACE_STR);
                        end = end + 1;
                    }
                    if (mSpannableStringBuilder.charAt(end - 1) == '\n') {
                        end = end - 1;
                    }
                    mSpannableStringBuilder.setSpan(obj[i], start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                } else {
                    try {
                        mSpannableStringBuilder.setSpan(obj[i], start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // 重新将 ListNumberSpan 排序
//        Util.reNumberBehindListItemSpans(0, mSpannableStringBuilder);
        Util.INSTANCE.renumberAllListItemSpans(mSpannableStringBuilder);

        return mSpannableStringBuilder;
    }

    private void handleStartTag(String tag, Attributes attributes) {
        if (tag.equalsIgnoreCase("br")) {
            // We don't need to handle this. TagSoup will ensure that there's a </br> for each <br>
            // so we can safely emit the linebreaks when we handle the close tag.
        } else if (tag.equalsIgnoreCase("p")) {
            startBlockElement(mSpannableStringBuilder, attributes, 1);
            startCssStyle(mSpannableStringBuilder, attributes);
        } else if (Html.TODO_LIST.equals(tag)) {
            String status = attributes.getValue("", "data-status");
            startTodo(mSpannableStringBuilder, attributes, "done".equalsIgnoreCase(status));
        } else if (tag.equalsIgnoreCase("ol")) {
            startOL(mSpannableStringBuilder);
            startBlockElement(mSpannableStringBuilder, attributes, 1, false);
        } else if (tag.equalsIgnoreCase("ul")) {
            startUL(mSpannableStringBuilder);
            startBlockElement(mSpannableStringBuilder, attributes, 1, false);
        } else if (tag.equalsIgnoreCase("li")) {
            startLi(mSpannableStringBuilder, attributes);
        } else if (tag.equalsIgnoreCase("div")) {
            startBlockElement(mSpannableStringBuilder, attributes, 1);
        } else if (tag.equalsIgnoreCase("span")) {
            startCssStyle(mSpannableStringBuilder, attributes);
        } else if (tag.equalsIgnoreCase("strong")) {
            start(mSpannableStringBuilder, new Bold());
        } else if (tag.equalsIgnoreCase("b")) {
            start(mSpannableStringBuilder, new Bold());
        } else if (tag.equalsIgnoreCase("em")) {
            start(mSpannableStringBuilder, new Italic());
        } else if (tag.equalsIgnoreCase("cite")) {
            start(mSpannableStringBuilder, new Italic());
        } else if (tag.equalsIgnoreCase("dfn")) {
            start(mSpannableStringBuilder, new Italic());
        } else if (tag.equalsIgnoreCase("i")) {
            start(mSpannableStringBuilder, new Italic());
        } else if (tag.equalsIgnoreCase("big")) {
            start(mSpannableStringBuilder, new Big());
        } else if (tag.equalsIgnoreCase("small")) {
            start(mSpannableStringBuilder, new Small());
        } else if (tag.equalsIgnoreCase("font")) {
            startFont(mSpannableStringBuilder, attributes);
        } else if (tag.equalsIgnoreCase("blockquote")) {
            startBlockquote(mSpannableStringBuilder, attributes);
        } else if (tag.equalsIgnoreCase("tt")) {
            start(mSpannableStringBuilder, new Monospace());
        } else if (tag.equalsIgnoreCase("a")) {
            startA(mSpannableStringBuilder, attributes);
        } else if (tag.equalsIgnoreCase("u")) {
            start(mSpannableStringBuilder, new Underline());
        } else if (tag.equalsIgnoreCase("del")) {
            start(mSpannableStringBuilder, new Strikethrough());
        } else if (tag.equalsIgnoreCase("s")) {
            start(mSpannableStringBuilder, new Strikethrough());
        } else if (tag.equalsIgnoreCase("strike")) {
            start(mSpannableStringBuilder, new Strikethrough());
        } else if (tag.equalsIgnoreCase("sup")) {
            start(mSpannableStringBuilder, new Super());
        } else if (tag.equalsIgnoreCase("sub")) {
            start(mSpannableStringBuilder, new Sub());
        } else if (tag.length() == 2 &&
                Character.toLowerCase(tag.charAt(0)) == 'h' &&
                tag.charAt(1) >= '1' && tag.charAt(1) <= '6') {
            startHeading(mSpannableStringBuilder, attributes, tag.charAt(1) - '1');
        } else if (tag.equalsIgnoreCase("img")) {
            startImg(mSpannableStringBuilder, attributes, mImageGetter);
        } else if (tag.equalsIgnoreCase("attachment")) {
            String data_type = attributes.getValue("", "data-type");
            if (AttachFileType.VIDEO.getAttachmentValue().equalsIgnoreCase(data_type)) {
                startVideo(mSpannableStringBuilder, attributes, mImageGetter);
            } else if (AttachFileType.AUDIO.getAttachmentValue().equalsIgnoreCase(data_type)) {
                startAudio(mSpannableStringBuilder, attributes);
            } else {
                startAttachment(mSpannableStringBuilder, attributes);
            }

        } else if (tag.equalsIgnoreCase("table")) {
            startTable(mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase("hr")) {
            startHr(mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase("emoji")) {
            startEmoji(mSpannableStringBuilder, attributes);
        } else if (mTagHandler != null) {
            mTagHandler.handleTag(true, tag, mSpannableStringBuilder, mReader);
        }
    }

    private void startTable(SpannableStringBuilder text) {
//        if (richTableStrs.size() > 0){
//            text.append("\n");
//            text.append(" ");
//            int len = text.length();
//            text.append(Constants.ZERO_WIDTH_SPACE_STR);
//            text.setSpan(new DRTableSpan(Html.sContext, defTableBitmap, richTableStrs.remove(0)), len, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
    }

    private void handleEndTag(String tag) {
        if (tag.equalsIgnoreCase("br")) {
            handleBr(mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase("p")) {
            endCssStyle(mSpannableStringBuilder);
            endBlockElement(mSpannableStringBuilder);
        } else if (Html.TODO_LIST.equalsIgnoreCase(tag)) {
            endTodo(mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase("ol")) {
            endOL(mSpannableStringBuilder);
            endBlockElement(mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase("ul")) {
            endUL(mSpannableStringBuilder);
            endBlockElement(mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase("li")) {
            endLi(mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase("div")) {
            endBlockElement(mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase("span")) {
            endCssStyle(mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase("strong")) {
            end(mSpannableStringBuilder, Bold.class, new BoldSpan());
        } else if (tag.equalsIgnoreCase("b")) {
            end(mSpannableStringBuilder, Bold.class, new BoldSpan());
        } else if (tag.equalsIgnoreCase("em")) {
            end(mSpannableStringBuilder, Italic.class, new StyleSpan(Typeface.ITALIC));
        } else if (tag.equalsIgnoreCase("cite")) {
            end(mSpannableStringBuilder, Italic.class, new StyleSpan(Typeface.ITALIC));
        } else if (tag.equalsIgnoreCase("dfn")) {
            end(mSpannableStringBuilder, Italic.class, new StyleSpan(Typeface.ITALIC));
        } else if (tag.equalsIgnoreCase("i")) {
            end(mSpannableStringBuilder, Italic.class, new StyleSpan(Typeface.ITALIC));
        } else if (tag.equalsIgnoreCase("big")) {
            end(mSpannableStringBuilder, Big.class, new RelativeSizeSpan(1.25f));
        } else if (tag.equalsIgnoreCase("small")) {
            end(mSpannableStringBuilder, Small.class, new RelativeSizeSpan(0.8f));
        } else if (tag.equalsIgnoreCase("font")) {
            endFont(mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase("blockquote")) {
            endBlockquote(mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase("tt")) {
            end(mSpannableStringBuilder, Monospace.class, new TypefaceSpan("monospace"));
        } else if (tag.equalsIgnoreCase("a")) {
            endA(mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase("u")) {
            end(mSpannableStringBuilder, Underline.class, new UnderlineSpan2());
        } else if (tag.equalsIgnoreCase("del")) {
            end(mSpannableStringBuilder, Strikethrough.class, new StrikethroughSpan());
        } else if (tag.equalsIgnoreCase("s")) {
            end(mSpannableStringBuilder, Strikethrough.class, new StrikethroughSpan());
        } else if (tag.equalsIgnoreCase("strike")) {
            end(mSpannableStringBuilder, Strikethrough.class, new StrikethroughSpan());
        } else if (tag.equalsIgnoreCase("sup")) {
            end(mSpannableStringBuilder, Super.class, new SuperscriptSpan());
        } else if (tag.equalsIgnoreCase("sub")) {
            end(mSpannableStringBuilder, Sub.class, new SubscriptSpan());
        } else if (tag.equalsIgnoreCase("table")) {
            endTable(mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase("attachment")) {
            endAttachment(mSpannableStringBuilder);
        } else if (tag.length() == 2 &&
                Character.toLowerCase(tag.charAt(0)) == 'h' &&
                tag.charAt(1) >= '1' && tag.charAt(1) <= '6') {
            endHeading(mSpannableStringBuilder);
        } else if (mTagHandler != null) {
            mTagHandler.handleTag(false, tag, mSpannableStringBuilder, mReader);
        }
    }

    private void endTable(SpannableStringBuilder text) {
        text.append("\n");
        text.append(" ");
    }


    private static void endAttachment(Editable text) {
        text.append("\n");
//        endCssStyle(text);
//        endBlockElement(text);
//
//        Todo todo = getLast(text, Todo.class);
//        if (todo != null) {
//            int where = text.getSpanStart(todo);
//            text.removeSpan(todo);
//            int len = text.length();
//            if (where != len) {
//                text.setSpan(new DRTodoSpan(todo.isCheck), where, len, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//            }
//        }
    }

    private int getMarginParagraph() {
        return getMargin(Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH);
    }

    private int getMarginHeading() {
//        return getMargin(Html.FROM_HTML_SEPARATOR_LINE_BREAK_HEADING);
        return 1;
    }

    private int getMarginListItem() {
        // return getMargin(Html.FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM);
        return 1;
    }

    private int getMarginList() {
        return getMargin(Html.FROM_HTML_SEPARATOR_LINE_BREAK_LIST);
    }

    private int getMarginDiv() {
        return getMargin(Html.FROM_HTML_SEPARATOR_LINE_BREAK_DIV);
    }

    private int getMarginBlockquote() {
//        return getMargin(Html.FROM_HTML_SEPARATOR_LINE_BREAK_BLOCKQUOTE);
        return 1;
    }

    /**
     * Returns the minimum number of newline characters needed before and after a given block-level
     * element.
     *
     * @param flag the corresponding option flag defined in {@link Html} of a block-level element
     */
    private int getMargin(int flag) {
        if ((flag & mFlags) != 0) {
            return 1;
        }
        return 2;
    }

    private static void appendNewlines(Editable text, int minNewline) {
        final int len = text.length();

        if (len == 0) {
            return;
        }

        int existingNewlines = 0;
        for (int i = len - 1; i >= 0 && text.charAt(i) == '\n'; i--) {
            existingNewlines++;
        }

        for (int j = existingNewlines; j < minNewline; j++) {
            text.append("\n");
        }
    }

    @SuppressLint("NewApi")
    private static void startBlockElement(Editable text, Attributes attributes, int margin, boolean parseIndent) {
        if (margin > 0) {
            appendNewlines(text, margin);
            start(text, new Newline(margin));
        }

        String style = attributes.getValue("", "style");
        if (style != null) {
            Matcher m = getTextAlignPattern().matcher(style);
            Matcher indentMatcher = getIndentPattern().matcher(style);
            if (m.find()) {
                String alignment = m.group(1);
                if (alignment.equalsIgnoreCase("start") || alignment.equalsIgnoreCase("left")) {
                    start(text, new Alignment(Layout.Alignment.ALIGN_NORMAL));
                } else if (alignment.equalsIgnoreCase("center")) {
                    start(text, new Alignment(Layout.Alignment.ALIGN_CENTER));
                } else if (alignment.equalsIgnoreCase("end") || alignment.equalsIgnoreCase("right")) {
                    start(text, new Alignment(Layout.Alignment.ALIGN_OPPOSITE));
                }
            }

            // 统一放在 cssStyle解析中做
//            if (parseIndent && indentMatcher.find()) {
//                String indentLength = indentMatcher.group(1);
//                int indentSize = getIndentSize(indentLength);
//                if (indentSize > 0) {
//                    start(text, new Indent(indentSize));
//                }
//            }
        }
    }

    @SuppressLint("NewApi")
    private static void startBlockElement(Editable text, Attributes attributes, int margin) {
        startBlockElement(text, attributes, margin, true);
    }

    private static void endBlockElement(Editable text) {
        Newline n = getLast(text, Newline.class);
        if (n != null) {
            appendNewlines(text, n.mNumNewlines);
            text.removeSpan(n);
        }

        Alignment a = getLast(text, Alignment.class);
        if (a != null) {
            setSpanFromMark(text, a, new AlignmentSpan.Standard(a.mAlignment));
        }

        // 统一放在endCssStyle中做
//        Indent indent = getLast(text, Indent.class);
//        if (indent != null && indent.mIndentLength > 0) {
//            DRLeadingMarginSpan leadingMarginSpan = new DRLeadingMarginSpan(indent.mIndentLength / DRLeadingMarginSpan.LEADING_MARGIN);
////            leadingMarginSpan.setLeadingMargin(indent.mIndentLength);
//            setSpanFromMark(text, indent, leadingMarginSpan);
//        }
    }

    private void startTodo(Editable text, Attributes attributes, boolean isCheck) {
        startBlockElement(text, attributes, getMarginListItem());
        start(text, new Todo(isCheck));
        startCssStyle(text, attributes);
    }

    private static void endTodo(Editable text) {
        endCssStyle(text);
        endBlockElement(text);
//        end(text, Todo.class, new DRTodoSpan());

//        Todo[] objs = text.getSpans(0, text.length(), Todo.class);
//
//        if (objs != null && objs.length > 0) {
//            for (Todo obj : objs) {
//                int where = text.getSpanStart(obj);
//                int len = text.getSpanEnd(obj);
//                text.removeSpan(obj);
//                if (where != len) {
//                    text.setSpan(new DRTodoSpan(obj.isCheck), where, len, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//                }
//            }
//
//        }

        Todo todo = getLast(text, Todo.class);
        if (todo != null) {
            int where = text.getSpanStart(todo);
            text.removeSpan(todo);
            int len = text.length();
            if (where != len) {
                text.setSpan(new TodoSpan(Html.sContext, todo.isCheck), where, len, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }

        }
    }

    private static void handleBr(Editable text) {
        text.append('\n');
    }

    private void startOL(Editable text) {
        int level = OL_UL_STACK.size();
        OL ol = new OL(level);
        start(text, ol);
        OL_UL_STACK.push(ol);
//        Html.sListNumber = 0;
    }

    private void endOL(Editable text) {
//        Html.sListNumber = -1;
        if (OL_UL_STACK.isEmpty()) {
            return;
        }
        Object peekEle = OL_UL_STACK.peek();
        if (peekEle instanceof OL) {
            OL_UL_STACK.pop();
        }
    }

    private void startUL(Editable text) {
        int level = OL_UL_STACK.size();
        UL ul = new UL(level);
        start(text, ul);
        OL_UL_STACK.push(ul);
    }

    private void endUL(Editable text) {
        if (OL_UL_STACK.isEmpty()) {
            return;
        }
        Object peekEle = OL_UL_STACK.peek();
        if (peekEle instanceof UL) {
            OL_UL_STACK.pop();
        }
    }

    private void startLi(Editable text, Attributes attributes) {
        startBlockElement(text, attributes, getMarginListItem());
        startCssStyle(text, attributes);
        Object peekEle = OL_UL_STACK.peek();
        int len = text.length();
        text.append(Constants.ZERO_WIDTH_SPACE_STR);
        if (peekEle instanceof OL) {
//            start(text, new Numeric());
            text.setSpan(new Numeric(), len, text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        } else {
//            start(text, new Bullet());
            text.setSpan(new Bullet(), len, text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
    }


    private static void endLi(Editable text) {
        endCssStyle(text);
        endBlockElement(text);
        Object peekEle = OL_UL_STACK.peek();
        if (peekEle instanceof OL) {
//            Html.sListNumber = Html.sListNumber + 1;
            end(text, Numeric.class, new ListNumberSpan());
        } else {
            end(text, Bullet.class, new ListBulletSpan());
        }
    }


    private void startBlockquote(Editable text, Attributes attributes) {
        startBlockElement(text, attributes, getMarginBlockquote());
        start(text, new Blockquote());
    }

    private static void endBlockquote(Editable text) {
        endBlockElement(text);
        end(text, Blockquote.class, new QuoteSpan2());
    }

    private void startHeading(Editable text, Attributes attributes, int level) {
        startBlockElement(text, attributes, getMarginHeading());
        start(text, new Heading(level));
    }

    private static void endHeading(Editable text) {
        // RelativeSizeSpan and StyleSpan are CharacterStyles
        // Their ranges should not include the newlines at the end
        Heading h = getLast(text, Heading.class);
        if (h != null) {
            setSpanFromMark(text, h, new RelativeSizeSpan(HEADING_SIZES[h.mLevel]),
                    new StyleSpan(Typeface.BOLD));
        }

        endBlockElement(text);
    }

    private static <T> T getLast(Spanned text, Class<T> kind) {
        /*
         * This knows that the last returned object from getSpans()
         * will be the most recently added.
         */
        T[] objs = text.getSpans(0, text.length(), kind);

        if (objs.length == 0) {
            return null;
        } else {
            return objs[objs.length - 1];
        }
    }

    private static void setSpanFromMark(Spannable text, Object mark, Object... spans) {
        int where = text.getSpanStart(mark);
        text.removeSpan(mark);
        int len = text.length();
        if (where != len) {
            for (Object span : spans) {
                text.setSpan(span, where, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private static void start(Editable text, Object mark) {
        int len = text.length();
        text.setSpan(mark, len, len, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
    }

    private static void end(Editable text, Class kind, Object repl) {
        int len = text.length();
        Object obj = getLast(text, kind);
        if (obj != null) {
            setSpanFromMark(text, obj, repl);
        }
    }

    private void startCssStyle(Editable text, Attributes attributes) {
        String style = attributes.getValue("", "style");
        if (style != null) {
            String[] split = style.split(";");
            if (split != null && split.length > 0) {
                for (String s : split) {

                    Matcher m = getForegroundColorPattern().matcher(s);
                    if (m.find()) {
                        int c = getHtmlColor(m.group(1));
                        if (c != -1) {
                            start(text, new Foreground(c | 0xFF000000));
                        }
                    }

                    m = getBackgroundColorPattern().matcher(s);
                    if (m.find()) {
                        int c = getHtmlColor(m.group(1));
                        if (c != -1) {
                            start(text, new Background(c | 0xFF000000));
                        }
                    }

                    m = getTextDecorationPattern().matcher(s);
                    if (m.find()) {
                        String textDecoration = m.group(1);
                        if (textDecoration.equalsIgnoreCase("line-through")) {
                            start(text, new Strikethrough());
                        } else if (textDecoration.equalsIgnoreCase("underline")) {
                            start(text, new Underline());
                        }
                    }

                    m = getFontSizePattern().matcher(s);
                    if (m.find()) {
                        int fontSize = getFontSize(m.group(1));
                        start(text, new FontSize(fontSize));
                    }
                    m = getLineSpacePattern().matcher(s);
                    if (m.find()) {
                        float lineSpaceFactor = getLineSpaceFactor(m.group(1));
                        start(text, new LineSpace(lineSpaceFactor));
                    }
                    m = getIndentPattern().matcher(s);
                    if (m.find()) {
                        String indentLength = m.group(1);
                        int indentSize = getIndentSize(indentLength);
                        if (indentSize > 0) {
                            start(text, new Indent(indentSize));
                        }
                    }
                }
            }
        }
    }

    private static void endCssStyle(Editable text) {
        Strikethrough s = getLast(text, Strikethrough.class);
        if (s != null) {
            setSpanFromMark(text, s, new StrikethroughSpan());
        }

        Underline u = getLast(text, Underline.class);
        if (u != null) {
            setSpanFromMark(text, u, new UnderlineSpan2());
        }

        Background b = getLast(text, Background.class);
        if (b != null) {
            setSpanFromMark(text, b, new FontBackgroundColorSpan(b.mBackgroundColor));
        }

        Foreground f = getLast(text, Foreground.class);
        if (f != null) {
            setSpanFromMark(text, f, new FontForegroundColorSpan(f.mForegroundColor));
        }

        FontSize fontSize = getLast(text, FontSize.class);
        if (fontSize != null) {
            setSpanFromMark(text, fontSize, new FontSizeSpan(fontSize.mFontSize));
        }

        LineSpace lineSpace = getLast(text, LineSpace.class);
        if (lineSpace != null && lineSpace.factor > 1.0f) {
            setSpanFromMark(text, lineSpace, new LineSpaceSpan(lineSpace.factor));
        }

        Indent indent = getLast(text, Indent.class);
        if (indent != null && indent.mIndentLength > 0) {
            IndentSpan leadingMarginSpan = new IndentSpan(indent.mIndentLength / IndentSpan.LEADING_MARGIN);
//            leadingMarginSpan.setLeadingMargin(indent.mIndentLength);
            setSpanFromMark(text, indent, leadingMarginSpan);
        }
    }

    private static void startImg(Editable text, Attributes attributes, Html.ImageGetter img) {
        if (Html.sContext == null) return;

        String src = attributes.getValue("", "src");
        String width = attributes.getValue("", "width");
        String height = attributes.getValue("", "height");
        String name = attributes.getValue("", "name");
        String size = attributes.getValue("", "size");
        String uploadTime = attributes.getValue("", "uploadTime");
        String dataType = attributes.getValue("", "data-type");

        if (width == null) {
            width = "0";
        }
        if (height == null) {
            height = "0";
        }

        int len = text.length();
        // obj符号 "\uFFFC"
        text.append("\uFFFC");
        text.append("\n");

        Drawable defDrawable = Html.sContext.getResources().getDrawable(R.mipmap.default_image);
        defDrawable.setBounds(0, 0, defDrawable.getIntrinsicWidth(), defDrawable.getIntrinsicHeight());
        String localPath = "", url = "";
        if (!TextUtils.isEmpty(src)) {
            if (new File(src).exists()) {
                localPath = src;
            } else {
                url = src;
            }
        }
        ImageSpan2 defSpan = new ImageSpan2(defDrawable, localPath, url, name, size, Integer.parseInt(width), Integer.parseInt(height));
        defSpan.setUploadTime(uploadTime);
        ImageStyle.Companion.addImageSpanToEditable(Html.sContext, text, len, defSpan);
    }

    private static void startVideo(final Editable text, Attributes attributes, Html.ImageGetter imageGetter) {

        if (Html.sContext == null) return;

        String url = attributes.getValue("", "data-url");
        String type = attributes.getValue("", "data-type");
        final String name = attributes.getValue("", "data-file-name");
        final String size = attributes.getValue("", "data-file-size");
        String uploadTime = attributes.getValue("", "data-uploadtime");
        final String duration = attributes.getValue("", "data-duration");

        Drawable defDrawable = Html.sContext.getResources().getDrawable(R.mipmap.default_image);
        defDrawable.setBounds(0, 0, defDrawable.getIntrinsicWidth(), defDrawable.getIntrinsicHeight());
        String localPath = "", videoUrl = "";
        if (!TextUtils.isEmpty(url)) {
            if (new File(url).exists()) {
                localPath = url;
            } else {
                videoUrl = url;
            }
        }
        VideoSpan defSpan = new VideoSpan(defDrawable, localPath, videoUrl, name, size, duration);
        defSpan.setUploadTime(uploadTime);
        int len = text.length();
        // obj符号 "\uFFFC"
        text.append("\uFFFC\n");
        VideoStyle.Companion.addVideoSpanToEditable(Html.sContext, text, len, defSpan);

    }

    private static void startAudio(Editable text, Attributes attributes) {

        if (Html.sContext == null) return;

        String url = attributes.getValue("", "data-url");
        String type = attributes.getValue("", "data-type");
        String name = attributes.getValue("", "data-file-name");
        String size = attributes.getValue("", "data-file-size");
        String uploadTime = attributes.getValue("", "data-uploadtime");
        String duration = attributes.getValue("", "data-duration");

    }

    private static void startAttachment(Editable text, Attributes attributes) {
        if (Html.sContext == null) return;

        String url = attributes.getValue("", "data-url");
        String type = attributes.getValue("", "data-type");
        String name = attributes.getValue("", "data-file-name");
        String size = attributes.getValue("", "data-file-size");
        String uploadTime = attributes.getValue("", "data-uploadtime");
        String duration = attributes.getValue("", "data-duration");
        AttachFileType attachmentType = AttachFileType.getAttachmentTypeByValue(type);

    }


    private static void startHr(Editable text) {
        int len = text.length();
        text.append(Constants.ZERO_WIDTH_SPACE_STR);
        text.setSpan(new HrSpan(Html.sContext), len, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private static void startEmoji(Editable text, Attributes attributes) {
        String src = attributes.getValue("", "src");
        int emojiSrc = Integer.parseInt(src);
        Drawable d = Html.sContext.getResources().getDrawable(emojiSrc);
        int size = d.getIntrinsicHeight();
        EmojiSpan emojiSpan = new EmojiSpan(Html.sContext, emojiSrc, size);
        int len = text.length();
        text.append("\uFFFC");
        text.setSpan(emojiSpan, len, text.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void startFont(Editable text, Attributes attributes) {
        String color = attributes.getValue("", "color");
        String face = attributes.getValue("", "face");

        if (!TextUtils.isEmpty(color)) {
            int c = getHtmlColor(color);
            if (c != -1) {
                start(text, new Foreground(c | 0xFF000000));
            }
        }

        if (!TextUtils.isEmpty(face)) {
            start(text, new Font(face));
        }
    }


    private static void endFont(Editable text) {
        Font font = getLast(text, Font.class);
        if (font != null) {
            setSpanFromMark(text, font, new TypefaceSpan(font.mFace));
        }

        Foreground foreground = getLast(text, Foreground.class);
        if (foreground != null) {
            setSpanFromMark(text, foreground,
                    new FontForegroundColorSpan(foreground.mForegroundColor));
        }
    }

    private static void startA(Editable text, Attributes attributes) {
        String atKey = attributes.getValue("", "ukey"); // Can only be lower-case!!
        String atName = attributes.getValue("", "uname");
        String style = attributes.getValue("", "style");
        int atColor = Color.BLUE;
        if (style != null) {
            Matcher m = getForegroundColorPattern().matcher(style);
            if (m.find()) {
                atColor = getHtmlColor(m.group(1));
            }
        }

        if (!TextUtils.isEmpty(atKey)) {
            start(text, new At(atKey, atName, atColor));
            return;
        }
        String href = attributes.getValue("", "href");
        start(text, new Href(href));
    }

    private static void endA(Editable text) {
        At at = getLast(text, At.class);
        if (at != null) {
            AtItem atItem = new AtItem(at.mKey, at.mName, at.mColor);
            AtSpan atSpan = new AtSpan(atItem);
            setSpanFromMark(text, at, atSpan);
            return;
        }
        Href h = getLast(text, Href.class);
        if (h != null) {
            if (h.mHref != null) {
                setSpanFromMark(text, h, new UrlSpan((h.mHref)));
            }
        }
    }

//    private int getHtmlColor(String color) {
//        if ((mFlags & Html.FROM_HTML_OPTION_USE_CSS_COLORS)
//                == Html.FROM_HTML_OPTION_USE_CSS_COLORS) {
//            Integer i = sColorMap.get(color.toLowerCase(Locale.US));
//            if (i != null) {
//                return i;
//            }
//        }
//        return Color.getHtmlColor(color);
//    }

    @Override
    public void setDocumentLocator(Locator locator) {
    }

    @Override
    public void startDocument() throws SAXException {
    }

    @Override
    public void endDocument() throws SAXException {
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        handleStartTag(localName, attributes);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        handleEndTag(localName);
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        StringBuilder sb = new StringBuilder();

        /*
         * Ignore whitespace that immediately follows other whitespace;
         * newlines count as spaces.
         */

        for (int i = 0; i < length; i++) {
            char c = ch[i + start];

            if (c == ' ' || c == '\n') {
                char pred;
                int len = sb.length();

                if (len == 0) {
                    len = mSpannableStringBuilder.length();

                    if (len == 0) {
                        pred = '\n';
                    } else {
                        pred = mSpannableStringBuilder.charAt(len - 1);
                    }
                } else {
                    pred = sb.charAt(len - 1);
                }

                if (pred != ' ' && pred != '\n') {
                    sb.append(' ');
                }
            } else {
                sb.append(c);
            }
        }

        mSpannableStringBuilder.append(sb);
    }

    @Override
    public void ignorableWhitespace(char ch[], int start, int length) throws SAXException {
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
    }

    private static class At {
        public String mKey;
        public String mName;
        public int mColor;

        public At(String key, String name, int color) {
            mKey = key;
            mName = name;
            mColor = color;
        }
    }

    private static class Bold {
    }

    private static class Italic {
    }

    private static class Underline {
    }

    private static class Strikethrough {
    }

    private static class Big {
    }

    private static class Small {
    }

    private static class Monospace {
    }

    private static class Blockquote {
    }

    private static class Super {
    }

    private static class Sub {
    }

    private static class Bullet {
    }

    private static class Numeric {
    }

    private static class Font {
        public String mFace;

        public Font(String face) {
            mFace = face;
        }
    }

    private static class Href {
        public String mHref;

        public Href(String href) {
            mHref = href;
        }
    }

    private static class Foreground {
        private int mForegroundColor;

        public Foreground(int foregroundColor) {
            mForegroundColor = foregroundColor;
        }
    }

    private static class Background {
        private int mBackgroundColor;

        public Background(int backgroundColor) {
            mBackgroundColor = backgroundColor;
        }
    }

    private static class FontSize {
        private int mFontSize;

        public FontSize(int fontSize) {
            mFontSize = fontSize;
        }
    }

    private static class LineSpace {
        public float factor;

        public LineSpace(float factor) {
            this.factor = factor;
        }
    }

    private static class Heading {
        private int mLevel;

        public Heading(int level) {
            mLevel = level;
        }
    }

    private static class Newline {
        private int mNumNewlines;

        public Newline(int numNewlines) {
            mNumNewlines = numNewlines;
        }
    }

    private static class Alignment {
        private Layout.Alignment mAlignment;

        public Alignment(Layout.Alignment alignment) {
            mAlignment = alignment;
        }
    }

    private static class Indent {
        private int mIndentLength;

        public Indent(int indentLength) {
            mIndentLength = indentLength;
        }
    }

    private static class OL {
        private int level;

        public OL(int level) {
            this.level = level;
        }
    }

    private static class UL {
        private int level;

        public UL(int level) {
            this.level = level;
        }
    }

    private static class Todo {
        private boolean isCheck;

        public Todo(boolean isCheck) {
            this.isCheck = isCheck;
        }
    }

    private static Stack OL_UL_STACK = new Stack();


    private static HashMap<String, Integer> COLORS = buildColorMap();

    private static HashMap<String, Integer> buildColorMap() {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put("aqua", 0x00FFFF);
        map.put("black", 0x000000);
        map.put("blue", 0x0000FF);
        map.put("fuchsia", 0xFF00FF);
        map.put("green", 0x008000);
        map.put("grey", 0x808080);
        map.put("lime", 0x00FF00);
        map.put("maroon", 0x800000);
        map.put("navy", 0x000080);
        map.put("olive", 0x808000);
        map.put("purple", 0x800080);
        map.put("red", 0xFF0000);
        map.put("silver", 0xC0C0C0);
        map.put("teal", 0x008080);
        map.put("white", 0xFFFFFF);
        map.put("yellow", 0xFFFF00);
        return map;
    }

    /**
     * Converts an HTML color (named or numeric) to an integer RGB value.
     *
     * @param color Non-null color string.
     * @return A color value, or {@code -1} if the color string could not be
     * interpreted.
     */
    private static int getHtmlColor(String color) {
        Integer i = COLORS.get(color.toLowerCase());
        if (i != null) {
            return i;
        } else {
            try {
                return XmlUtils.convertValueToInt(color, -1);
            } catch (NumberFormatException nfe) {
                return -1;
            }
        }
    }

    /**
     * Returns the font size int value.
     *
     * @param fontSizePx like 32px
     * @return
     */
    private static int getFontSize(String fontSizePx) {
        if (TextUtils.isEmpty(fontSizePx)) {
            return 16;
        }
        int pxIndex = fontSizePx.indexOf("px");
        if (pxIndex < 0 || pxIndex > fontSizePx.length()) {
            return 16;
        }

        String fontSizeStr = fontSizePx.substring(0, pxIndex);
        try {
            return Integer.parseInt(fontSizeStr);
        } catch (NumberFormatException e) {
            return 16;
        }
    }

    private static int getIndentSize(String indentSizePx) {
        if (TextUtils.isEmpty(indentSizePx)) {
            return 0;
        }
        int pxIndex = indentSizePx.indexOf("px");
        if (pxIndex < 0 || pxIndex > indentSizePx.length()) {
            return 0;
        }

        String indentSizePxStr = indentSizePx.substring(0, pxIndex);
        try {
            return Integer.parseInt(indentSizePxStr);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static float getLineSpaceFactor(String lineSpaceFactor) {
        if (TextUtils.isEmpty(lineSpaceFactor)) {
            return 1;
        }
        try {
            return Float.parseFloat(lineSpaceFactor);
        } catch (NumberFormatException e) {
            return 1;
        }
    }
}
