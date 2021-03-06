package com.linweiyuan.mhp.service

import com.linweiyuan.mhp.entity.User
import com.linweiyuan.misc.model.Data

interface UserService {
    fun register(user: User): Data

    fun validate(user: User): Data

    fun login(user: User): Data
}
