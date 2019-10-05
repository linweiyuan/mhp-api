package com.linweiyuan.mhp.service.impl

import com.linweiyuan.mhp.common.Constant
import com.linweiyuan.mhp.common.Constant.QUEST_KEY
import com.linweiyuan.mhp.common.toHex
import com.linweiyuan.mhp.model.*
import com.linweiyuan.mhp.service.CodeService
import com.linweiyuan.misc.model.Data
import org.springframework.stereotype.Service
import java.util.*
import java.util.stream.Collectors

@Service
class CodeServiceImpl : CodeService {
    private val offset = ThreadLocal.withInitial { 0x1800 }

    override fun genCode(any: Any): Data {
        lateinit var codeName: String

        val codeMap = mutableMapOf<String, String>()
        when (any) {
            is Stone -> {
                genStoneCode(any, codeMap)
                codeName = "护石"
            }
            is Drink -> {
                genDrinkCode(any, codeMap)
                codeName = "饮料技能"
            }
            is Player -> {
                genPlayerCode(any, codeMap)
                codeName = "玩家信息"
            }
            is List<*> -> {
                @Suppress("UNCHECKED_CAST")
                genCatCode(any as List<Cat>, codeMap)
                codeName = "随从猫信息"
            }
            is Time -> {
                genTimeCode(any, codeMap)
                codeName = "游戏时间"
            }
            is WeaponNum -> {
                genWeaponNumCode(any, codeMap)
                codeName = "武器使用频率"
            }
            is QuestNum -> {
                genQuestNumCode(any, codeMap)
                codeName = "任务执行次数"
            }
            is BossNum -> {
                genBossNumCode(any, codeMap)
                codeName = "怪物狩猎记录"
            }
            is Quest -> {
                genQuestCode(any, codeMap)
                codeName = "自制任务"
            }
        }
        val code = StringBuilder("_C0 ${codeName}\n") // _C0:disable(default), _C1:enable
        codeMap.mapValues { (k, v) ->
            code.append("_L 0x$k 0x$v\n")
        }
        return Data(msg = "生成${codeName}成功", data = code)
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

    private fun genTimeCode(time: Time, codeMap: MutableMap<String, String>) {
        codeMap[(Constant.TIME_KEY).toHex()] = (time.hour * 60 * 60 + time.minute * 60).toHex() // 总秒数
    }

    private fun genWeaponNumCode(weaponNum: WeaponNum, codeMap: MutableMap<String, String>) {
        // manually fix
        if (weaponNum.type >= 5) {
            weaponNum.type = (weaponNum.type + 1).toByte()
        }
        val key = if (weaponNum.place.toInt() == 0) Constant.WEAPON_PLACE_KEY_1 else Constant.WEAPON_PLACE_KEY_2
        codeMap[(key + weaponNum.type * Constant.KEY_OFFSET_2).toHex()] = weaponNum.value.toHex()
    }

    private fun genQuestNumCode(questNum: QuestNum, codeMap: MutableMap<String, String>) {
        codeMap[(Constant.QUEST_NUM_KEY + questNum.type * Constant.KEY_OFFSET_2).toHex()] = questNum.value.toHex()
    }

    private fun genBossNumCode(bossNum: BossNum, codeMap: MutableMap<String, String>) {
        codeMap[(Constant.BOSS_NUM_KILL_KEY + Integer.parseInt(bossNum.gameId, 16) * Constant.KEY_OFFSET_2).toHex()] = bossNum.killNum.toHex()
        codeMap[(Constant.BOSS_NUM_CATCH_KEY + Integer.parseInt(bossNum.gameId, 16) * Constant.KEY_OFFSET_2).toHex()] = bossNum.catchNum.toHex()
    }

    private fun genQuestCode(quest: Quest, codeMap: MutableMap<String, String>) {
        genBasicCode(quest, codeMap)

        offset.set(0x2000)
        genShikyuCode(quest, codeMap)
        genHosyuCode(quest, codeMap)
        genMonsterCode(quest, codeMap)
        genBossCode(quest, codeMap)
    }

    private fun genBasicCode(quest: Quest, codeMap: MutableMap<String, String>) {
        val basic = quest.basic

        val tmpIntList = ArrayList<Int>()
        genQuestTextCode(basic.name, tmpIntList, codeMap)
        genQuestTextCode(basic.success, tmpIntList, codeMap)
        genQuestTextCode(basic.failure, tmpIntList, codeMap)
        genQuestTextCode(basic.content, tmpIntList, codeMap)
        genQuestTextCode(basic.monster, tmpIntList, codeMap)
        genQuestTextCode(basic.client, tmpIntList, codeMap)

        codeMap[(QUEST_KEY + offset.get()).toHex()] = (tmpIntList[0]).toHex()
        codeMap[(QUEST_KEY + offset.get() + Constant.KEY_OFFSET_4).toHex()] = tmpIntList[1].toHex()
        codeMap[(QUEST_KEY + offset.get() + 0x08).toHex()] = tmpIntList[2].toHex()
        codeMap[(QUEST_KEY + offset.get() + 0x0C).toHex()] = tmpIntList[3].toHex()
        codeMap[(QUEST_KEY + offset.get() + 0x10).toHex()] = tmpIntList[4].toHex()
        codeMap[(QUEST_KEY + offset.get() + 0x14).toHex()] = tmpIntList[5].toHex()
        codeMap[(QUEST_KEY + offset.get() + 0x18).toHex()] = offset.get().toHex()
        codeMap[(QUEST_KEY + offset.get() + 0x1C).toHex()] = offset.get().toHex()
        codeMap[(QUEST_KEY + offset.get() + 0x20).toHex()] = offset.get().toHex()
        codeMap[(QUEST_KEY + offset.get() + 0x24).toHex()] = offset.get().toHex()
        codeMap[(QUEST_KEY + offset.get() + 0x28).toHex()] = offset.get().toHex()
        codeMap[(QUEST_KEY + offset.get() + 0x2C).toHex()] = offset.get().toHex()
        codeMap[(QUEST_KEY + offset.get() + 0x30).toHex()] = offset.get().toHex()
        codeMap[(QUEST_KEY + 0xAC).toHex()] = (offset.get() + 0x18).toHex()
        offset.set(offset.get() + 0x34)

        /*上面文本设置, 下面基本设置*/

        codeMap[(QUEST_KEY + 0x7C).toHex()] = basic.successPts.toHex()
        codeMap[(QUEST_KEY + 0x80).toHex()] = basic.failPts.toHex()
        codeMap[(QUEST_KEY + 0x90).toHex()] = "0" + basic.startArea + "0" + (basic.bossSkill + 1) + "0" + (basic.pickRank + 1) + "00"
        codeMap[(QUEST_KEY + 0x94).toHex()] = (basic.bgm * 2).toHex(1) + (basic.returnTime * 8).toHex(1) + "0000" + (1 shl basic.questType.toInt()).toHex(2)
        codeMap[(QUEST_KEY + 0x98).toHex()] = basic.contractZ.toHex()
        codeMap[(QUEST_KEY + 0x9C).toHex()] = basic.rewardZ.toHex()
        codeMap[(QUEST_KEY + 0xA0).toHex()] = (basic.rewardZ / 3).toHex()
        codeMap[(QUEST_KEY + 0xA4).toHex()] = (basic.minute * 60 + basic.second * 30).toHex()
        codeMap[(QUEST_KEY + 0xA8).toHex()] = "00000000"
        codeMap[(QUEST_KEY + 0xB2).toHex()] = "0000000" + (basic.rank + 1)
        codeMap[(QUEST_KEY + 0xB4).toHex()] = "00" + basic.joinCondition2.toHex(2) + basic.joinCondition1.toHex(2) + (basic.map + 1).toHex(2)
        codeMap[(QUEST_KEY + 0xB8).toHex()] = "0" + (basic.successCondition + 1) + "000000"

        val successConditionTypeItem1 = basic.successConditionTypeItem1
        val successConditionTypeNum1 = basic.successConditionTypeNum1
        if (successConditionTypeNum1.toInt() == 0) {
            codeMap[(QUEST_KEY + 0xBC).toHex()] = "00000000"
            codeMap[(QUEST_KEY + 0xC0).toHex()] = "00000000"
        } else {
            codeMap[(QUEST_KEY + 0xBC).toHex()] = "0000000" + (basic.successConditionType1 + 1)
            codeMap[(QUEST_KEY + 0xC0).toHex()] = "00" + successConditionTypeNum1.toHex(2) + successConditionTypeItem1
        }
        val successConditionTypeItem2 = basic.successConditionTypeItem2
        val successConditionTypeNum2 = basic.successConditionTypeNum2
        if (successConditionTypeNum2.toInt() == 0) {
            codeMap[(QUEST_KEY + 0xC4).toHex()] = "00000000"
            codeMap[(QUEST_KEY + 0xC8).toHex()] = "00000000"
        } else {
            codeMap[(QUEST_KEY + 0xC4).toHex()] = "0000000" + (basic.successConditionType2 + 1)
            codeMap[(QUEST_KEY + 0xC8).toHex()] = "00" + successConditionTypeNum2.toHex(2) + successConditionTypeItem2
        }
        codeMap[(QUEST_KEY + 0xD0).toHex()] = basic.bossIcon2 + basic.bossIcon1
        codeMap[(QUEST_KEY + 0xD4).toHex()] = basic.bossIcon4 + basic.bossIcon3
        codeMap[(QUEST_KEY + 0xD8).toHex()] = "0000" + basic.bossIcon5
    }

    private fun genQuestTextCode(text: String, tmpIntList: MutableList<Int>, codeMap: MutableMap<String, String>) {
        tmpIntList.add(offset.get())

        val strs = ArrayList<String>()
        for (b in text.toByteArray()) {
            var str = Integer.toString(b.toInt() and 0xFF, 16).toUpperCase()
            if (str.length < 2) {
                str = "0$str" // 凑成2bit
            }
            strs.add(str)
        }

        var tmpStr = StringBuilder()
        for (i in strs.indices) {
            val str = strs[i]
            if (i != 0 && i % 4 == 0) {
                codeMap[(QUEST_KEY + offset.get()).toHex()] = tmpStr.toString()
                offset.set(offset.get() + Constant.KEY_OFFSET_4)
                tmpStr = StringBuilder(str)
            } else {
                tmpStr.insert(0, str)
            }
            if (i == strs.size - 1) {
                val tmp = StringBuilder()
                val length = tmpStr.length
                for (j in 0 until 8 - length) {
                    tmp.insert(0, "0")
                }
                tmpStr.insert(0, tmp.toString())
                codeMap[(QUEST_KEY + offset.get()).toHex()] = tmpStr.toString()
                offset.set(offset.get() + Constant.KEY_OFFSET_4)
                if (length == 8) {
                    codeMap[(QUEST_KEY + offset.get()).toHex()] = "00000000"
                    offset.set(offset.get() + Constant.KEY_OFFSET_4)
                }
            }
        }
    }

    private fun genShikyuCode(quest: Quest, codeMap: MutableMap<String, String>) {
        val shikyus = quest.shikyus

        shikyus.forEach { _ -> offset.set(offset.get() - Constant.KEY_OFFSET_4) }
        val tmpOffset = offset.get()
        var i = 0
        for ((gameId, num) in shikyus) {
            codeMap[(QUEST_KEY + offset.get()).toHex()] = num.toHex(4) + gameId
            offset.set(offset.get() + Constant.KEY_OFFSET_4)
            i++
        }
        codeMap[(QUEST_KEY + offset.get()).toHex()] = "0000" + i.toHex(2) + "00"
        codeMap[(QUEST_KEY + offset.get() + Constant.KEY_OFFSET_4).toHex()] = tmpOffset.toHex()
        codeMap[(QUEST_KEY + offset.get() + 0x08).toHex()] = "000000FF"
        codeMap[(QUEST_KEY + 0x08).toHex()] = offset.get().toHex()
        offset.set(offset.get() + 0x10)
        codeMap[(QUEST_KEY + 0x60).toHex()] = offset.get().toHex()
        offset.set(offset.get() + 0x30)
    }

    private fun genHosyuCode(quest: Quest, codeMap: MutableMap<String, String>) {
        val hosyus = quest.hosyus

        val tmpOffset1 = offset.get()
        var tmpOffset2 = 0
        if (!hosyus.isEmpty()) {
            val size = hosyus.size
            val odd = size % 2 != 0
            for (i in 0 until size) {
                if (odd) {
                    genHosyu(codeMap, hosyus, i)
                    if (i == size - 1) {
                        codeMap[(QUEST_KEY + offset.get()).toHex()] = hosyus[i].gameId + 100.toHex(4)
                        codeMap[(QUEST_KEY + offset.get() + Constant.KEY_OFFSET_4).toHex()] = "FFFF" + hosyus[i].num.toHex(4)
                        offset.set(offset.get() + 0x08)
                    }
                } else {
                    genHosyu(codeMap, hosyus, i)
                    if (i == size - 1) {
                        codeMap[(QUEST_KEY + offset.get()).toHex()] = "0000FFFF"
                        offset.set(offset.get() + 0x04)
                    }
                }
            }
            tmpOffset2 = offset.get()
            codeMap[(QUEST_KEY + offset.get()).toHex()] = 1.toHex(2) + "008000"
            codeMap[(QUEST_KEY + offset.get() + Constant.KEY_OFFSET_4).toHex()] = "00000003"
            codeMap[(QUEST_KEY + offset.get() + 0x08).toHex()] = tmpOffset1.toHex()
            offset.set(offset.get() + 0x0C)
        }
        codeMap[(QUEST_KEY + offset.get()).toHex()] = "0000FFFF"
        codeMap[(QUEST_KEY + 0x1C).toHex()] = tmpOffset2.toHex()
        offset.set(offset.get() + 0x0C)
    }

    private fun genHosyu(codeMap: MutableMap<String, String>, hosyus: MutableList<Hosyu>, i: Int) {
        if (i % 2 == 1) {
            codeMap[(QUEST_KEY + offset.get()).toHex()] = hosyus[i - 1].gameId + 100.toHex(4)
            codeMap[(QUEST_KEY + offset.get() + Constant.KEY_OFFSET_4).toHex()] = 100.toHex(4) + hosyus[i - 1].num.toHex(4)
            codeMap[(QUEST_KEY + offset.get() + 0x08).toHex()] = hosyus[i].num.toHex(4) + hosyus[i].gameId
            offset.set(offset.get() + 0x0C)
        }
    }

    private fun genMonsterCode(quest: Quest, codeMap: MutableMap<String, String>, num: Int? = null) {
        if (num == null) {
            val map = (quest.basic.map + 1).toHex(2)
            val areaNum = getAreaNum(map)
            val tmpIntList = ArrayList<Int>()
            for (i in 0..13) {
                tmpIntList.add(0)
            }
            tmpIntList[0] = offset.get()
            for (i in 0 until areaNum) {
                genMonsterCode(quest, codeMap, getArea(map, i))
                tmpIntList[i + 1] = offset.get()
            }
            tmpIntList[13] = offset.get()
            for (i in 0 until areaNum) {
                codeMap[(QUEST_KEY + offset.get()).toHex()] = i.toHex()
                codeMap[(QUEST_KEY + offset.get() + 0x08).toHex()] = (tmpIntList[i + 1] - 0x10).toHex()
                codeMap[(QUEST_KEY + offset.get() + 0x0C).toHex()] = tmpIntList[i].toHex()
                offset.set(offset.get() + 0x10)
            }
            offset.set(offset.get() + 0x10)
            codeMap[(QUEST_KEY + offset.get()).toHex()] = tmpIntList[13].toHex()
            codeMap[(QUEST_KEY + offset.get() + Constant.KEY_OFFSET_4).toHex()] = tmpIntList[13].toHex()
            codeMap[(QUEST_KEY + offset.get() + 0x08).toHex()] = tmpIntList[13].toHex()
            codeMap[(QUEST_KEY + 0x24).toHex()] = offset.get().toHex()
            offset.set(offset.get() + 0x10)
        } else {
            val monsters = quest.monsters
            val tmpStrList = ArrayList<String>()
            var tmpInt = 0
            for ((gameId, area, num1) in monsters) {
                if (area.toInt() != num) {
                    continue
                }
                tmpStrList.add(tmpInt++, gameId)
                codeMap[(QUEST_KEY + offset.get()).toHex()] = 0.toHex(4) + gameId
                codeMap[(QUEST_KEY + offset.get() + Constant.KEY_OFFSET_4).toHex()] = num.toHex(4) + "01" + num1.toHex(2)
                codeMap[(QUEST_KEY + offset.get() + 0x0C).toHex()] = "00005091"
                codeMap[(QUEST_KEY + offset.get() + 0x10).toHex()] = "C4518000"
                codeMap[(QUEST_KEY + offset.get() + 0x14).toHex()] = "00000000"
                codeMap[(QUEST_KEY + offset.get() + 0x18).toHex()] = "C4906000"
                offset.set(offset.get() + 0x30)
            }
            codeMap[(QUEST_KEY + offset.get()).toHex()] = "0000FFFF"
            offset.set(offset.get() + 0x30)
            for (i in 0..3) {
                if (i < tmpInt) {
                    codeMap[(QUEST_KEY + offset.get()).toHex()] = "0000" + tmpStrList[i]
                } else {
                    codeMap[(QUEST_KEY + offset.get()).toHex()] = "FFFFFFFF"
                }
                offset.set(offset.get() + Constant.KEY_OFFSET_4)
            }
        }

    }

    private fun getAreaNum(map: String) = when (map) {
        "01", "0E" -> 13
        "02", "05", "0F", "12" -> 12
        "03", "10" -> 11
        "04", "0C", "11", "13" -> 10
        "06" -> 3
        else -> 2
    }

    private fun getArea(map: String, areaNum: Int): Int {
        when (map) {
            "01", "0E" -> {
                if (areaNum == 1) return 1
                if (areaNum == 3) return 2
                if (areaNum == 2) return 3
                if (areaNum == 11) return 4
                if (areaNum == 5) return 5
                if (areaNum == 9) return 6
                if (areaNum == 4) return 7
                if (areaNum == 10) return 8
                if (areaNum == 6) return 9
                if (areaNum == 12) return 10
            }
            "02", "0F" -> {
                if (areaNum == 1) return 1
                if (areaNum == 2) return 2
                if (areaNum == 3) return 3
                if (areaNum == 4) return 4
                if (areaNum == 5) return 5
                if (areaNum == 11) return 6
                if (areaNum == 9) return 7
                if (areaNum == 6) return 8
                if (areaNum == 7) return 9
                if (areaNum == 8) return 10
                if (areaNum == 10) return 11
            }
            "03", "10" -> {
                if (areaNum == 1) return 1
                if (areaNum == 5) return 2
                if (areaNum == 2) return 3
                if (areaNum == 3) return 4
                if (areaNum == 4) return 5
                if (areaNum == 8) return 6
                if (areaNum == 7) return 7
                if (areaNum == 6) return 8
                if (areaNum == 9) return 9
                if (areaNum == 10) return 10
            }
            "04", "11" -> {
                if (areaNum == 1) return 1
                if (areaNum == 3) return 2
                if (areaNum == 2) return 3
                if (areaNum == 6) return 4
                if (areaNum == 4) return 5
                if (areaNum == 7) return 6
                if (areaNum == 5) return 7
                if (areaNum == 8) return 8
                if (areaNum == 9) return 9
            }
            "05", "12" -> {
                if (areaNum == 2) return 1
                if (areaNum == 1) return 2
                if (areaNum == 3) return 3
                if (areaNum == 6) return 4
                if (areaNum == 4) return 5
                if (areaNum == 5) return 6
                if (areaNum == 7) return 7
                if (areaNum == 8) return 8
                if (areaNum == 10) return 9
                if (areaNum == 9) return 10
            }
            "06" -> {
                if (areaNum == 1) return 1
                if (areaNum == 2) return 2
            }
            "0C", "13" -> {
                if (areaNum == 1) return 1
                if (areaNum == 2) return 2
                if (areaNum == 3) return 3
                if (areaNum == 4) return 4
                if (areaNum == 5) return 5
                if (areaNum == 6) return 6
                if (areaNum == 7) return 7
                if (areaNum == 8) return 8
                if (areaNum == 9) return 9
            }
            else -> if (areaNum == 1) return 1
        }
        return 0
    }

    private fun genBossCode(quest: Quest, codeMap: MutableMap<String, String>) {
        val bosses = quest.bosses
        for (i in 0..5) {
            codeMap[(QUEST_KEY + 0x30 + i * 0x08).toHex()] = "0000" + 100.toHex(4)
            codeMap[(QUEST_KEY + 0x30 + i * 0x08 + Constant.KEY_OFFSET_4).toHex()] = "00000000"
        }
        for (i in bosses.indices) {
            val boss = bosses[i]
            codeMap[(QUEST_KEY + 0x30 + i * 0x08).toHex()] = boss.hp.toHex(2) + "00" + boss.size.toHex(4)
            codeMap[(QUEST_KEY + 0x30 + i * 0x08 + Constant.KEY_OFFSET_4).toHex()] = "00" + boss.fatigue.toHex(2) + boss.endurance.toHex(2) + boss.strength.toHex(2)
            boss.round = (boss.round + 1).toByte()
        }
        val tmpIntList = ArrayList<Int>()
        tmpIntList.add(offset.get())
        for (i in 1..6) {
            genBossDetail(quest, bosses, i, codeMap)
            tmpIntList.add(offset.get())
        }
        for (i in 0..5) {
            codeMap[(QUEST_KEY + offset.get()).toHex()] = "00000001"
            codeMap[(QUEST_KEY + offset.get() + 0x08).toHex()] = (tmpIntList[i + 1] - 0x10).toHex()
            codeMap[(QUEST_KEY + offset.get() + 0x0C).toHex()] = tmpIntList[i].toHex()
            offset.set(offset.get() + 0x10)
        }
        codeMap[(QUEST_KEY + 0x20).toHex()] = tmpIntList[6].toHex()
        offset.set(offset.get() + 0x10)
    }

    private fun genBossDetail(quest: Quest, bosses: List<Boss>, round: Int, codeMap: MutableMap<String, String>) {
        if (bosses.isEmpty()) {
            codeMap[(QUEST_KEY + offset.get()).toHex()] = "0000FFFF"
            codeMap[(QUEST_KEY + offset.get() + 0x30).toHex()] = "FFFFFFFF"
            codeMap[(QUEST_KEY + offset.get() + 0x34).toHex()] = "FFFFFFFF"
            codeMap[(QUEST_KEY + offset.get() + 0x38).toHex()] = "FFFFFFFF"
            codeMap[(QUEST_KEY + offset.get() + 0x3C).toHex()] = "FFFFFFFF"
            offset.set(offset.get() + 0x40)
        } else {
            bosses.stream()
                .filter { boss -> boss.round == round.toByte() }
                .forEach { (gameId, status, num, area) ->
                    codeMap[(QUEST_KEY + offset.get()).toHex()] = status.toHex(4) + gameId
                    codeMap[(QUEST_KEY + offset.get() + Constant.KEY_OFFSET_4).toHex()] = getAreaId((quest.basic.map + 1).toHex(2), area.toHex(4)) + num.toHex(4)
                    codeMap[(QUEST_KEY + offset.get() + 0x0C).toHex()] = "00006AE8"
                    codeMap[(QUEST_KEY + offset.get() + 0x10).toHex()] = "C49D699A"
                    codeMap[(QUEST_KEY + offset.get() + 0x14).toHex()] = "00000000"
                    codeMap[(QUEST_KEY + offset.get() + 0x18).toHex()] = "4301E666"
                    offset.set(offset.get() + 0x30)
                }
        }
        val tmpBossList = bosses.stream().filter { boss -> boss.round == round.toByte() }.collect(Collectors.toList<Boss>())
        if (tmpBossList.isNotEmpty()) {
            codeMap[(QUEST_KEY + offset.get()).toHex()] = "0000FFFF"
            codeMap[(QUEST_KEY + offset.get() + 0x30).toHex()] = "0000" + tmpBossList[0].gameId
            codeMap[(QUEST_KEY + offset.get() + 0x30).toHex()] = if (tmpBossList.size == 2) "0000" + tmpBossList[1].gameId else "FFFFFFFF"
            codeMap[(QUEST_KEY + offset.get() + 0x38).toHex()] = "FFFFFFFF"
            codeMap[(QUEST_KEY + offset.get() + 0x3C).toHex()] = "FFFFFFFF"
            offset.set(offset.get() + 0x40)
        }
    }

    private fun getAreaId(map: String, area: String): String {
        when (map) {
            "01", "0E" -> {
                if (area == "0001") return "0001"
                if (area == "0002") return "0003"
                if (area == "0003") return "0002"
                if (area == "0004") return "000B"
                if (area == "0005") return "0005"
                if (area == "0006") return "0009"
                if (area == "0007") return "0004"
                if (area == "0008") return "000A"
                if (area == "0009") return "0006"
                return if (area == "000A") "000C" else "0000"
            }
            "02", "0F" -> {
                if (area == "0001") return "0001"
                if (area == "0002") return "0002"
                if (area == "0003") return "0003"
                if (area == "0004") return "0004"
                if (area == "0005") return "0005"
                if (area == "0006") return "000B"
                if (area == "0007") return "0009"
                if (area == "0008") return "0006"
                if (area == "0009") return "0007"
                if (area == "000A") return "0008"
                return if (area == "000B") "000A" else "0000"
            }
            "03", "10" -> {
                if (area == "0001") return "0001"
                if (area == "0002") return "0005"
                if (area == "0003") return "0002"
                if (area == "0004") return "0003"
                if (area == "0005") return "0004"
                if (area == "0006") return "0008"
                if (area == "0007") return "0007"
                if (area == "0008") return "0006"
                if (area == "0009") return "0009"
                return if (area == "000A") "000A" else "0000"
            }
            "04", "11" -> {
                if (area == "0001") return "0001"
                if (area == "0002") return "0003"
                if (area == "0003") return "0002"
                if (area == "0004") return "0006"
                if (area == "0005") return "0004"
                if (area == "0006") return "0007"
                if (area == "0007") return "0005"
                if (area == "0008") return "0008"
                return if (area == "0009") "0009" else "0000"
            }
            "05", "12" -> {
                if (area == "0001") return "0002"
                if (area == "0002") return "0001"
                if (area == "0003") return "0003"
                if (area == "0004") return "0006"
                if (area == "0005") return "0004"
                if (area == "0006") return "0005"
                if (area == "0007") return "0007"
                if (area == "0008") return "0008"
                if (area == "0009") return "000A"
                return if (area == "000A") "0009" else "0000"
            }
            "06" -> {
                if (area == "0001") return "0001"
                return if (area == "0002") "0002" else "0000"
            }
            "0C", "13" -> {
                if (area == "0001") return "0001"
                if (area == "0002") return "0002"
                if (area == "0003") return "0003"
                if (area == "0004") return "0004"
                if (area == "0005") return "0005"
                if (area == "0006") return "0006"
                if (area == "0007") return "0007"
                if (area == "0008") return "0008"
                return if (area == "0009") "0009" else "0000"
            }
            else -> {
                return if (area == "0001") "0001" else "0000"
            }
        }
    }
}
