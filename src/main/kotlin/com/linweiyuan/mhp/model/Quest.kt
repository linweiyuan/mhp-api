package com.linweiyuan.mhp.model

data class Quest(
        val basic: Basic,
        val shikyus: MutableList<Shikyu>,
        val hosyus: MutableList<Hosyu>,
        val monsters: MutableList<Monster>,
        val bosses: MutableList<Boss>
)

data class Basic(
        val name: String, // 任务名称
        val success: String, // 成功条件
        val failure: String, // 失败条件
        val content: String, // 任务内容
        val monster: String, // 主要怪物
        val client: String, // 委托人
        val successPts: Int, // 成功点数
        val failPts: Int, // 失败点数
        val startArea: Byte, // 开始位置
        val bossSkill: Byte, // BOSS技能行为
        val pickRank: Byte, // 剥取素材级别
        val bgm: Byte,
        val returnTime: Byte, // 任务成功后返回时间
        val questType: Byte, // 任务类型
        val contractZ: Int, // 契约金
        val rewardZ: Int, // 报酬金
        val minute: Byte, // 任务时间（分）
        val second: Byte, // 任务时间（秒）
        val rank: Byte, // 任务级别
        val map: Byte,
        val joinCondition1: Byte, // 参与条件
        val joinCondition2: Byte,
        val successCondition: Byte, // 成功条件
        val successConditionType1: Byte,
        val successConditionTypeItem1: String,
        val successConditionTypeNum1: Byte,
        val successConditionType2: Byte,
        val successConditionTypeItem2: String,
        val successConditionTypeNum2: Byte,
        val bossIcon1: String, // BOSS显示图标
        val bossIcon2: String,
        val bossIcon3: String,
        val bossIcon4: String,
        val bossIcon5: String
)

data class Shikyu(
        val gameId: String,
        val num: Byte
)

data class Hosyu(
        val gameId: String,
        val num: Byte
)

data class Monster(
        val gameId: String,
        val area: Byte, // 出现地区
        val num: Byte
)

data class Boss(
        val gameId: String,
        val status: Byte, // 状态(正常、超带电)
        val num: Byte,
        val area: Byte,
        var round: Byte, // 出现场次
        val size: Short, // 体型
        val hp: Byte, // 血量
        val strength: Byte, // 强度
        val endurance: Byte, // 部位耐性
        val fatigue: Byte // 耐力
)
