package com.sophimp.are.utils;

public interface UndoRedoControl<T> {

    void redo(UndoRedoHelper.Action<T> action);

    void undo(UndoRedoHelper.Action<T> action);

}
