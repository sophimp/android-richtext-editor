package com.sophimp.are.utils;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({UndoRedoActionTypeEnum.ADD, UndoRedoActionTypeEnum.DELETE, UndoRedoActionTypeEnum.CHANGE, UndoRedoActionTypeEnum.DEL_COL, UndoRedoActionTypeEnum.ADD_COL, UndoRedoActionTypeEnum.DEL_ROW, UndoRedoActionTypeEnum.ADD_ROW})
public @interface UndoRedoActionTypeEnum {

    /**
     * 增加字符
     */
    String ADD = "add";

    /**
     * 删除字符
     */
    String DELETE = "delete";

    /**
     * 修改样式
     */
    String CHANGE = "change";

    /**
     * 删除列
     */
    String DEL_COL = "del_col";

    /**
     * 添加列
     */
    String ADD_COL = "add_col";

    /**
     * 删除行
     */
    String DEL_ROW = "del_row";

    /**
     * 添加行
     */
    String ADD_ROW = "add_row";

}
