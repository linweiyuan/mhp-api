# 怪物猎人金手指API（3rd HD ver.）
## 表
```sql
CREATE DATABASE `mhp`;
USE `mhp`;
CREATE TABLE `user` (
  `id` tinyint(3) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名（邮箱）',
  `password` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `reg_time` timestamp NOT NULL COMMENT '注册时间',
  `login_time` timestamp NULL DEFAULT NULL COMMENT '最近一次登录时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户';
```
---
## 部署
```yaml
  mhp-api:
    container_name: mhp-api
    image: openjdk:8-jre-alpine
    ports:
      - 8080:8080
    volumes:
      - /home/linweiyuan/.project/mhp-api/target/mhp-api-0.0.1.jar:/mhp-api-0.0.1.jar
    environment:
      - TZ=Asia/Shanghai
    command: java -jar -Xms64m -Xmx64m /mhp-api-0.0.1.jar
```
---
## 用户接口
### 注册
```java
POST /mhp/user/register
```
```json
{
    "username": "用户名（邮箱）",
    "password": "密码"
}
```
---
### 验证
```java
POST /mhp/user/validate
```
```json
{
    "username": "用户名",
    "regCode": "验证码"
}
```
---
### 登录
```java
POST /mhp/user/login
```
```json
{
    "username": "用户名",
    "password": "密码"
}
```
---
## 代码接口
### 护石
```java
POST /mhp/code/stone
```
```json
{
    "skill1": 1,
    "point1": 2,
    "skill2": 3,
    "point2": 4,
    "rarity": 5,
    "slot": 6
}
```
---
### 饮料技能
```java
POST /mhp/code/drink
```
```json
{
    "skill1": 1,
    "skill2": 2,
    "skill3": 3,
    "skill4": 4,
    "skill5": 5
}
```
---
### 玩家信息
```java
POST /mhp/code/player
```
```json
{
    "name": "张三",
    "intro": "一个猎人"
}
```
### 随从猫信息
```java
POST /mhp/code/cat
```
```json
[
    {
        "name": "喵1",
        "owner": "李四",
        "intro": "喵喵喵"
    },
    {
        "name": "喵2",
        "owner": "王五",
        "intro": "喵喵喵"
    }
]
```
---
### 游戏时间
```java
POST /mhp/code/time
```
```json
{
    "hour": 1,
    "minute": 2
}
```
---
### 武器使用频率
```java
POST /mhp/code/weaponNum
```
```json
{
    "type": 0,
    "place": 1,
    "value": 123
}
```
---
### 任务执行次数
```java
POST /mhp/code/questNum
```
```json
{
    "type": 0,
    "value": 123
}
```
---
### 怪物狩猎记录
```java
POST /mhp/code/bossNum
```
```json
{
    "gameId": "000A",
    "killNum": 123,
    "catchNum": 456
}
```
---
### 自制任务（不完善，设置不当易死机）
```java
POST /mhp/code/quest
```
```json
{
    "basic": {
        "bgm": 0,
        "bossIcon1": "0002",
        "bossIcon2": "0000",
        "bossIcon3": "0000",
        "bossIcon4": "0000",
        "bossIcon5": "0000",
        "bossSkill": 0,
        "client": "6",
        "content": "2",
        "contractZ": 100,
        "failPts": 15,
        "failure": "4",
        "joinCondition1": 1,
        "joinCondition2": 19,
        "map": 0,
        "minute": 50,
        "monster": "5",
        "name": "1",
        "pickRank": 0,
        "questType": 0,
        "rank": 0,
        "returnTime": 0,
        "rewardZ": 1200,
        "second": 0,
        "startArea": 0,
        "success": "3",
        "successCondition": 0,
        "successConditionType1": 0,
        "successConditionType2": 0,
        "successConditionTypeItem1": "0001",
        "successConditionTypeItem2": "0000",
        "successConditionTypeNum1": 1,
        "successConditionTypeNum2": 0,
        "successPts": 100
    },
    "bosses": [{
        "area": 2,
        "endurance": 3,
        "fatigue": 4,
        "gameId": "0001",
        "hp": 1,
        "num": 1,
        "round": 0,
        "size": 100,
        "status": 1,
        "strength": 2
    }],
	"hosyus": [{
        "gameId": "0002",
        "num": 2
    }],
    "monsters": [{
        "area": 1,
        "gameId": "000A",
        "num": 1
    }],
    "shikyus": [{
        "gameId": "0001",
        "num": 1
    }]
}
```
