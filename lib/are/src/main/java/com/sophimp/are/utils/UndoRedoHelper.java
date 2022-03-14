package com.sophimp.are.utils;

import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.sophimp.are.RichEditText;
import com.sophimp.are.table.EditTableViewModel;
import com.sophimp.are.table.TableCellInfo;

import java.lang.reflect.ParameterizedType;
import java.util.AbstractMap;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;


public class UndoRedoHelper {

    private final ToggleStyleObserver toggleStyleObserver = new ToggleStyleObserver();
    private EditTableViewModel tableViewModel;

    //操作序号(一次编辑可能对应多个操作，如替换文字，就是删除+插入)
    private int index;

    //撤销栈
//    private Deque<Action> history = new LinkedList<>();
//    //恢复栈
//    private Deque<Action> historyBack = new LinkedList<>();

    //撤销栈
    private Deque<Action> historyE = new LinkedList<>();
    //恢复栈
    private Deque<Action> historyBackE = new LinkedList<>();

    private Editable editable;
    private RichEditText editText;
    //    private RichUtils richUtils;
    //自动操作标志，防止重复回调,导致无限撤销
    private boolean flag = false;

    private View undoView;
    private View redoView;

//    private boolean isFirst = true;

    public UndoRedoHelper(@NonNull RichEditText editText) {
        CheckNull(editText, "EditText不能为空");
        this.editable = editText.getText();
        this.editText = editText;
        editText.addTextChangedListener(new Watcher());

        // 注册样式修改观察者到被观察中，当样式修改的时候，会通知当前对象将action插入到撤销栈中。
        editText.setStyleChangedListener(drEditText -> handleStyleChanged(null));
    }

    public UndoRedoHelper(EditTableViewModel tableViewModel) {
        this.tableViewModel = tableViewModel;
    }

    protected void onEditableChanged(Editable s) {
    }

    protected void onTextChanged(Editable s) {
    }

    public void onHistoryChange() {

        if (undoView != null) {
            undoView.setSelected(!historyE.isEmpty());
        }

        if (redoView != null) {
            redoView.setSelected(!historyBackE.isEmpty());
        }
    }

    public void undoTable() {
        if (!canUndo()) return;
        Action action = historyE.pop();
        // 对action反过来操作, 并将action添加到redo栈
        historyBackE.push(action);
        switch (action.actionType) {
            case UndoRedoActionTypeEnum.ADD_COL:
                tableViewModel.delCol(action.col);
                break;
            case UndoRedoActionTypeEnum.DEL_COL:
                tableViewModel.addCol(action.col, action.actionCells);
                break;
            case UndoRedoActionTypeEnum.ADD_ROW:
                tableViewModel.delRow(action.row);
                break;
            case UndoRedoActionTypeEnum.DEL_ROW:
                tableViewModel.addRow(action.row, action.actionCells);
                break;
            default:
                tableViewModel.notifyItemChanged(action);
                break;
        }
    }

    public void redoTable() {
        if (!canRedo()) return;
        Action action = historyBackE.pop();
        historyE.push(action);
        switch (action.actionType) {
            case UndoRedoActionTypeEnum.ADD_COL:
                tableViewModel.addCol(action.col, action.actionCells);
                break;
            case UndoRedoActionTypeEnum.DEL_COL:
                tableViewModel.delCol(action.col);
                break;
            case UndoRedoActionTypeEnum.ADD_ROW:
                tableViewModel.addRow(action.row, action.actionCells);
                break;
            case UndoRedoActionTypeEnum.DEL_ROW:
                tableViewModel.delRow(action.row);
                break;
            default:
                tableViewModel.notifyItemChanged(action);
                break;
        }
    }

    public void setUndoRedoView(View undoView, View redoView) {
        this.undoView = undoView;
        this.redoView = redoView;

        if (undoView != null) {
            undoView.setOnClickListener(v -> {
                undo();
            });
        }

        if (redoView != null) {
            redoView.setOnClickListener(v -> {
                redo();
            });
        }

    }

    /**
     * 清理记录
     * Clear history.
     */
    public final void clearHistory() {
        historyE.clear();
        historyBackE.clear();
    }

    /**
     * 撤销修改样式
     */
    private void undoChangeStyle(Action action) {
        if (action == null || action.styleControl == null) {
            return;
        }
        action.styleControl.undo(action);

    }

    private void redoChangeStyle(Action action) {

        if (action == null || action.styleControl == null) {
            return;
        }
        action.styleControl.redo(action);

    }

    /**
     * 撤销
     * Undo.
     */
    public final void undo() {
        if (historyE.isEmpty()) {
            return;
        }

        //锁定操作
        flag = true;
        editText.stopMonitor();
        Action action = historyE.pop();

        if (historyBackE.isEmpty() && action.actionTarget.toString().equals(editText.getEditableText().toString()) && historyE.size() > 0) {
            historyBackE.push(action);
            Action action1 = historyE.pop();
            historyBackE.push(action1);
            editText.setText(action1.actionTarget);
            editText.setSelection(action1.startCursor, action1.endCursor);
        } else {
            historyBackE.push(action);
            editText.setText(action.actionTarget);
            editText.setSelection(action.startCursor, action.endCursor);
        }
        editText.startMonitor();
        flag = false;

        onHistoryChange();
    }

    /**
     * 恢复
     * Redo.
     */
    public final void redo() {
        if (historyBackE.isEmpty()) {
            return;
        }

        flag = true;
        editText.stopMonitor();

        Action action = historyBackE.pop();
        if (action.actionTarget.toString().equals(editText.getEditableText().toString()) && historyBackE.size() > 0) {
            historyE.push(action);
            Action action1 = historyBackE.pop();
            historyE.push(action1);
            editText.setText(action1.actionTarget);
            editText.setSelection(action1.startCursor, action1.endCursor);
        } else {
            historyE.push(action);
            editText.setText(action.actionTarget);
            editText.setSelection(action.startCursor, action.endCursor);
        }

        flag = false;
        editText.startMonitor();
        onHistoryChange();
    }

    /**
     * 首次设置文本
     * Set default text.
     */
    public final void setDefaultText(CharSequence text) {
        clearHistory();
        flag = true;
        editable.replace(0, editable.length(), text);
        flag = false;
    }

    public void addHistoryAction(Action action) {
        historyE.push(action);
    }

    /**
     * 当样式发生变化回调
     */
    private void handleStyleChanged(Action action) {
//        action.setIndex(++index);
//        history.push(action);
//        historyBack.clear();
        int selectionStart = editText.getSelectionStart();
        int selectionEnd = editText.getSelectionEnd();
        CharSequence s = editText.getEditableText();
        if (s != null) {
            CharSequence editable = s.subSequence(0, s.length());
            Action action1 = new Action(editable, selectionStart, selectionEnd);
            historyE.push(action1);
        } else {
            Action action1 = new Action(s, selectionStart, selectionEnd);
            historyE.push(action1);
        }
        historyBackE.clear();

        onHistoryChange();
    }

    public boolean canUndo() {
        return historyE.size() > 0;
    }

    public boolean canRedo() {
        return historyBackE.size() > 0;
    }

    private class Watcher implements TextWatcher {

        /**
         * Before text changed.
         *
         * @param s     the s
         * @param start the start 起始光标
         * @param count the endCursor 选择数量
         * @param after the after 替换增加的文字数
         */
        @Override
        public final void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (flag) return;
            if (!editText.getCanMonitor()) {
                return;
            }

            if (historyE.isEmpty()) {
                CharSequence editable = s.subSequence(0, s.length());
                Action action = new Action(editable, start, start + count);
                historyE.push(action);
            }
        }

        /**
         * On text changed.
         *
         * @param s      the s
         * @param start  the start 起始光标
         * @param before the before 选择数量
         * @param count  the endCursor 添加的数量
         */
        @Override
        public final void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public final void afterTextChanged(Editable s) {

            if (flag) return;
            if (!editText.getCanMonitor()) {
                return;
            }
            int selectionStart = editText.getSelectionStart();
            int selectionEnd = editText.getSelectionEnd();


            CharSequence editable = s.subSequence(0, s.length());
            Action action = new Action(editable, selectionStart, selectionEnd);

            historyE.push(action);
            historyBackE.clear();
            onHistoryChange();

        }

    }

    public static class Action<T> {
        /**
         * 改变字符.
         */
        public CharSequence actionTarget;

        public List<TableCellInfo> actionCells;

        /**
         * 光标位置.
         */
        public int startCursor;
        public int endCursor;

        /**
         * 类型：增加、删除、修改样式
         */
        @UndoRedoActionTypeEnum
        public String actionType;

        public UndoRedoControl styleControl;

        public List<HistorySpan<T>> removeSpans;
        public List<HistorySpan<T>> insertSpans;

        public int col;
        public int row;

        /**
         * 操作序号.
         */
        int index;

//        Class<T> clazzT;


        /**
         * 用于表格cell的修改
         */
        public Action(CharSequence actionTarget, int startCursor, int endCursor, @UndoRedoActionTypeEnum String actionType, int col, int row) {
            this.actionTarget = actionTarget;
            this.startCursor = startCursor;
            this.endCursor = endCursor;
            this.actionType = actionType;
            this.col = col;
            this.row = row;
        }

        public Action(CharSequence actionTarget, int startCursor, int endCursor) {
            this.actionTarget = actionTarget;
            this.startCursor = startCursor;
            this.endCursor = endCursor;
        }

        public Action(CharSequence actionTag, int startCursor, String actionType) {
            this.actionTarget = actionTag;
            this.startCursor = startCursor;
            this.endCursor = startCursor;
            this.actionType = actionType;
        }

        public Action(int startCursor, int endCursor, UndoRedoControl styleControl, List<HistorySpan<T>> removeSpans, List<HistorySpan<T>> insertSpans) {
            this.startCursor = startCursor;
            this.endCursor = endCursor;
            this.actionType = UndoRedoActionTypeEnum.CHANGE;
            this.styleControl = styleControl;
            this.removeSpans = removeSpans;
            this.insertSpans = insertSpans;

//            clazzT = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        }

        /**
         * 用于表格的行列增删
         */
        public Action(List<TableCellInfo> cells, @UndoRedoActionTypeEnum String actionType, int row, int col) {
            this.actionCells = cells;
            this.actionType = actionType;
            this.row = row;
            this.col = col;
        }

        public void setSelectCount(int count) {
            this.endCursor = endCursor + count;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public Class<T> getTClass() {
            Class<T> tClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            return tClass;
        }
    }

    public static class HistorySpan<T> {

        public int start;
        public int end;
        public T span;

        public HistorySpan(int start, int end, T span) {
            this.start = start;
            this.end = end;
            this.span = span;
        }
    }


    private static void CheckNull(Object o, String message) {
        if (o == null) throw new IllegalStateException(message);
    }

    public class ToggleStyleObserver {
        public void onChange(Action action) {
            handleStyleChanged(action);
        }
    }

    /**
     * @param editText
     * @param action
     */
    public static <T> void commonCharacterStyleRedo(EditText editText, Action<T> action) {
        if (editText == null) return;
        if (action == null) return;

        int startCursor = action.startCursor;
        int endCursor = action.endCursor;
        List<HistorySpan<T>> insertSpans = action.insertSpans;

        if (startCursor < 0) {
            startCursor = 0;
        }
        if (editText.getText() == null) {
            endCursor = 0;
        } else if (editText.getText().length() <= endCursor) {
            endCursor = editText.getText().length() - 1;
        }

        editText.setSelection(startCursor, endCursor);
        T[] spans = editText.getText().getSpans(startCursor, endCursor, action.getTClass());


        if (spans != null && spans.length > 0) {
            for (int i = 0; i < spans.length; i++) {
                T span = spans[i];
                editText.getText().removeSpan(span);
            }
        }

        if (insertSpans != null && insertSpans.size() > 0) {

            for (HistorySpan<T> insertSpan : insertSpans) {

                int start = insertSpan.start;
                int end = insertSpan.end;
                Editable text = editText.getText();
                if (text == null) {
                    continue;
                }

                if (start < 0) {
                    start = 0;
                }

                if (text.length() <= end) {
                    end = text.length() - 1;
                }

                if (start > end) {
                    start = end;
                }
                text.setSpan(insertSpan.span, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
    }

    public static <T> void commonCharacterStyleUndo(EditText editText, Action<T> action) {

        if (editText == null) return;
        if (action == null) return;

        int startCursor = action.startCursor;
        int endCursor = action.endCursor;
        List<HistorySpan<T>> removeSpans = action.removeSpans;

        if (startCursor < 0) {
            startCursor = 0;
        }
        if (editText.getText() == null) {
            endCursor = 0;
        } else if (editText.getText().length() <= endCursor) {
            endCursor = editText.getText().length() - 1;
        }

        editText.setSelection(startCursor, endCursor);
        T[] spans = editText.getText().getSpans(startCursor, endCursor, action.getTClass());
        if (spans != null && spans.length > 0) {
            for (int i = 0; i < spans.length; i++) {
                T span = spans[i];
                editText.getText().removeSpan(span);
            }
        }

        if (removeSpans != null && removeSpans.size() > 0) {

            for (HistorySpan<T> removeSpan : removeSpans) {

                int start = removeSpan.start;
                int end = removeSpan.end;
                Editable text = editText.getText();
                if (text == null) {
                    continue;
                }

                if (start < 0) {
                    start = 0;
                }

                if (text.length() <= end) {
                    end = text.length() - 1;
                }

                if (start > end) {
                    start = end;
                }
                text.setSpan(removeSpan.span, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
    }

    public static <T> void commonParagraphStyleUndo(EditText editText, Action<T> action) {

        if (editText == null) return;
        if (action == null) return;

        int startCursor = action.startCursor;
        int endCursor = action.endCursor;
        List<HistorySpan<T>> removeSpans = action.removeSpans;
        if (startCursor < 0) {
            startCursor = 0;
        }
        if (editText.getText() == null) {
            endCursor = 0;
        } else if (editText.getText().length() <= endCursor) {
            endCursor = editText.getText().length() - 1;
        }

        editText.setSelection(startCursor, endCursor);

        List<AbstractMap.SimpleEntry<Integer, Integer>> currentSelectionLines = Util.getAllSelectionLines(editText);

        if (currentSelectionLines == null || currentSelectionLines.size() == 0) {

            int currentLine = Util.getCurrentCursorLine(editText);
            int start = Util.getThisLineStart(editText, currentLine);
            int end = Util.getThisLineEnd(editText, currentLine);
            T[] spans = editText.getText().getSpans(start, end, action.getTClass());
            if (null != spans) {
                for (T span : spans) {
                    editText.getText().removeSpan(span);
                }
            }
        } else {
            for (AbstractMap.SimpleEntry<Integer, Integer> currentSelectionLine : currentSelectionLines) {
                int start = currentSelectionLine.getKey();
                int end = currentSelectionLine.getValue();


                T[] spans = editText.getText().getSpans(start, end, action.getTClass());
                if (null != spans) {
                    for (T span : spans) {
                        editText.getText().removeSpan(span);
                    }
                }
            }
        }

        if (removeSpans != null && removeSpans.size() > 0) {

            for (HistorySpan<T> removeSpan : removeSpans) {
                int start = removeSpan.start;
                int end = removeSpan.end;
                Editable text = editText.getText();
                if (text == null) {
                    continue;
                }

                if (start < 0) {
                    start = 0;
                }

                if (text.length() <= end) {
                    end = text.length() - 1;
                }

                if (start > end) {
                    start = end;
                }

                text.setSpan(removeSpan.span, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

            }

        }
    }

    public static <T> void commonParagraphStyleRedo(EditText editText, Action<T> action) {

        if (editText == null) return;
        if (action == null) return;

        int startCursor = action.startCursor;
        int endCursor = action.endCursor;
        List<HistorySpan<T>> insertSpans = action.insertSpans;
        if (startCursor < 0) {
            startCursor = 0;
        }
        if (editText.getText() == null) {
            endCursor = 0;
        } else if (editText.getText().length() <= endCursor) {
            endCursor = editText.getText().length() - 1;
        }

        editText.setSelection(startCursor, endCursor);

        List<AbstractMap.SimpleEntry<Integer, Integer>> currentSelectionLines = Util.getAllSelectionLines(editText);

        if (currentSelectionLines.size() == 0) {

            int currentLine = Util.getCurrentCursorLine(editText);
            int start = Util.getThisLineStart(editText, currentLine);
            int end = Util.getThisLineEnd(editText, currentLine);
            T[] spans = editText.getText().getSpans(start, end, action.getTClass());
            if (null != spans) {
                for (T span : spans) {
                    editText.getText().removeSpan(span);
                }
            }
        } else {
            for (AbstractMap.SimpleEntry<Integer, Integer> currentSelectionLine : currentSelectionLines) {
                int start = currentSelectionLine.getKey();
                int end = currentSelectionLine.getValue();


                T[] spans = editText.getText().getSpans(start, end, action.getTClass());
                if (null != spans) {
                    for (T span : spans) {
                        editText.getText().removeSpan(span);
                    }
                }
            }
        }

        if (insertSpans != null && insertSpans.size() > 0) {

            for (HistorySpan<T> removeSpan : insertSpans) {
                int start = removeSpan.start;
                int end = removeSpan.end;
                Editable text = editText.getText();
                if (text == null) {
                    continue;
                }

                if (start < 0) {
                    start = 0;
                }

                if (text.length() <= end) {
                    end = text.length() - 1;
                }

                if (start > end) {
                    start = end;
                }

                text.setSpan(removeSpan.span, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }

    }

}
