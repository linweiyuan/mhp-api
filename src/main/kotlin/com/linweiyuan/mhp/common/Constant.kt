package com.linweiyuan.mhp.common

object Constant {
    const val REDIS_KEY_MHP_USER_USERNAME = "mhp_user_username:"
    const val REG_CODE_TIMEOUT = 5L // 验证码有效时间（分钟）

    const val MQ_QUEUE_MAIL = "mhp-mail"
    const val MAIL_NICKNAME = "怪物猎人金手指"

    const val JWT_CLAIM_USERNAME = "mhp_user_username"
    const val JWT_TIMEOUT: Long = 7 // 天

    const val CODE_NAME_SUFFIX = "（${MAIL_NICKNAME}）" // 金手指代码名字小尾巴
    const val KEY_OFFSET_4 = 0x00000004
    // 护石
    const val STONE_KEY = 0x2174FE14
    const val STONE_VALUE = 0x79DF6501
    // 饮料技能
    const val DRINK_KEY = 0x217AC8F0 // default val 0x00000000
    // 玩家信息
    const val PLAYER_NAME_KEY = 0x2174FCAC // 名字
    const val PLAYER_CARD_KEY = 0x217A8280 // 工会卡片名字
    const val PLAYER_CARD_INTRO_KEY = 0x217A82E0 // 工会卡片介绍
}
