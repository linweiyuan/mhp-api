package com.linweiyuan.mhp.api

import com.linweiyuan.mhp.entity.User
import com.linweiyuan.mhp.service.UserService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Api(description = "用户接口")
@RestController
@RequestMapping("/user")
class UserApi {
    @Autowired
    private lateinit var userService: UserService

    @ApiOperation("注册")
    @PostMapping("/register")
    fun register(@ApiParam(value = "用户", required = true) @RequestBody user: User) = userService.register(user)

    @ApiOperation("验证")
    @PostMapping("/validate")
    fun validate(@ApiParam(value = "用户", required = true) @RequestBody user: User) = userService.validate(user)

    @ApiOperation("登录")
    @PostMapping("/login")
    fun login(@ApiParam(value = "用户", required = true) @RequestBody user: User) = userService.login(user)
}
