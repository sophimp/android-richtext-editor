package com.sophimp.are.table;

import com.sophimp.are.RichEditText;

/**
 * 当前Cell取焦监听
 */
public interface OnCellFocusListener {
    void onCellFocus(RichEditText editText, int row, int col);
}
