package com.linweiyuan.mhp.service.impl

import com.linweiyuan.mhp.common.Constant
import com.linweiyuan.mhp.common.toHex
import com.linweiyuan.mhp.model.Stone
import com.linweiyuan.mhp.service.CodeService
import com.linweiyuan.misc.model.Data
import org.springframework.stereotype.Service

@Service
class CodeServiceImpl : CodeService {
    override fun genCode(any: Any): Data {
        lateinit var codeName: String
        lateinit var msg: String

        val codeMap = mutableMapOf<Number, Number>()
        when (any) {
            is Stone -> {
                genStoneCode(any, codeMap)
                codeName = "护石${Constant.CODE_NAME_SUFFIX}"
                msg = "生成护石代码成功"
            }
        }
        val code = StringBuilder("_C0 ${codeName}\n") // _C0:disable(default), _C1:enable
        for ((k, v) in codeMap) {
            code.append("_L 0x${k.toHex()} 0x${v.toHex()}\n")
        }
        return Data(msg = msg, data = code)
    }

    private fun genStoneCode(stone: Stone, codeMap: MutableMap<Number, Number>) {
        codeMap[Constant.STONE_KEY] = Constant.STONE_VALUE + 0x00010000 * stone.rarity + 0x00100000 * stone.point1 + 0x04000000 * stone.point2
        codeMap[Constant.STONE_KEY + Constant.KEY_OFFSET_4] = 0x00000001 * stone.skill1 + 0x00000080 * stone.skill2 + 0x00004000 * stone.slot
    }
}
