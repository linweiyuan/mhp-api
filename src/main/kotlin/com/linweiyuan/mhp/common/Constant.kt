package com.linweiyuan.mhp.common

object Constant {
    const val REDIS_KEY_MHP_USER_USERNAME = "mhp_user_username:"
    const val REG_CODE_TIMEOUT = 5L // 验证码有效时间（分钟）

    const val MQ_QUEUE_MAIL = "mhp-mail"
    const val MAIL_NICKNAME = "怪物猎人金手指"

    const val JWT_CLAIM_USERNAME = "mhp_user_username"
    const val JWT_TIMEOUT: Long = 7 // 天
}
