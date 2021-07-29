/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sophimp.are.inner;

//import com.android.internal.util.ArrayUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Layout;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.text.style.BulletSpan;
import android.text.style.CharacterStyle;
import android.text.style.ImageSpan;
import android.text.style.ParagraphStyle;
import android.text.style.QuoteSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;

import androidx.annotation.NonNull;

import com.sophimp.are.render.GlideResDrawable;
import com.sophimp.are.spans.FontBackgroundColorSpan;
import com.sophimp.are.spans.FontForegroundColorSpan;
import com.sophimp.are.spans.IListSpan;
import com.sophimp.are.spans.ISpan;
import com.sophimp.are.spans.IndentSpan;
import com.sophimp.are.spans.LineSpaceSpan;
import com.sophimp.are.spans.ListBulletSpan;
import com.sophimp.are.spans.ListNumberSpan;
import com.sophimp.are.spans.TodoSpan;

import org.ccil.cowan.tagsoup.HTMLSchema;
import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.XMLReader;

import java.text.DecimalFormat;
import java.util.Observer;

/**
 * This class processes HTML strings into displayable styled text.
 * Not all HTML tags are supported.
 */
public class Html {

    public static boolean escapeCJK = false;

    /**
     * should set value before use html
     */
    public static @NonNull
    Context sContext;

    public static final String OL = "ol";
    public static final String UL = "ul";
    public static final String TODO_LIST = "sgx-todolist";

//    public static int sListNumber = -1;

    /**
     * discover版本解析
     */
    public static boolean isDiscoverVersion;

    public static Observer videoThumbObserver;

    /**
     * 视频标签预览图有改变
     */
    public static Spanned fromHtml(String html, int fromHtmlSeparatorLineBreakParagraph, ImageGetter imageGetter, TagHandler tagHandler, boolean isNewVideoPreview) {
        Html.isDiscoverVersion = isNewVideoPreview;

        return fromHtml(html, fromHtmlSeparatorLineBreakParagraph, imageGetter, tagHandler);
    }

    /**
     * Retrieves images for HTML &lt;img&gt; tags.
     */
    public static interface ImageGetter {
        /**
         * This method is called when the HTML parser encounters an
         * &lt;img&gt; tag.  The <code>source</code> argument is the
         * string from the "src" attribute; the return value should be
         * a Drawable representation of the image or <code>null</code>
         * for a generic replacement image.  Make sure you call
         * setBounds() on your Drawable if it doesn't already have
         * its bounds set.
         */
        public GlideResDrawable getDrawable(String source, String width, String height, boolean isVideoDrawable);
    }

    /**
     * Is notified when HTML tags are encountered that the parser does
     * not know how to interpret.
     */
    public static interface TagHandler {
        /**
         * This method will be called whenn the HTML parser encounters
         * a tag that it does not know how to interpret.
         */
        public void handleTag(boolean opening, String tag,
                              Editable output, XMLReader xmlReader);
    }

    /**
     * Option for {@link #toHtml(Spanned, int)}: Wrap consecutive lines of text delimited by '\n'
     * inside &lt;p&gt; elements. {@link BulletSpan}s are ignored.
     */
    public static final int TO_HTML_PARAGRAPH_LINES_CONSECUTIVE = 0x00000000;

    /**
     * Option for {@link #toHtml(Spanned, int)}: Wrap each line of text delimited by '\n' inside a
     * &lt;p&gt; or a &lt;li&gt; element. This allows {@link ParagraphStyle}s attached to be
     * encoded as CSS styles within the corresponding &lt;p&gt; or &lt;li&gt; element.
     */
    public static final int TO_HTML_PARAGRAPH_LINES_INDIVIDUAL = 0x00000001;

    /**
     * Flag indicating that texts inside &lt;p&gt; elements will be separated from other texts with
     * one newline character by default.
     */
    public static final int FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH = 0x00000001;

    /**
     * Flag indicating that texts inside &lt;h1&gt;~&lt;h6&gt; elements will be separated from
     * other texts with one newline character by default.
     */
    public static final int FROM_HTML_SEPARATOR_LINE_BREAK_HEADING = 0x00000002;

    /**
     * Flag indicating that texts inside &lt;li&gt; elements will be separated from other texts
     * with one newline character by default.
     */
    public static final int FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM = 0x00000004;

    /**
     * Flag indicating that texts inside &lt;ul&gt; elements will be separated from other texts
     * with one newline character by default.
     */
    public static final int FROM_HTML_SEPARATOR_LINE_BREAK_LIST = 0x00000008;

    /**
     * Flag indicating that texts inside &lt;div&gt; elements will be separated from other texts
     * with one newline character by default.
     */
    public static final int FROM_HTML_SEPARATOR_LINE_BREAK_DIV = 0x00000010;

    /**
     * Flag indicating that texts inside &lt;blockquote&gt; elements will be separated from other
     * texts with one newline character by default.
     */
    public static final int FROM_HTML_SEPARATOR_LINE_BREAK_BLOCKQUOTE = 0x00000020;

    /**
     * Flag indicating that CSS color values should be used instead of those defined in
     * {@link Color}.
     */
    public static final int FROM_HTML_OPTION_USE_CSS_COLORS = 0x00000100;

    /**
     * Flags for {@link #fromHtml(String, int, ImageGetter, TagHandler)}: Separate block-level
     * elements with blank lines (two newline characters) in between. This is the legacy behavior
     * prior to N.
     */
    public static final int FROM_HTML_MODE_LEGACY = 0x00000000;

    /**
     * Flags for {@link #fromHtml(String, int, ImageGetter, TagHandler)}: Separate block-level
     * elements with line breaks (single newline character) in between. This inverts the
     * {@link Spanned} to HTML string conversion done with the option
     * {@link #TO_HTML_PARAGRAPH_LINES_INDIVIDUAL}.
     */
    public static final int FROM_HTML_MODE_COMPACT =
            FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
                    | FROM_HTML_SEPARATOR_LINE_BREAK_HEADING
                    | FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM
                    | FROM_HTML_SEPARATOR_LINE_BREAK_LIST
                    | FROM_HTML_SEPARATOR_LINE_BREAK_DIV
                    | FROM_HTML_SEPARATOR_LINE_BREAK_BLOCKQUOTE;

    /**
     * The bit which indicates if lines delimited by '\n' will be grouped into &lt;p&gt; elements.
     */
    private static final int TO_HTML_PARAGRAPH_FLAG = 0x00000001;

    private Html() {
    }

    /**
     * Returns displayable styled text from the provided HTML string with the legacy flags
     * {@link #FROM_HTML_MODE_LEGACY}.
     *
     * @deprecated use {@link #fromHtml(String, int)} instead.
     */
    @Deprecated
    public static Spanned fromHtml(String source) {
        return fromHtml(source, FROM_HTML_MODE_LEGACY, null, null);
    }

    /**
     * Returns displayable styled text from the provided HTML string. Any &lt;img&gt; tags in the
     * HTML will display as a generic replacement image which your program can then go through and
     * replace with real images.
     *
     * <p>This uses TagSoup to handle real HTML, including all of the brokenness found in the wild.
     */
    public static Spanned fromHtml(String source, int flags) {
        return fromHtml(source, flags, null, null);
    }

    /**
     * Lazy initialization holder for HTML parser. This class will
     * a) be preloaded by the zygote, or b) not loaded until absolutely
     * necessary.
     */
    public static class HtmlParser {
        public static final HTMLSchema schema = new HTMLSchema();
    }

    /**
     * Returns displayable styled text from the provided HTML string with the legacy flags
     * {@link #FROM_HTML_MODE_LEGACY}.
     *
     * @deprecated use {@link #fromHtml(String, int, ImageGetter, TagHandler)} instead.
     */
    @Deprecated
    public static Spanned fromHtml(String source, ImageGetter imageGetter, TagHandler tagHandler) {
        return fromHtml(source, FROM_HTML_MODE_LEGACY, imageGetter, tagHandler);
    }

    /**
     * Returns displayable styled text from the provided HTML string. Any &lt;img&gt; tags in the
     * HTML will use the specified ImageGetter to request a representation of the image (use null
     * if you don't want this) and the specified TagHandler to handle unknown tags (specify null if
     * you don't want this).
     *
     * <p>This uses TagSoup to handle real HTML, including all of the brokenness found in the wild.
     */
    public static Spanned fromHtml(String source, int flags, ImageGetter imageGetter,
                                   TagHandler tagHandler) {
        Parser parser = new Parser();
        try {
            HTMLSchema schema = HtmlParser.schema;
            schema.elementType(TODO_LIST, 1084794496, 288148, 0);
            schema.parent(TODO_LIST, "body");
            parser.setProperty(Parser.schemaProperty, schema);
        } catch (org.xml.sax.SAXNotRecognizedException e) {
            // Should not happen.
            throw new RuntimeException(e);
        } catch (org.xml.sax.SAXNotSupportedException e) {
            // Should not happen.
            throw new RuntimeException(e);
        }

        HtmlToSpannedConverter converter = new HtmlToSpannedConverter(source, imageGetter, tagHandler, parser, flags);
        return converter.convert();
    }

    /**
     * @deprecated use {@link #toHtml(Spanned, int)} instead.
     */
    @Deprecated
    public static String toHtml(Spanned text) {
        return toHtml(text, TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
    }

    /**
     * Returns an HTML representation of the provided Spanned text. A best effort is
     * made to add HTML tags corresponding to spans. Also note that HTML metacharacters
     * (such as "&lt;" and "&amp;") within the input text are escaped.
     *
     * @param text   input text to convert
     * @param option one of {@link #TO_HTML_PARAGRAPH_LINES_CONSECUTIVE} or
     *               {@link #TO_HTML_PARAGRAPH_LINES_INDIVIDUAL}
     * @return string containing input converted to HTML
     */
    public static String toHtml(Spanned text, int option) {
        StringBuilder out = new StringBuilder();
        withinHtml(out, text, option);
        return out.toString();
    }

    /**
     * Returns an HTML escaped representation of the given plain text.
     */
    public static String escapeHtml(CharSequence text) {
        StringBuilder out = new StringBuilder();
        withinStyle(out, text, 0, text.length());
        return out.toString();
    }

    private static void withinHtml(StringBuilder out, Spanned text, int option) {
        if ((option & TO_HTML_PARAGRAPH_FLAG) == TO_HTML_PARAGRAPH_LINES_CONSECUTIVE) {
            encodeTextAlignmentByDiv(out, text, option);
            return;
        }

        withinDiv(out, text, 0, text.length(), option);
    }

    @SuppressLint("NewApi")
    private static void encodeTextAlignmentByDiv(StringBuilder out, Spanned text, int option) {
        int len = text.length();

        int next;
        for (int i = 0; i < len; i = next) {
            next = text.nextSpanTransition(i, len, ParagraphStyle.class);
            ParagraphStyle[] style = text.getSpans(i, next, ParagraphStyle.class);
            String elements = " ";
            boolean needDiv = false;

            for (int j = 0; j < style.length; j++) {
                if (style[j] instanceof AlignmentSpan) {
                    Layout.Alignment align =
                            ((AlignmentSpan) style[j]).getAlignment();
                    needDiv = true;
                    if (align == Layout.Alignment.ALIGN_CENTER) {
                        elements = "align=\"center\" " + elements;
                    } else if (align == Layout.Alignment.ALIGN_OPPOSITE) {
                        elements = "align=\"right\" " + elements;
                    } else {
                        elements = "align=\"left\" " + elements;
                    }
                }
            }
            if (needDiv) {
                out.append("<div ").append(elements).append(">");
            }

            withinDiv(out, text, i, next, option);

            if (needDiv) {
                out.append("</div>");
            }
        }
    }

    private static void withinDiv(StringBuilder out, Spanned text, int start, int end,
                                  int option) {
        int next;
        for (int i = start; i < end; i = next) {
            next = text.nextSpanTransition(i, end, QuoteSpan.class);
            QuoteSpan[] quotes = text.getSpans(i, next, QuoteSpan.class);

            for (QuoteSpan quote : quotes) {
                out.append("<blockquote>");
            }

            withinBlockquote(out, text, i, next, option);

            for (QuoteSpan quote : quotes) {
                out.append("</blockquote>");
            }
        }
    }

    private static String getTextDirection(Spanned text, int start, int end) {
//        final int len = end - start;
//        final byte[] levels = ArrayUtils.newUnpaddedByteArray(len);
//        final char[] buffer = TextUtils.obtain(len);
//        TextUtils.getChars(text, start, end, buffer, 0);
//
//        int paraDir = AndroidBidi.bidi(Layout.DIR_REQUEST_DEFAULT_LTR, buffer, levels, len,
//                false /* no info */);
//    	int paraDir = Layout.DIR_LEFT_TO_RIGHT;
//
//        switch (paraDir) {
//          case Layout.DIR_RIGHT_TO_LEFT:
//            return "<p dir=\"rtl\">";
//          case Layout.DIR_LEFT_TO_RIGHT:
//          default:
//            return "<p>";
//        }
        return "";
    }

    @SuppressLint("NewApi")
    private static String getTextStyles(Spanned text, int start, int end,
                                        boolean forceNoVerticalMargin, boolean includeTextAlign) {
        String margin = null;
        String textAlign = null;
        String lineHeight = null;

        if (forceNoVerticalMargin) {
            // chinalwb:
            // Do not set margin for p tag for now.
            // margin = "margin-top:0; margin-bottom:0;";
        }
        if (includeTextAlign) {
            final AlignmentSpan[] alignmentSpans = text.getSpans(start, end, AlignmentSpan.class);

            for (int i = alignmentSpans.length - 1; i >= 0; i--) {
                AlignmentSpan s = alignmentSpans[i];
                final Layout.Alignment alignment = s.getAlignment();
                if (alignment == Layout.Alignment.ALIGN_NORMAL) {
                    textAlign = "text-align:left;";
                } else if (alignment == Layout.Alignment.ALIGN_CENTER) {
                    textAlign = "text-align:center;";
                } else if (alignment == Layout.Alignment.ALIGN_OPPOSITE) {
                    textAlign = "text-align:right;";
                }
            }
        }

        IndentSpan[] leadingMarginSpans = text.getSpans(start, end, IndentSpan.class);
        if (leadingMarginSpans != null && leadingMarginSpans.length > 0) {
            for (IndentSpan leadingMarginSpan : leadingMarginSpans) {
                margin = String.format("padding-left:%dpx;", (leadingMarginSpan.getMLevel() * IndentSpan.LEADING_MARGIN));
            }
        }

        LineSpaceSpan[] lineSpaceSpans = text.getSpans(start, end, LineSpaceSpan.class);
        if (lineSpaceSpans.length > 0) {
            DecimalFormat decimalFormat = new DecimalFormat(".00");
            String factor = decimalFormat.format(lineSpaceSpans[0].getFactor());
            lineHeight = String.format("line-height:%s;", factor);
        }

//        if (margin == null && textAlign == null && lineHeight == null) {
//            return "";
//        }

        final StringBuilder style = new StringBuilder(" style=\"");
        if (margin != null && textAlign != null) {
            style.append(margin).append(" ").append(textAlign);
        } else if (margin != null) {
            style.append(margin);
        } else if (textAlign != null) {
            style.append(textAlign);
        } else if (lineHeight != null) {
            style.append(lineHeight);
        }

        return style.append("\"").toString();
    }

    private static void withinBlockquote(StringBuilder out, Spanned text, int start, int end,
                                         int option) {
        if ((option & TO_HTML_PARAGRAPH_FLAG) == TO_HTML_PARAGRAPH_LINES_CONSECUTIVE) {
            withinBlockquoteConsecutive(out, text, start, end);
        } else {
            withinBlockquoteIndividual(out, text, start, end);
        }
    }

    private static void withinBlockquoteIndividual(StringBuilder out, Spanned text, int start, int end) {
        boolean isInList = false;
        int next;
        String listType = "";
        for (int i = start; i <= end; i = next) {
            next = TextUtils.indexOf(text, '\n', i, end);
            if (next < 0) {
                next = end;
            }

            if (next == i) {
                if (isInList && !TODO_LIST.equals(listType) && !TextUtils.isEmpty(listType)) {
                    // Current paragraph is no longer a list item; close the previously opened list
                    isInList = false;
                    out.append("</" + listType + ">");
                    listType = "";
                } else {
                    out.append("<br>");
                }
            } else {
                boolean isListItem = false;
                ParagraphStyle[] paragraphStyles = text.getSpans(i, next, ParagraphStyle.class);
                for (ParagraphStyle paragraphStyle : paragraphStyles) {
                    if (paragraphStyle instanceof IListSpan) {
                        boolean closed = false;
                        if (paragraphStyle instanceof ListNumberSpan) {
                            closed = checkToClosePreviousList(out, listType, OL);
                            listType = OL;
                        } else if (paragraphStyle instanceof ListBulletSpan) {
                            closed = checkToClosePreviousList(out, listType, UL);
                            listType = UL;
                        } else if (paragraphStyle instanceof TodoSpan) {
                            closed = checkToClosePreviousList(out, listType, TODO_LIST);
                            listType = TODO_LIST;
//                            out.append(((DRTodoSpan) paragraphStyle).getBeginHtml());

                            out.append("<" + listType).append(" ").append(((TodoSpan) paragraphStyle).getAttributeStr());
                        }

                        if (closed) {
                            // If the list item has been closed,
                            // It will no longer be in list.
                            // So set it as false then the following
                            // logic can start a new list item again
                            isInList = false;
                        }

                        isListItem = true;
                        break;
                    }
                }

                if (isListItem && !isInList) {
                    // Current paragraph is the first item in a list
                    isInList = true;

                    if ((OL.equals(listType) || UL.equals(listType))) {
                        out.append("<" + listType)
                                .append(getTextStyles(text, i, next, true, false))
                                .append(">");
                    } else if (TODO_LIST.equals(listType)) {
                        out.append(getTextStyles(text, i, next, true, true))
                                .append(">");
                    }
                }

                if (isInList && !isListItem) {
                    // Current paragraph is no longer a list item; close the previously opened list
                    isInList = false;

                    if ((OL.equals(listType) || UL.equals(listType))) {
                        out.append("</" + listType + ">");
                        listType = "";
                    }
                }

                //为何会有这一堆判断呢？ WEB端把todo列表当成行内元素去处理了，不懂得怎么改，故替他们兼容了这个问题，所以TODO_LIST 还要单独处理，不能作为列表的形式处理。
                String tagType = "p";
                if ((OL.equals(listType) || UL.equals(listType))) {
                    tagType = "li";
                } else if (TODO_LIST.equals(listType)) {
                    tagType = "";
                }


                if (!TextUtils.isEmpty(tagType)) {
                    out.append("<").append(tagType)
                            .append(getTextDirection(text, i, next))
                            .append(getTextStyles(text, i, next, !isListItem, true))
                            .append(">");
                }


                withinParagraph(out, text, i, next);

                if (!TextUtils.isEmpty(tagType)) {
                    out.append("</");
                    out.append(tagType);
                    out.append(">");
                } else {
                    out.append("</");
                    out.append(listType);
                    out.append(">");
                    listType = "";
                    isInList = false;
                }


                if (next == end && isInList && !TODO_LIST.equals(listType) && !TextUtils.isEmpty(listType)) {
                    isInList = false;
                    out.append("</" + listType + ">");
                    listType = "";
                }
            }

            next++;
        }
    }

    private static boolean checkToClosePreviousList(StringBuilder out, String srcListType, String targetListType) {
        if (!srcListType.equals(targetListType) && !TextUtils.isEmpty(srcListType)) {
            out.append("</" + srcListType + ">");
            return true;
        }
        return false;
    }

    private static void withinBlockquoteConsecutive(StringBuilder out, Spanned text, int start,
                                                    int end) {
        out.append("<p").append(getTextDirection(text, start, end)).append(">");

        int next;
        for (int i = start; i < end; i = next) {
            next = TextUtils.indexOf(text, '\n', i, end);
            if (next < 0) {
                next = end;
            }

            int nl = 0;

            while (next < end && text.charAt(next) == '\n') {
                nl++;
                next++;
            }

            withinParagraph(out, text, i, next - nl);

            if (nl == 1) {
                out.append("<br>");
            } else {
                for (int j = 2; j < nl; j++) {
                    out.append("<br>");
                }
                if (next != end) {
                    /* Paragraph should be closed and reopened */
                    out.append("</p>");
                    out.append("<p").append(getTextDirection(text, start, end)).append(">");
                }
            }
        }

        out.append("</p>");
    }

    private static void withinParagraph(StringBuilder out, Spanned text, int start, int end) {
        int next;
        for (int i = start; i < end; i = next) {
            next = text.nextSpanTransition(i, end, CharacterStyle.class);
            CharacterStyle[] style = text.getSpans(i, next, CharacterStyle.class);

            for (int j = 0; j < style.length; j++) {
                if (style[j] instanceof ISpan) {
                    out.append(((ISpan) style[j]).getHtml());
                    i = next;
                    continue;
                }
                if (style[j] instanceof StyleSpan) {
                    int s = ((StyleSpan) style[j]).getStyle();

                    if ((s & Typeface.BOLD) != 0) {
                        out.append("<b>");
                    }
                    if ((s & Typeface.ITALIC) != 0) {
                        out.append("<i>");
                    }
                }
                if (style[j] instanceof TypefaceSpan) {
                    String s = ((TypefaceSpan) style[j]).getFamily();

                    if ("monospace".equals(s)) {
                        out.append("<tt>");
                    }
                }
                if (style[j] instanceof SuperscriptSpan) {
                    out.append("<sup>");
                }
                if (style[j] instanceof SubscriptSpan) {
                    out.append("<sub>");
                }
//                if (style[j] instanceof UnderlineSpan) {
//                    out.append("<u>");
//                }

                if (style[j] instanceof UnderlineSpan) {
                    out.append("<span style=\"text-decoration:underline;\">");
                }

                if (style[j] instanceof StrikethroughSpan) {
                    out.append("<span style=\"text-decoration:line-through;\">");
                }

                if (style[j] instanceof URLSpan) {
                    out.append("<a href=\"");
                    out.append(((URLSpan) style[j]).getURL());
                    out.append("\">");
                }
                if (style[j] instanceof ImageSpan) {
                    out.append("<img src=\"");
                    out.append(((ImageSpan) style[j]).getSource());
                    out.append("\" />");

                    // Don't output the dummy character underlying the image.
                    i = next;
                }
                if (style[j] instanceof AbsoluteSizeSpan) {
                    AbsoluteSizeSpan s = ((AbsoluteSizeSpan) style[j]);
                    float sizeDip = s.getSize();
                    if (!s.getDip()) {
                        // Application application = ActivityThread.currentApplication();
                        // float density = application.getResources().getDisplayMetrics().density;
                        float density = 1.5f;
                        sizeDip /= density;
                    }

                    // px in CSS is the equivalance of dip in Android
                    out.append(String.format("<span style=\"font-size:%.0fpx;\">", sizeDip));
                }
                if (style[j] instanceof RelativeSizeSpan) {
                    float sizeEm = ((RelativeSizeSpan) style[j]).getSizeChange();
                    out.append(String.format("<span style=\"font-size:%.2fem;\">", sizeEm));
                }
                if (style[j] instanceof FontForegroundColorSpan) {
                    int color = ((FontForegroundColorSpan) style[j]).getForegroundColor();
                    out.append(String.format("<span style=\"color:#%06X;\">", 0xFFFFFF & color));
                }
                if (style[j] instanceof FontBackgroundColorSpan) {
                    int color = ((FontBackgroundColorSpan) style[j]).getBackgroundColor();
                    if (color != 0) {
                        out.append(String.format("<span style=\"background-color:#%06X;\">", 0xFFFFFF & color));
                    }
                }
            }

            withinStyle(out, text, i, next);

            for (int j = style.length - 1; j >= 0; j--) {
                if (style[j] instanceof FontBackgroundColorSpan) {
                    out.append("</span>");
                }
                if (style[j] instanceof FontForegroundColorSpan) {
                    out.append("</span>");
                }
                if (style[j] instanceof RelativeSizeSpan) {
                    out.append("</span>");
                }
                if (style[j] instanceof AbsoluteSizeSpan) {
                    out.append("</span>");
                }
                if (style[j] instanceof URLSpan) {
                    out.append("</a>");
                }
                if (style[j] instanceof StrikethroughSpan) {
                    out.append("</span>");
                }
                if (style[j] instanceof UnderlineSpan) {
                    out.append("</span>");
                }
                if (style[j] instanceof SubscriptSpan) {
                    out.append("</sub>");
                }
                if (style[j] instanceof SuperscriptSpan) {
                    out.append("</sup>");
                }
                if (style[j] instanceof TypefaceSpan) {
                    String s = ((TypefaceSpan) style[j]).getFamily();

                    if ("monospace".equals(s)) {
                        out.append("</tt>");
                    }
                }
                if (style[j] instanceof StyleSpan) {
                    int s = ((StyleSpan) style[j]).getStyle();

                    if ((s & Typeface.BOLD) != 0) {
                        out.append("</b>");
                    }
                    if ((s & Typeface.ITALIC) != 0) {
                        out.append("</i>");
                    }
                }
            }
        }
    }

    private static void withinStyle(StringBuilder out, CharSequence text,
                                    int start, int end) {
        for (int i = start; i < end; i++) {
            char c = text.charAt(i);

            if (c == '<') {
                out.append("&lt;");
            } else if (c == '>') {
                out.append("&gt;");
            } else if (c == '&') {
                out.append("&amp;");
            } else if (c >= 0xD800 && c <= 0xDFFF) {
                if (c < 0xDC00 && i + 1 < end) {
                    char d = text.charAt(i + 1);
                    if (d >= 0xDC00 && d <= 0xDFFF) {
                        i++;
                        int codepoint = 0x010000 | (int) c - 0xD800 << 10 | (int) d - 0xDC00;
                        out.append("&#").append(codepoint).append(";");
                    }
                }
            } else if (c > 0x7E || c < ' ') {
                if (escapeCJK) {
                    out.append("&#").append((int) c).append(";");
                } else {
                    out.append(c);
                }
            } else if (c == ' ') {
                while (i + 1 < end && text.charAt(i + 1) == ' ') {
                    out.append("&nbsp;");
                    i++;
                }

                out.append(' ');
            } else {
                out.append(c);
            }
        }
    }
}


