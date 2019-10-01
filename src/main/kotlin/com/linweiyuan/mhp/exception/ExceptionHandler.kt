package com.linweiyuan.mhp.exception

import com.linweiyuan.misc.model.Code
import com.linweiyuan.misc.model.Data
import com.linweiyuan.misc.util.ExceptionUtil
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionController {
    @ExceptionHandler(Throwable::class)
    fun handleException(t: Throwable): Data {
        ExceptionUtil.print(t)
        return Data(Code.ERR, "系统异常 -> " + t.localizedMessage)
    }
}
