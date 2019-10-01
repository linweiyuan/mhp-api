package com.linweiyuan.mhp.service

import com.linweiyuan.mhp.model.Mail

interface RabbitService {
    fun sendMail(mail: Mail)
}
