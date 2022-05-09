package com.sophimp.are.table;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.Layout;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.AlignmentSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sophimp.are.R;
import com.sophimp.are.RichEditText;
import com.sophimp.are.databinding.ItemRichTableCellBinding;
import com.sophimp.are.models.StyleChangedListener;
import com.sophimp.are.toolbar.DefaultTableToolbar;
import com.sophimp.are.utils.UndoRedoActionTypeEnum;
import com.sophimp.are.utils.UndoRedoHelper;

/**
 * @des: EditText的监听
 * @since: 2021/6/21
 * @version: 0.1
 * @author: sfx
 */
public class EditTableAdapter extends RecyclerView.Adapter<EditTableAdapter.TableCellHolder> {

    private final DefaultTableToolbar richToolBar;
    private EditTableViewModel tableViewModel;
    private OnCellFocusListener cellFocusChangeListener;
    private OnCellChangeListener cellChangeListener;
    private boolean shouldClearEmpty = true;
    private TableCellInfo lastFocusCell = null;

    private final static int UPDATE_CELL_HEIGHT = 0x1001;

    private int curScrollPosition = 0;

    public EditTableAdapter(EditTableViewModel tableViewModel, DefaultTableToolbar areToolbar) {
        this.richToolBar = areToolbar;
        this.tableViewModel = tableViewModel;
    }

    protected void convert(TableCellHolder helper, int position) {
        int curRow = position / tableViewModel.getCol();
        int curCol = position % tableViewModel.getCol();

        // outOfIndexException
        if (curRow >= tableViewModel.getDatas().size() || curCol >= tableViewModel.getDatas().get(curRow).size())
            return;
        helper.cellInfo = tableViewModel.getDatas().get(curRow).get(curCol);
        helper.binding.areItem.fromHtml(helper.cellInfo.richText);
        helper.binding.areItem.post(() -> {
            if (helper.cellInfo.alignment == Layout.Alignment.ALIGN_CENTER) {
                helper.binding.areItem.getEditableText().setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, helper.binding.areItem.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (helper.cellInfo.alignment == Layout.Alignment.ALIGN_OPPOSITE) {
                helper.binding.areItem.getEditableText().setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), 0, helper.binding.areItem.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            helper.updateCellSize(position, helper.cellInfo.cellHeight);
            if (curRow == tableViewModel.focusRow && curCol == tableViewModel.focusCol) {
                helper.binding.areItem.requestFocus();
//            int selectionStart = Math.max(0, Math.min(helper.cellInfo.cursorSelectionStart, helper.binding.areItem.length()));
//            int selectionEnd = Math.max(0, Math.min(helper.cellInfo.cursorSelectionEnd, helper.binding.areItem.length()));
//            helper.binding.areItem.postDelayed(() -> {
//                if (selectionStart < selectionEnd) {
//                    helper.binding.areItem.setSelection(selectionStart, selectionEnd);
//                } else {
//                    helper.binding.areItem.setSelection(selectionEnd);
//                }
//            },50);
            }
        });
    }

    public RichEditText getCurFocusEditText() {
        return richToolBar.getCurEditText();
    }

    public void setOnCellFocusListener(OnCellFocusListener onFocusChangeListener) {
        this.cellFocusChangeListener = onFocusChangeListener;
    }

    public void setCellChangeListener(OnCellChangeListener cellChangeListener) {
        this.cellChangeListener = cellChangeListener;
    }

    @NonNull
    @Override
    public TableCellHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TableCellHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rich_table_cell, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TableCellHolder holder, int position) {
        convert(holder, position);
    }

    @Override
    public int getItemCount() {
        return tableViewModel.getCellCount();
    }

    class TableCellHolder extends RecyclerView.ViewHolder {
        private Handler uiHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                updateCellSizeInHandler(msg.arg1, msg.arg2);
            }
        };
        TableCellInfo cellInfo;
        ItemRichTableCellBinding binding;
        RecyclerView.LayoutParams layoutParams;

        public TableCellHolder(View view) {
            super(view);
            binding = ItemRichTableCellBinding.bind(view);
            layoutParams = (RecyclerView.LayoutParams) binding.areItem.getLayoutParams();
            binding.areItem.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (!binding.areItem.getCanMonitor()) return;
                    if (cellChangeListener != null) {
                        cellChangeListener.beforeCellChange(
                                new UndoRedoHelper.Action(cellInfo.richText, binding.areItem.getSelectionStart(), binding.areItem.getSelectionEnd(), UndoRedoActionTypeEnum.CHANGE, getAbsoluteAdapterPosition(), 0),
                                getAbsoluteAdapterPosition() / tableViewModel.getCol(), getAbsoluteAdapterPosition() % tableViewModel.getCol()
                        );
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    LogUtils.d("sgx table cell changed: " + s + " count: " + count);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!binding.areItem.getCanMonitor()) return;
//                    float strWidth = binding.areItem.getPaint().measureText(s.toString());
                    // 编辑过就清空兼容web的属性
                    cellInfo.alignment = null;
                    cellInfo.cursorSelectionStart = binding.areItem.getSelectionStart();
                    cellInfo.cursorSelectionEnd = binding.areItem.getSelectionEnd();
                    if (cellChangeListener != null) {
                        cellChangeListener.afterCellChange(
                                new UndoRedoHelper.Action(cellInfo.richText, cellInfo.cursorSelectionStart, cellInfo.cursorSelectionEnd, UndoRedoActionTypeEnum.CHANGE, getAbsoluteAdapterPosition(), 0),
                                getAbsoluteAdapterPosition() / tableViewModel.getCol(), getAbsoluteAdapterPosition() % tableViewModel.getCol()
                        );
                    }
                    if (binding.areItem.getLayout() != null) {
//                        LogUtils.d("sgx height: " + binding.areItem.getLayout().getHeight());
                        // 更新当前cell高度
                        updateCellSize(getLayoutPosition(), binding.areItem.getLayout().getHeight());
                        // 更新富文本
//                        LogUtils.d("sgx textChanged richText:" + binding.areItem.toHtml());
                        cellInfo.richText = binding.areItem.toHtml();
                    }
                }
            });
            if (richToolBar != null && binding.areItem.hasFocus()) {
                richToolBar.setCurEditText(binding.areItem);
            }
            binding.areItem.setStyleChangedListener(new StyleChangedListener() {
                @Override
                public void onStyleChanged(RichEditText arEdit) {
                    cellInfo.cursorSelectionStart = binding.areItem.getSelectionStart();
                    cellInfo.cursorSelectionEnd = binding.areItem.getSelectionEnd();
                    if (binding.areItem.getLayout() != null) {
                        // 更新当前cell高度, 样式的更改都是异步的，没那么快刷新，延时刷新
                        uiHandler.postDelayed(() -> {
                            updateCellSize(getLayoutPosition(), binding.areItem.getLayout().getHeight());
                        }, 60);
                        // 更新富文本
                        cellInfo.richText = binding.areItem.toHtml();
//                        LogUtils.d("sgx styleChanged richText:" + binding.areItem.toHtml());
                    }
                }
            });
            binding.areItem.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (binding.areItem == v && hasFocus) {
                        if (cellFocusChangeListener != null) {
                            cellFocusChangeListener.onCellFocus(binding.areItem, getAbsoluteAdapterPosition() / tableViewModel.getCol(), getAbsoluteAdapterPosition() % tableViewModel.getCol());
                        }
                        cellInfo.requestFocus = true;
                        if (lastFocusCell != null) {
                            lastFocusCell.requestFocus = false;
                        }
                    } else {
                        if (lastFocusCell == null || !lastFocusCell.equals(cellInfo)) {
                            lastFocusCell = cellInfo;
                        }
                    }
                }
            });

        }

        void updateCellSize(int position, int cellHeight) {
            layoutParams.width = cellInfo.width;
            Message msg = uiHandler.obtainMessage();
            msg.what = UPDATE_CELL_HEIGHT;
            msg.arg1 = position;
            msg.arg2 = cellHeight;
            uiHandler.removeMessages(UPDATE_CELL_HEIGHT);
            uiHandler.sendMessageDelayed(msg, 100);
        }

        void updateCellSizeInHandler(int position, int cellHeight) {
            if (cellHeight <= 0) {
                binding.areItem.post(() -> {
                    if (binding.areItem.getLayout() != null) {
                        cellInfo.cellHeight = binding.areItem.getLayout().getHeight();
                        tableViewModel.updateCellSize(position, cellInfo.cellHeight);
                        layoutParams.height = cellInfo.rowHeight;
                        binding.areItem.setLayoutParams(layoutParams);
                    }
                });
            } else {
                tableViewModel.updateCellSize(position, cellHeight);
                layoutParams.height = cellInfo.rowHeight;
                binding.areItem.setLayoutParams(layoutParams);
            }
        }

    }

}
