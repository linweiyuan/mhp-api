package com.linweiyuan.mhp.api

import com.linweiyuan.mhp.model.Stone
import com.linweiyuan.mhp.service.CodeService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Api(description = "金手指代码生成接口")
@RestController
@RequestMapping("/code")
class CodeApi {
    @Autowired
    private lateinit var codeService: CodeService

    @ApiOperation("护石")
    @PostMapping("/stone")
    fun stone(@RequestBody stone: Stone) = codeService.genCode(stone)
}
