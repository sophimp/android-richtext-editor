package com.sophimp.are.decoration

data class LineDivider(
    var leftSideLine: SideLine = SideLine(false, -0x99999a, 0f, 0f, 0f),
    var topSideLine: SideLine = SideLine(false, -0x99999a, 0f, 0f, 0f),
    var rightSideLine: SideLine = SideLine(false, -0x99999a, 0f, 0f, 0f),
    var bottomSideLine: SideLine = SideLine(false, -0x99999a, 0f, 0f, 0f)
)