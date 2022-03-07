package com.sophimp.are.table;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sophimp.are.utils.UndoRedoActionTypeEnum;
import com.sophimp.are.utils.UndoRedoHelper;
import com.sophimp.are.utils.Util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 可编辑表格数据源处理
 */
public class EditTableViewModel extends ViewModel implements IEditTableView {
    List<List<TableCellInfo>> dataSource;
    private int cellWidth;
    private int cellMinWidth = -2;
    private int cellMaxWidth = -2;

    /**
     * 行列变化
     */
    private int col = 3, row = 3;

    public @interface RefreshEvent {
        int ADD_OR_DELETE_ROW = 0;
        int ADD_OR_DELETE_COL = 1;
        int REFRESH_ROW = 2;
        int REFRESH_COL = 3;
    }

    /**
     * 刷新位置
     * int[0] : 刷新事件 {@link RefreshEvent}
     * 0: 增删行
     * 1: 增删列
     * 2: 刷新行
     * 3: 刷新列
     * int[1] : 变更所在行
     * int[2] : 变更所在列
     */
    public MutableLiveData<int[]> refreshRow = new MutableLiveData();

    private int tableShowWidth;
    public int defHeight;

    public void init(Context ctx) {
        // 计算出cell的最大长度与最小长度, 去掉两边边距16*2 + 列菜单的长度 20
        tableShowWidth = Util.getScreenWidth(ctx) - Util.dip2px(ctx, 52);
        defHeight = Util.dip2px(ctx, 20);
        cellWidth = (int) (tableShowWidth * 0.4f + 0.5f);
//        cellMinWidth = (int) (tableShowWidth * 0.25f + 0.5f);
//        cellMaxWidth = (int) (tableShowWidth * 0.85f + 0.5f);
        if (dataSource == null) {
            dataSource = new LinkedList<>();
            for (int i = 0; i < row; i++) {
                dataSource.add(new LinkedList<>());
                for (int j = 0; j < col; j++) {
                    TableCellInfo cellInfo = new TableCellInfo(cellWidth, defHeight);
//                    cellInfo.richText = i * col + j + "";
                    cellInfo.richText = "";
                    dataSource.get(i).add(cellInfo);
                }
            }
        }
    }

    @Override
    public List<List<TableCellInfo>> getDatas() {
        return dataSource;
    }

    @Override
    public int getCol() {
        return col;
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getCellCount() {
        return dataSource.size() * dataSource.get(0).size();
    }

    @Override
    public int getCellWidth() {
        return cellWidth;
    }

    @Override
    public List<TableCellInfo> addCol(int colIndex) {
        List<TableCellInfo> res = new ArrayList<>();
        addCol(colIndex, res);
        return res;
    }

    @Override
    public void addCol(int colIndex, List<TableCellInfo> actionCells) {
        if (colIndex < 0 || colIndex >= col) {
            // 默认添加在最后一行
            colIndex = col;
        }
        if (actionCells == null) {
            actionCells = new ArrayList<>();
        }
        if (actionCells.size() == 0) {
            // 新添加行
            for (int i = 0; i < row; i++) {
                TableCellInfo cellInfo = new TableCellInfo(cellWidth, defHeight);
                cellInfo.cellHeight = defHeight;
                cellInfo.rowHeight = dataSource.get(0).get(0).rowHeight;
//                cellInfo.richText = i * col + colIndex + " new col " + i;
                cellInfo.richText = "";
                dataSource.get(i).add(colIndex, cellInfo);
                actionCells.add(cellInfo);
            }
        } else {
            // undo redo 操作
            for (int i = 0; i < row; i++) {
                dataSource.get(Math.min(i, dataSource.size() - 1)).add(actionCells.get(Math.min(i, actionCells.size() - 1)));
            }
        }
        col = dataSource.get(0).size();

        if (col < 4) {
            updateCellWidth(col);
        }

        // 通知UI刷新，放在最后
        refreshRow.postValue(new int[]{RefreshEvent.ADD_OR_DELETE_COL, -1, colIndex});
    }

    @Override
    public List<TableCellInfo> delCol(int colIndex) {
        if (col == 1) return null;
        List<TableCellInfo> res = new ArrayList<>();
        if (colIndex < 0 || colIndex >= col) {
            // 默认删除最后一行
            colIndex = col - 1;
        }
        for (int i = 0; i < row; i++) {
            // 动态减，坐标要减上偏移量
            res.add(dataSource.get(i).remove(colIndex));
        }
        col = dataSource.get(0).size();
        if (col < 4) {
            updateCellWidth(col);
        }
        // 通知UI刷新，放在最后
        refreshRow.postValue(new int[]{RefreshEvent.ADD_OR_DELETE_COL, -1, colIndex});
        return res;
    }

    @Override
    public List<TableCellInfo> addRow(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= row) {
            // 默认添加在最后一行
            rowIndex = dataSource.size();
        }
        List<TableCellInfo> newRow = new LinkedList<>();
        for (int i = 0; i < col; i++) {
            TableCellInfo cellInfo = new TableCellInfo(cellWidth, defHeight);
            cellInfo.cellHeight = defHeight;
            cellInfo.rowHeight = defHeight;
//            cellInfo.richText = (col * rowIndex + i) + "new row " + i;
            cellInfo.richText = "";
            newRow.add(cellInfo);
        }
        dataSource.add(rowIndex, newRow);
        row = dataSource.size();
        // 通知UI刷新，放在最后
        refreshRow.postValue(new int[]{RefreshEvent.ADD_OR_DELETE_ROW, rowIndex, -1});
        return newRow;
    }

    @Override
    public List<TableCellInfo> addRow(int rowIndex, List<TableCellInfo> actionCells) {
        if (rowIndex < 0 || rowIndex >= row) {
            // 默认添加在最后一行
            rowIndex = dataSource.size();
        }
        dataSource.add(rowIndex, actionCells);
        row = dataSource.size();
        // 通知UI刷新，放在最后
        refreshRow.postValue(new int[]{RefreshEvent.ADD_OR_DELETE_ROW, rowIndex, -1});
        return actionCells;
    }

    @Override
    public List<TableCellInfo> delRow(int rowIndex) {
        if (row == 1) return null;
        List<TableCellInfo> res = new ArrayList<>();
        if (rowIndex < 0 || rowIndex >= row) {
            // 默认删除在最后一行
            rowIndex = row - 1;
        }
        res.addAll(dataSource.remove(rowIndex));
        row = dataSource.size();
        // 通知UI刷新，放在最后
        refreshRow.postValue(new int[]{RefreshEvent.ADD_OR_DELETE_ROW, rowIndex, -1});
        return res;
    }

    private void updateCellWidth(int column) {
        switch (column) {
            case 1:
                cellWidth = tableShowWidth;
                break;
            case 2:
                cellWidth = (int) (tableShowWidth * 0.5f + 0.5f);
                break;
            default:
                cellWidth = (int) (tableShowWidth * 0.4f + 0.5f);
                break;
        }
        for (List<TableCellInfo> rows : dataSource) {
            for (TableCellInfo col : rows) {
                col.width = cellWidth;
                if (col.rowHeight <= 0) {
                    col.rowHeight = defHeight;
                }
            }
        }
    }

    public void updateCellSize(int position, int height) {
        int size = getCellCount();
        if (position > size) return;
        int targetCol = position % col, targetRow = position / col;
        int rowMaxCellHeight = findMaxRowCellHeight(targetRow, targetCol);
        List<TableCellInfo> rowCellInfos = dataSource.get(targetRow);
        if (rowMaxCellHeight < height) {
            // 更新当前行高
            for (int i = 0; i < col; i++) {
                rowCellInfos.get(i).rowHeight = height;
            }
            refreshRow.postValue(new int[]{RefreshEvent.REFRESH_ROW, targetRow, targetCol});
        } else {
            refreshRow.postValue(new int[]{RefreshEvent.REFRESH_COL, -1, targetCol});
        }
    }

    /**
     * 查找所在行的其余cell的最大height
     *
     * @param targetRow 所在行
     * @param targetCol 所在列
     */
    private int findMaxRowCellHeight(int targetRow, int targetCol) {
        int maxHeight = 0;
        List<TableCellInfo> rowCellInfos = dataSource.get(targetRow);
        for (int i = 0; i < col; i++) {
            if (i != targetCol) {
                maxHeight = Math.max(maxHeight, rowCellInfos.get(i).cellHeight);
            }
        }
        return maxHeight;
    }

    @Override
    public void updateDatas(List<List<TableCellInfo>> parseTableCell) {
        dataSource.clear();
        dataSource.addAll(parseTableCell);
        if (parseTableCell.size() == 0 || parseTableCell.get(0).size() == 0) return;
        row = parseTableCell.size();
        col = parseTableCell.get(0).size();
        updateCellWidth(col);
        refreshRow.postValue(new int[]{RefreshEvent.ADD_OR_DELETE_COL, 0, 0});
    }

    @Override
    public void notifyItemChanged(UndoRedoHelper.Action action) {
        List<List<TableCellInfo>> datas = dataSource;
        List<TableCellInfo> rowDatas = datas.get(Math.min(action.row, datas.size() - 1));
        TableCellInfo cellInfo = rowDatas.get(Math.min(action.col, rowDatas.size() - 1));
        cellInfo.requestFocus = true;
        cellInfo.richText = action.actionTarget.toString();
        cellInfo.cursorSelectionStart = action.startCursor;
        cellInfo.cursorSelectionEnd = action.endCursor;
        switch (action.actionType) {
            case UndoRedoActionTypeEnum.ADD_COL:
            case UndoRedoActionTypeEnum.DEL_COL:
                refreshRow.postValue(new int[]{RefreshEvent.ADD_OR_DELETE_COL, -1, action.col});
                break;
            case UndoRedoActionTypeEnum.ADD_ROW:
            case UndoRedoActionTypeEnum.DEL_ROW:
                refreshRow.postValue(new int[]{RefreshEvent.ADD_OR_DELETE_ROW, action.row, -1});
                break;
            default:
                refreshRow.postValue(new int[]{RefreshEvent.REFRESH_ROW, action.row, action.col});
                break;
        }
    }

}
