package com.linweiyuan.mhp.service.impl

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.linweiyuan.mhp.common.Constant
import com.linweiyuan.mhp.entity.User
import com.linweiyuan.mhp.model.Mail
import com.linweiyuan.mhp.repository.UserRepository
import com.linweiyuan.mhp.service.UserService
import com.linweiyuan.mhp.util.JsonUtil
import com.linweiyuan.misc.model.Code
import com.linweiyuan.misc.model.Data
import net.bytebuddy.utility.RandomString
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.TimeUnit

@Service
class UserServiceImpl : UserService {
    @Autowired
    private lateinit var userRepository: UserRepository
    @Autowired
    private lateinit var redis: StringRedisTemplate
    @Autowired
    private lateinit var rabbit: RabbitTemplate

    @Value("\${spring.mail.username}")
    private lateinit var mailFrom: String
    @Value("\${jwt.secret}")
    private lateinit var jwtSecret: String

    override fun register(user: User): Data {
        val username = user.username
        if (!checkUsername(username)) {
            return Data(Code.ERR, "请输入正确的邮箱地址")
        }

        val dbUser = userRepository.findByUsername(user.username)
        if (dbUser != null) {
            return Data(Code.ERR, "该邮箱已被注册")
        }

        user.regCode = RandomString.make()
        redis.opsForValue().set("${Constant.REDIS_KEY_MHP_USER_USERNAME}${username}", JsonUtil.toJson(user), Constant.REG_CODE_TIMEOUT, TimeUnit.MINUTES)

        val mail = Mail(mailFrom, username, "验证码", "你好，本次注册的验证码为 ${user.regCode}\n${Constant.REG_CODE_TIMEOUT}分钟内有效")
        rabbit.convertAndSend(Constant.MQ_QUEUE_MAIL, mail)
        return Data(msg = "验证码已发送到注册邮箱（${Constant.REG_CODE_TIMEOUT}分钟内有效）")
    }

    /**
     * 简单验证邮箱地址
     */
    private fun checkUsername(username: String?) = username != null && username.contains("@") && !username.startsWith("@") && !username.endsWith("@")

    override fun validate(user: User): Data {
        val key = "${Constant.REDIS_KEY_MHP_USER_USERNAME}${user.username}"
        if (!redis.hasKey(key)) {
            return Data(Code.ERR, "验证码已过期")
        }

        val redisUser = JsonUtil.fromJson(redis.opsForValue().get(key) as String, User::class)
        if (redisUser.username != user.username || redisUser.regCode != user.regCode) {
            return Data(Code.ERR, "验证码不正确")
        }

        redisUser.regTime = Date()
        userRepository.save(redisUser)
        redis.delete(key)
        return login(user)
    }

    override fun login(user: User): Data {
        val dbUser = userRepository.findByUsername(user.username)
        if (dbUser == null || dbUser.password != user.password) {
            return Data(Code.ERR, "用户名或密码不正确")
        }

        dbUser.loginTime = Date()
        userRepository.save(dbUser)

        redis.opsForValue().set("${Constant.REDIS_KEY_MHP_USER_USERNAME}${user.username}", JsonUtil.toJson(user), Constant.JWT_TIMEOUT, TimeUnit.DAYS)
        val token = JWT.create()
            .withClaim(Constant.JWT_CLAIM_USERNAME, dbUser.username)
            .withExpiresAt(Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(Constant.JWT_TIMEOUT)))
            .sign(Algorithm.HMAC256(jwtSecret))
        return Data(msg = "登录成功", data = token)
    }
}
