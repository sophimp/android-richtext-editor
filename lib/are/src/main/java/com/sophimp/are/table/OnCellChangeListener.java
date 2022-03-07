package com.sophimp.are.table;

import com.sophimp.are.utils.UndoRedoHelper;

/**
 * @des:
 * @since: 2021/7/2
 * @version:
 * @author: sfx
 */
public interface OnCellChangeListener {
    default void beforeCellChange(UndoRedoHelper.Action action) {
    }

    ;

    default void beforeCellChange(UndoRedoHelper.Action action, int row, int col) {
    }

    ;

    default void afterCellChange(UndoRedoHelper.Action action, int row, int col) {
    }

    ;

    default void onCellHeightChange(int height) {
    }

    ;
}
