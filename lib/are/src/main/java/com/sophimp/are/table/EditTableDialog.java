package com.sophimp.are.table;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sophimp.are.R;
import com.sophimp.are.RichEditText;
import com.sophimp.are.databinding.DialogFragmentEditTableBinding;
import com.sophimp.are.utils.Util;

import java.util.List;

/**
 * 可编辑富文本表格弹窗
 */
public class EditTableDialog extends BottomSheetDialogFragment {
    private static final int MAX_TEXTSIZE = 10000;
    private static final int REQUEST_CODE_OCR = 0x111;
    private static final String MENU_ABANDON_TABLE = "menu_abandon_table";
    private final Drawable background;
    private DialogFragmentEditTableBinding binding;

    private EditTableViewModel tableViewModel;
    private OnConfirmListener confirmListener;
    private EditTableAdapter tableAdapter;
    private String html = "";
    private int curRow = 0, curCol = 0;
    private Context mContext;

    public EditTableDialog(Drawable background) {
        this.background = background;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getContext() == null) {
            return super.onCreateDialog(savedInstanceState);
        }
        return new BottomSheetDialog(getContext(), R.style.TransparentBottomSheetStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogFragmentEditTableBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData(savedInstanceState);
        setListener();
    }

    @Override
    public void onStart() {
        super.onStart();
        // 禁止拖动消失效果
//        getBehavior().setPeekHeight(getHeight());
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    protected void initView() {
        mContext = getContext();
    }

    private void updateToolbarHeight(boolean keyboardShow) {
//        ConstraintLayout.LayoutParams toolbarLayoutParams = (ConstraintLayout.LayoutParams) binding.toolbar.getLayoutParams();
//        if (keyboardShow) {
//            toolbarLayoutParams.bottomMargin = KeyboardUtil.getKeyboardHeight(getContext()) + getBottomStatusHeight(getContext());
//        } else {
//            toolbarLayoutParams.bottomMargin = getBottomStatusHeight(getContext());
//        }
//        binding.toolbar.setLayoutParams(toolbarLayoutParams);
//        checkAndUpdateTableLayout();
    }

    protected void initData(Bundle savedInstanceState) {

        tableViewModel = new ViewModelProvider(this).get(EditTableViewModel.class);
        tableViewModel.init(getContext());

        binding.richTable.setMotionEventSplittingEnabled(false);

        tableAdapter = new EditTableAdapter(tableViewModel, binding.tableToolbar);

        tableAdapter.setOnCellFocusListener(new OnCellFocusListener() {

            @Override
            public void onCellFocus(RichEditText editText, int row, int col) {
                curRow = row;
                curCol = col;
//                ToastUtils.show("cur row: " + row + " col: " + col);
//                editText.clearToolbarStyle();
//                editText.setToolbar(binding.drRichToolbar);
//                binding.mTextFontStyleGroupView.setEditText(editText);
//                editText.addStyle(binding.mTextFontStyleGroupView.getStyleList());
//                editText.registerSelectionChangeListener(binding.mTextFontStyleGroupView);
//                binding.mTextFontStyleGroupView.onSelectionChange(editText.getSelectionStart(), editText.getSelectionEnd());
            }
        });

        binding.richTable.setLayoutManager(new GridLayoutManager(getContext(), tableViewModel.getCol()));
        binding.richTable.setItemAnimator(null);
        binding.richTable.addItemDecoration(new ItemDecoration(getActivity(), tableViewModel));
        binding.richTable.setAdapter(tableAdapter);

        binding.richTable.post(() -> {
            if (tableAdapter.getCurFocusEditText() != null) {
                tableAdapter.getCurFocusEditText();
                tableAdapter.getCurFocusEditText().requestFocus();
            }
//            updateToolbarHeight(KeyboardUtil.isKeyboardVisible());
        });

        tableViewModel.refreshRow.observe(this, ints -> {
            if (ints.length < 3) return;
            /*
             *  ints[0] 刷新事件
             *      0: 增删行
             *      1: 增删列
             *      2: 刷新行
             *      3: 刷新列
             * int[1] 变更所在行, int[2] 变更所在列
             */
            switch (ints[0]) {
                case 0: // 增删行
                    if (ints[1] >= 0) {
//                        int rowStart = Math.max(0, ints[1] - 1) * tableViewModel.getCol();
//                        tableAdapter.notifyItemRangeChanged(rowStart, tableViewModel.getCellCount());
                        tableAdapter.notifyDataSetChanged();
                    }
                    break;
                case 2: // 刷新行
                    if (ints[1] >= 0) {
                        int rowStart = ints[1] * tableViewModel.getCol();
                        tableAdapter.notifyItemRangeChanged(rowStart, rowStart + tableViewModel.getCol());
                    }
                    break;
                case 1: // 增删列
                    binding.richTable.setLayoutManager(new GridLayoutManager(getContext(), tableViewModel.getCol()));
                case 3: // 刷新列
                    if (ints[2] >= 0) {
                        tableAdapter.notifyItemRangeChanged(ints[2], tableViewModel.getCellCount());
                    }
                    break;
            }
        });

        if (!TextUtils.isEmpty(html)) {
            // 回显表格
            tableViewModel.updateDatas(Util.parseTableByHtmlCleaner(html));
        }
    }

    protected void setListener() {
        binding.tvBtnClose.setOnClickListener(v -> {
        });

        binding.tvBtnConfirm.setOnClickListener(v -> {
            // 截图插入
            insertTableToText();
        });

        binding.ivBtnAddTableCol.setOnClickListener(v -> {
            List<TableCellInfo> res = tableViewModel.addCol(curCol + 1);
            if (tableAdapter.getCurFocusEditText() != null) {
                tableAdapter.getCurFocusEditText().requestFocus();
            }
        });

        binding.ivBtnDelTableCol.setOnClickListener(v -> {
            List<TableCellInfo> res = tableViewModel.delCol(curCol);
            if (tableAdapter.getCurFocusEditText() != null) {
                tableAdapter.getCurFocusEditText().requestFocus();
            }
        });

        binding.ivBtnAddTableRow.setOnClickListener(v -> {
            List<TableCellInfo> res = tableViewModel.addRow(curRow + 1);
            if (tableAdapter.getCurFocusEditText() != null) {
                tableAdapter.getCurFocusEditText().requestFocus();
            }
        });

        binding.ivBtnDelTableRow.setOnClickListener(v -> {
            List<TableCellInfo> res = tableViewModel.delRow(curRow);
            if (tableAdapter.getCurFocusEditText() != null) {
                tableAdapter.getCurFocusEditText().requestFocus();
            }
        });
        binding.richTable.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                Log.d("are", recyclerViewLayout : " +
//                        " \n table bottom : " + bottom + " table y: " + binding.richTable.getY() + " table height: " + binding.richTable.getHeight() +
//                        " \n row menu y: " + binding.llTableRowMenu.getY() +
//                        " \n bottom toolbar y: " + binding.llBottomToolbar.getY());

            }
        });
    }

    private void checkAndUpdateTableLayout() {
        ConstraintLayout.LayoutParams tableParams = (ConstraintLayout.LayoutParams) binding.richTable.getLayoutParams();
        ConstraintLayout.LayoutParams rowMenuParams = (ConstraintLayout.LayoutParams) binding.llTableRowMenu.getLayoutParams();
        if ((binding.richTable.getY() + binding.richTable.getHeight() + binding.llTableRowMenu.getHeight()) > binding.tableToolbar.getY() - 10) {
            // 表格有覆盖
            tableParams.height = (int) (binding.tableToolbar.getY() - binding.clTitleContainer.getHeight() - binding.llTableRowMenu.getHeight());
            tableParams.bottomToTop = R.id.ll_table_row_menu;
//            tableParams.topToBottom = -1;
            rowMenuParams.bottomToTop = R.id.table_toolbar;
            rowMenuParams.topToBottom = R.id.rich_table;
        } else {
            tableParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//            tableParams.topToBottom = R.id.top_space;
            tableParams.bottomToTop = -1;
            rowMenuParams.bottomToTop = -1;
            rowMenuParams.topToBottom = R.id.rich_table;
        }
        binding.richTable.setLayoutParams(tableParams);
        binding.llTableRowMenu.setLayoutParams(rowMenuParams);
    }

    public void setConfirmListener(OnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    private void insertTableToText() {

    }

    public String toHtml() {
        StringBuilder html = new StringBuilder();
        html.append("<table data-source=\"sgx\">");
        html.append("<tbody>");
        List<List<TableCellInfo>> datas = tableViewModel.getDatas();
        // 以现在的RichEditText内容为主， 因为只更新style不会触发缓存
        for (int i = 0, size = datas.size(); i < size; i++) {
            html.append("<tr>");
            List<TableCellInfo> rowData = datas.get(i);
            for (int j = 0, len = rowData.size(); j < len; j++) {
                TableCellInfo cellInfo = rowData.get(j);
                if (cellInfo.alignment != null) {
                    html.append("<td ");
                    if (cellInfo.alignment == Layout.Alignment.ALIGN_CENTER) {
                        html.append("style=\" text-align: center;\"");
                    } else if (cellInfo.alignment == Layout.Alignment.ALIGN_OPPOSITE) {
                        html.append("style=\" text-align: center;\"");
                    }
                    html.append(">");
                } else {
                    html.append("<td>");
                }
                html.append(cellInfo.richText);
                html.append("</td>");
            }
            html.append("</tr>");
        }
        html.append("</tbody>");
        html.append("</table>");
        return html.toString();
    }

    public void setHtml(String html) {
        this.html = html;
    }


    public interface OnConfirmListener {
        void onConfirm(Bitmap bitmap, String html);
    }
}
