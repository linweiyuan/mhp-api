package com.linweiyuan.mhp.service.impl

import com.linweiyuan.mhp.common.Constant
import com.linweiyuan.mhp.common.toHex
import com.linweiyuan.mhp.model.Cat
import com.linweiyuan.mhp.model.Drink
import com.linweiyuan.mhp.model.Player
import com.linweiyuan.mhp.model.Stone
import com.linweiyuan.mhp.service.CodeService
import com.linweiyuan.misc.model.Data
import org.springframework.stereotype.Service

@Service
class CodeServiceImpl : CodeService {
    override fun genCode(any: Any): Data {
        lateinit var codeName: String
        lateinit var msg: String

        val codeMap = mutableMapOf<String, String>()
        when (any) {
            is Stone -> {
                genStoneCode(any, codeMap)
                codeName = "护石${Constant.CODE_NAME_SUFFIX}"
                msg = "生成护石代码成功"
            }
            is Drink -> {
                genDrinkCode(any, codeMap)
                codeName = "饮料技能${Constant.CODE_NAME_SUFFIX}"
                msg = "生成饮料技能代码成功"
            }
            is Player -> {
                genPlayerCode(any, codeMap)
                codeName = "玩家信息${Constant.CODE_NAME_SUFFIX}"
                msg = "生成玩家信息代码成功"
            }
            is List<*> -> {
                @Suppress("UNCHECKED_CAST")
                genCatCode(any as List<Cat>, codeMap)
                codeName = "随从猫信息${Constant.CODE_NAME_SUFFIX}"
                msg = "生成随从猫信息代码成功"
            }
        }
        val code = StringBuilder("_C0 ${codeName}\n") // _C0:disable(default), _C1:enable
        for ((k, v) in codeMap) {
            code.append("_L 0x$k 0x$v\n")
        }
        return Data(msg = msg, data = code)
    }

    private fun genStoneCode(stone: Stone, codeMap: MutableMap<String, String>) {
        codeMap[Constant.STONE_KEY.toHex()] = (Constant.STONE_VALUE + 0x00010000 * stone.rarity + 0x00100000 * stone.point1 + 0x04000000 * stone.point2).toHex()
        codeMap[(Constant.STONE_KEY + Constant.KEY_OFFSET_4).toHex()] = (0x00000001 * stone.skill1 + 0x00000080 * stone.skill2 + 0x00004000 * stone.slot).toHex()
    }

    private fun genDrinkCode(drink: Drink, codeMap: MutableMap<String, String>) {
        codeMap[Constant.DRINK_KEY.toHex()] = (0x01000000 * drink.skill4 + 0x00010000 * drink.skill3 + 0x00000100 * drink.skill2 + 0x00000001 * drink.skill1).toHex()
        codeMap[(Constant.DRINK_KEY + Constant.KEY_OFFSET_4).toHex()] = drink.skill5.toHex()
    }

    private fun genPlayerCode(player: Player, codeMap: MutableMap<String, String>) {
        codeMap.putAll(genTextCode(Constant.PLAYER_NAME_KEY, player.name))
        codeMap.putAll(genTextCode(Constant.PLAYER_CARD_KEY, player.name))
        codeMap.putAll(genTextCode(Constant.PLAYER_CARD_INTRO_KEY, player.intro))
    }

    /**
     * 文字生成顺序
     * 比如: 12345
     * 生成顺序为:
     * 2     1
     * 4     3
     * 0补全 5
     * 各占4bit
     */
    private fun genTextCode(key: Int, name: String): MutableMap<String, String> {
        val textCode = mutableMapOf<String, String>()
        val length = name.length
        val odd = length % 2 != 0
        var tmpOffset = 0
        for (i in 0 until length step 2) {
            if (odd && i == length - 1) {
                break
            }
            tmpOffset = i / 2
            textCode[(key + Constant.KEY_OFFSET_4 * tmpOffset).toHex()] = name[i + 1].toLong().toHex(4) + name[i].toLong().toHex(4)
        }
        if (odd) {
            tmpOffset = length / 2
            textCode[(key + Constant.KEY_OFFSET_4 * tmpOffset).toHex()] = name[length - 1].toLong().toHex()
        }
        var i = tmpOffset + 1
        while (textCode.size < 6) {
            textCode[(key + Constant.KEY_OFFSET_4 * i).toHex()] = "00000000"
            i++
        }
        return textCode
    }

    private fun genCatCode(cats: List<Cat>, codeMap: MutableMap<String, String>) {
        for (i in cats.indices) {
            val (name, owner, intro) = cats[i]
            codeMap.putAll(genTextCode(Constant.CAT_NAME_KEY + Constant.KEY_OFFSET_CAT * i, name))
            codeMap.putAll(genTextCode(Constant.CAT_OWNER_KEY + Constant.KEY_OFFSET_CAT * i, owner))
            codeMap.putAll(genTextCode(Constant.CAT_INTRO_KEY + Constant.KEY_OFFSET_CAT * i, intro))
        }
    }
}
