package com.linweiyuan.mhp.common

object Constant {
    const val REDIS_KEY_MHP_USER_USERNAME = "mhp_user_username:"
    const val REG_CODE_TIMEOUT: Long = 5 // 验证码有效时间（分钟）

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
    // 随从猫信息
    const val CAT_NAME_KEY = 0x217ACF44 // 名字
    const val CAT_OWNER_KEY = 0x217ACF7C // 主人名字
    const val CAT_INTRO_KEY = 0x217ACF5E // 介绍
    const val KEY_OFFSET_CAT = 0x000000A0 // 偏移
    // 游戏时间
    const val TIME_KEY = 0x21754544
    // 武器使用频率
    const val WEAPON_PLACE_KEY_1 = 0x217A86C0 // 村长、集会
    const val WEAPON_PLACE_KEY_2 = 0x217A86DA // 训练所
    const val KEY_OFFSET_2 = 0x00000002
}
