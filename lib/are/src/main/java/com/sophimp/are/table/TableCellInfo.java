package com.sophimp.are.table;

import android.text.Layout;

/**
 * each cell dataclass
 */
public class TableCellInfo {
    public String richText = "";

    /**
     * 默认左对齐
     */
    public Layout.Alignment alignment = Layout.Alignment.ALIGN_NORMAL;

    public int width;
    /**
     * 自身测量高度
     */
    public int cellHeight;

    /**
     * 所在行高
     */
    public int rowHeight;

    /**
     * 用于undo, redo
     */
    public boolean requestFocus;

    /**
     * 用于undo, redo
     */
    public int cursorSelectionStart;
    public int cursorSelectionEnd;

    public TableCellInfo(int cellWidth, int defHeight) {
        width = cellWidth;
        cellHeight = defHeight;
        rowHeight = defHeight;
    }

    public TableCellInfo(String text) {
        richText = text;
    }

    public TableCellInfo(String richText, Layout.Alignment alignment) {
        this.richText = richText;
        this.alignment = alignment;
    }

    public TableCellInfo(Layout.Alignment alignment) {
        this.alignment = alignment;
    }
}
