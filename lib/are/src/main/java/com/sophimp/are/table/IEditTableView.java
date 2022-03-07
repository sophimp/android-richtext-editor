package com.sophimp.are.table;

import com.sophimp.are.utils.UndoRedoHelper;

import java.util.List;

/**
 * @des: Table 操作类
 * @since: 2021/6/22
 * @version: 0.1
 * @author: sfx
 */
public interface IEditTableView {
    List<List<TableCellInfo>> getDatas();

    int getCol();

    int getRow();

    int getCellCount();

    int getCellWidth();

    /**
     * @param colIndex -1 默认添加最后一列
     *                 从零开始,
     */
    List<TableCellInfo> addCol(int colIndex);

    /**
     * 用于undo redo
     * @param col  -1 默认添加最后一列
     *               从零开始,
     */
    void addCol(int col, List<TableCellInfo> actionCells);

    /**
     * @param rowIndex  -1 默认添加最后一行
     *                  从零开始,
     */
    List<TableCellInfo> addRow(int rowIndex);

    /**
     * 用于undo redo
     * @param rowIndex  -1 默认添加最后一行
     *                  从零开始,
     */
    List<TableCellInfo> addRow(int rowIndex, List<TableCellInfo> actionCells);

    /**
     * @param rowIndex  -1 默认删除最后一行
     *                  从零开始,
     */
    List<TableCellInfo> delRow(int rowIndex);

    /**
     * @param colIndex  -1 默认删除最后一列,
     *                  从零开始,
     */
    List<TableCellInfo> delCol(int colIndex);

    /**
     * 更新cell 大小, 减少LayoutManager重复测量
     *
     * @param position cell位置
     * @param height   字体当前高度
     */
    void updateCellSize(int position, int height);

    void updateDatas(List<List<TableCellInfo>> parseTableCell);

    /**
     * 用于undo redo
     */
    void notifyItemChanged(UndoRedoHelper.Action action);

}
