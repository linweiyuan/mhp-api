package com.linweiyuan.mhp.service.impl

import com.linweiyuan.mhp.common.Constant
import com.linweiyuan.mhp.model.Mail
import com.linweiyuan.mhp.service.RabbitService
import org.springframework.amqp.rabbit.annotation.RabbitHandler
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets

@Service
class RabbitServiceImpl : RabbitService {
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private lateinit var mailSender: JavaMailSender

    @RabbitHandler
    @RabbitListener(queues = [Constant.MQ_QUEUE_MAIL])
    override fun sendMail(mail: Mail) {
        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, StandardCharsets.UTF_8.name())
        helper.setFrom(javax.mail.internet.InternetAddress(mail.from, Constant.MAIL_NICKNAME, StandardCharsets.UTF_8.name()))
        helper.setTo(mail.to)
        helper.setSubject(mail.subject)
        helper.setText(mail.text)
        mailSender.send(message)
    }
}
