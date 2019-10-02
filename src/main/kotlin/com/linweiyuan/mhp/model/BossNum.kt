package com.linweiyuan.mhp.model

data class BossNum(
        val gameId: String, // 对应游戏4位16进制ID
        val killNum: Int, // 狩猎数量
        val catchNum: Int // 捕获数量
)
