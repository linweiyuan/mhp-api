package com.linweiyuan.mhp.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*
import javax.persistence.*

@Entity
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    val id: Int,
    val username: String,
    val password: String,
    @JsonIgnore
    var regTime: Date?,
    @JsonIgnore
    var loginTime: Date?,
    @Transient
    var regCode: String?
)
