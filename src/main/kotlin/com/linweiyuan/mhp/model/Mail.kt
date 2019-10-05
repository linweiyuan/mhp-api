package com.linweiyuan.mhp.model

import java.io.Serializable

data class Mail(
    val from: String,
    val to: String,
    val subject: String,
    val text: String
) : Serializable
