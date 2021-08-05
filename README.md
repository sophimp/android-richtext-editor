
## Android 富文本编辑器

android-richtext-editor(ARE) 是基于Android原生Spannable架构实现，参考了 [chinalwb/Android-Rich-text-Editor](https://github.com/chinalwb/Android-Rich-text-Editor)

由于`chinalwb/Android-Rich-text-Editor`不再维护，随着定制需求与BUG修改越来越多，便想着使用kotlin重新实现一番，完善了每个style的实现细节，新增了几种Style实现。

### 特性

1. 样式

|段落级| 字符级| span块 |
|:--:|:--:|:--:|
|右缩进 (支持多级缩进)|文字颜色|插入表情|
|左缩进|文字背景色|插入视频|
|左对齐|字体大小|插入图片(本地与网络)|
|居中对齐|加粗|插入分割线|
|右对齐|下划线||
|引用|斜体||
|有序列表(支持多级缩进)|删除线||
|无序列表(支持多级缩进)|上角标||
|TODO列表(支持多级缩进)|下角标||
|增大行距|插入超链接(支持别名)||
|缩小行距||

![paragraph_demo](https://github.com/sophimp/android-richtext-editor/blob/master/art/paragraph_demo.gif) ![character_demo](https://github.com/sophimp/android-richtext-editor/blob/master/art/character_demo.gif)

2. 操作特性

- 所有样式均支持导出HTML 文本
- 加载HTML内容并继续编辑或显示

![image_video](https://github.com/sophimp/android-richtext-editor/blob/master/art/image_video.gif)

### 使用说明
<b>具体使用查看 [DefaultToolbar#initDefaultToolItem](https://github.com/sophimp/android-richtext-editor/blob/master/lib/are/src/main/java/com/sophimp/are/toolbar/DefaultToolbar.kt) </b>
每一个style 对应一个 toolbar item
