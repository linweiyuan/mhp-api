package com.linweiyuan.mhp.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import kotlin.reflect.KClass

object JsonUtil {
    private val mapper = ObjectMapper().registerModule(KotlinModule())

    fun toJson(any: Any): String = mapper.writeValueAsString(any)

    fun <T : Any> fromJson(json: String, type: KClass<T>): T = mapper.readValue(json, type.java)
}
