
## Android 富文本编辑器

### 背景

在使用[chinalwb/Android-Rich-text-Editor](https://github.com/chinalwb/Android-Rich-text-Editor)(ARE)过程中，随着新需求的增加，对ARE修改定制越来越深, 正与其readme所说的，还达不到商业化需求。
由于ARE作者久不维护， 且ARE帮助了我很多，因此萌生重构的想法，便有了此库回馈开源社区。

ARE 是基于Android原生的Spannable架构，因此名字我依旧沿承了 android-richtext-editor, 同样简称 are

### 重构了什么
1. 使用kotlin
2. 重构了整体架构
3. 基于商业化需求，优化了每个Style的实现
4. 扩展了新的功能

### 已实现特性
具体使用查看 DefaultToolbar 实现
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
- 所有样式均支持导出HTML文件
- 加载HTML内容并继续编辑或显示

### 未实现特性
1. 表格
直接在EditText中编辑表格，基于原生的Spannable框架有些难以实现, 但是也可以绕过
2. 音频
3.