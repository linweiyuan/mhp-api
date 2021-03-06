package com.linweiyuan.mhp.api

import com.linweiyuan.mhp.model.*
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

    @ApiOperation("饮料技能")
    @PostMapping("/drink")
    fun drink(@RequestBody drink: Drink) = codeService.genCode(drink)

    @ApiOperation("玩家信息")
    @PostMapping("/player")
    fun player(@RequestBody player: Player) = codeService.genCode(player)

    @ApiOperation("随从猫信息")
    @PostMapping("/cat")
    fun cat(@RequestBody cats: List<Cat>) = codeService.genCode(cats)

    @ApiOperation("游戏时间")
    @PostMapping("/time")
    fun time(@RequestBody time: Time) = codeService.genCode(time)

    @ApiOperation("武器使用频率")
    @PostMapping("/weaponNum")
    fun weaponNum(@RequestBody weaponNum: WeaponNum) = codeService.genCode(weaponNum)

    @ApiOperation("任务执行次数")
    @PostMapping("/questNum")
    fun questNum(@RequestBody questNum: QuestNum) = codeService.genCode(questNum)

    @ApiOperation("怪物狩猎记录")
    @PostMapping("/bossNum")
    fun bossNum(@RequestBody bossNum: BossNum) = codeService.genCode(bossNum)

    @ApiOperation("自制任务")
    @PostMapping("/quest")
    fun quest(@RequestBody quest: Quest) = codeService.genCode(quest)
}
