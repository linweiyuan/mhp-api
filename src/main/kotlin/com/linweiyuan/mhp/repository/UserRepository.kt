package com.linweiyuan.mhp.repository

import com.linweiyuan.mhp.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Int> {
    fun findByUsername(username: String?): User?
}
