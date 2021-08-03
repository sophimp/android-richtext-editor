
## Android 富文本编辑器

android-richtext-editor(ARE) 是基于Android原生Spannable架构实现，参考了 [chinalwb/Android-Rich-text-Editor](https://github.com/chinalwb/Android-Rich-text-Editor)

由于`chinalwb/Android-Rich-text-Editor`不再维护，随着定制需求与BUG修改越来越多，便想着使用kotlin重新实现一番，完善了每个style的实现细节，新增了几种Style实现。

### 已实现特性
<b>具体使用查看 DefaultToolbar 实现</b>
- 插入表情
- 插入视频
- 插入图片(本地与网络)
- 文字颜色（前景色）
- 文字背景色 - Background color
- 字体大小
- 右缩进
- 左缩进
- 左对齐 - Align left
- 居中对齐 - Align center
- 右对齐 - Align right
- 引用

- 加粗 - Bold
- 下划线 - Underline
- 斜体 - Italic
- 删除线 - Strikethrough
- 有序列表 - Numeric list
- 无序列表 - Bullet list
- TODO列表 - TODO list
- 上角标
- 下角标
- 增大行距
- 缩小行距
- 插入分割线
- 插入超链接 - Hyper link
- 段落样式的复制粘贴
- 所有样式均支持导出HTML文件
- 加载HTML内容并继续编辑或显示
