package com.linweiyuan.mhp.model

data class Stone(
        val skill1: Byte, // 技能
        val point1: Byte, // 技能点
        val skill2: Byte,
        val point2: Byte,
        val rarity: Byte, // 稀有度
        val slot: Byte // 孔数
)
