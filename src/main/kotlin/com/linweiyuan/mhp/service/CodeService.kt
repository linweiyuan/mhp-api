package com.linweiyuan.mhp.service

import com.linweiyuan.misc.model.Data

interface CodeService {
    fun genCode(any: Any): Data
}
