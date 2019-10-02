package com.linweiyuan.mhp.model

data class WeaponNum(
        var type: Byte, // 武器种类
        val place: Byte, // 任务地点（内外部）
        val value: Int // 数值
)
