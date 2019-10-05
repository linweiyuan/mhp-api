package com.linweiyuan.mhp.interceptor

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.linweiyuan.mhp.annotation.NoAuth
import com.linweiyuan.mhp.common.Constant
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AuthInterceptor(private val redis: StringRedisTemplate) : HandlerInterceptor {
    @Value("\${jwt.secret}")
    private lateinit var jwtSecret: String

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val uri = request.requestURI
        // swagger-ui
        if (uri.endsWith("swagger-ui.html")
            || uri.contains("webjars")
            || uri.contains("swagger-resources")
            || uri.endsWith("/")
            || uri.endsWith("csrf")
            || uri.endsWith("error")
        ) {
            return true
        }

        // 白名单方式
        if ((handler as HandlerMethod).method.getAnnotation(NoAuth::class.java) != null) {
            return true
        }

        val token = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (token == null) {
            response.status = HttpStatus.UNAUTHORIZED.value()
            return false
        }

        try {
            JWT.require(Algorithm.HMAC256(jwtSecret)).build().verify(token)
            val username = JWT.decode(token).getClaim(Constant.JWT_CLAIM_USERNAME).asString()
            if (!redis.hasKey("${Constant.REDIS_KEY_MHP_USER_USERNAME}${username}")) {
                response.status = HttpStatus.UNAUTHORIZED.value()
                return false
            }
            return true
        } catch (e: JWTVerificationException) {
            response.status = HttpStatus.UNAUTHORIZED.value()
            return false
        }
    }
}
