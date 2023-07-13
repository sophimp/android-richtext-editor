package com.sophimp.are.utils

/*
 * Copyright (C) 2018 The Android Open Source Project
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

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Layout
import android.text.Spanned
import com.sophimp.are.spans.FontBackgroundColorSpan
import com.sophimp.are.spans.SearchHighlightSpan

/**
 * Helper class to draw multi-line rounded background to certain parts of a text. The start/end
 * positions of the backgrounds are annotated with [android.text.Annotation] class. Each annotation
 * should have the annotation key set to **rounded**.
 *
 * i.e.:
 * ```
 *    <!--without the quotes at the begining and end Android strips the whitespace and also starts
 *        the annotation at the wrong position-->
 *    <string name="ltr">"this is <annotation key="rounded">a regular</annotation> paragraph."</string>
 * ```
 *
 * **Note:** BiDi text is not supported.
 *
 * @param horizontalPadding the padding to be applied to left & right of the background
 * @param verticalPadding the padding to be applied to top & bottom of the background
 * @param drawable the drawable used to draw the background
 * @param drawableLeft the drawable used to draw left edge of the background
 * @param drawableMid the drawable used to draw for whole line
 * @param drawableRight the drawable used to draw right edge of the background
 */
class TextRoundedBgHelper(
    val horizontalPadding: Int,
    verticalPadding: Int,
//    drawable: Drawable,
//    drawableLeft: Drawable,
//    drawableMid: Drawable,
//    drawableRight: Drawable
) {

    private val bgDrawable: ColorDrawable = ColorDrawable(Color.TRANSPARENT)

    private val singleLineRenderer: SingleLineRenderer by lazy {
        SingleLineRenderer(
            horizontalPadding = horizontalPadding,
            verticalPadding = verticalPadding,
            drawable = bgDrawable
        )
    }

    private val multiLineRenderer: MultiLineRenderer by lazy {
        MultiLineRenderer(
            horizontalPadding = horizontalPadding,
            verticalPadding = verticalPadding,
            drawableLeft = bgDrawable,
            drawableMid = bgDrawable,
            drawableRight = bgDrawable
        )
    }

    /**
     * Call this function during onDraw of another widget such as TextView.
     *
     * @param canvas Canvas to draw onto
     * @param text
     * @param layout Layout that contains the text
     */
    fun drawFontBg(canvas: Canvas, text: Spanned, layout: Layout) {
        // ideally the calculations here should be cached since they are not cheap. However, proper
        // invalidation of the cache is required whenever anything related to text has changed.
        val spans = text.getSpans(0, text.length, FontBackgroundColorSpan::class.java)
        spans.forEach { span ->
            val spanStart = text.getSpanStart(span)
            val spanEnd = text.getSpanEnd(span)
            val startLine = layout.getLineForOffset(spanStart)
            val endLine = layout.getLineForOffset(spanEnd)
            if (spanStart > text.length) return

            // start can be on the left or on the right depending on the language direction.
            val startOffset = (layout.getPrimaryHorizontal(spanStart)
                    + -1 * layout.getParagraphDirection(startLine) * horizontalPadding).toInt()
            // end can be on the left or on the right depending on the language direction.
            val endOffset = (layout.getPrimaryHorizontal(spanEnd)
                    + layout.getParagraphDirection(endLine) * horizontalPadding).toInt()

//            bgDrawable.color = Color.parseColor(span.dynamicFeature)
            bgDrawable.color = span.mColor
            val renderer = if (startLine == endLine) singleLineRenderer else multiLineRenderer
            renderer.draw(canvas, layout, startLine, endLine, startOffset, endOffset)
        }
    }

    fun drawSearchHighlight(canvas: Canvas, bgSpans: List<SearchHighlightSpan>, text: Spanned, layout: Layout) {
        bgSpans.forEach { span ->
            val spanStart = text.getSpanStart(span)
            val spanEnd = text.getSpanEnd(span)
            val startLine = layout.getLineForOffset(spanStart)
            val endLine = layout.getLineForOffset(spanEnd)

            // start can be on the left or on the right depending on the language direction.
            val startOffset = (layout.getPrimaryHorizontal(spanStart)
                    + -1 * layout.getParagraphDirection(startLine) * horizontalPadding).toInt()
            // end can be on the left or on the right depending on the language direction.
            val endOffset = (layout.getPrimaryHorizontal(spanEnd)
                    + layout.getParagraphDirection(endLine) * horizontalPadding).toInt()

//            bgDrawable.color = Color.parseColor(span.dynamicFeature)
            bgDrawable.color = span.mColor
            val renderer = if (startLine == endLine) singleLineRenderer else multiLineRenderer
            renderer.draw(canvas, layout, startLine, endLine, startOffset, endOffset)
        }
    }
}