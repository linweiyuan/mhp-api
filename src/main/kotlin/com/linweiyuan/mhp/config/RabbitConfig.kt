package com.linweiyuan.mhp.config

import com.linweiyuan.mhp.common.Constant
import org.springframework.amqp.core.Queue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig {
    @Bean("mhp-mail")
    fun mailQueue() = Queue(Constant.MQ_QUEUE_MAIL)
}
