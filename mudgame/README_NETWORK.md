# MUD游戏网络版使用说明

## 概述

这是一个基于TCP协议的多人在线MUD武侠游戏，支持最多4个客户端同时连接。游戏采用场景系统，玩家可以在不同的场景中探索、战斗、与NPC对话，体验丰富的武侠世界。

## 功能特性

1. **多人在线游戏**：支持最多4个玩家同时在线
2. **角色系统**：可选择两种角色（萧言/莫），每个角色有独特的技能
3. **场景系统**：5个不同的游戏场景，每个场景有不同的功能
4. **打怪升级**：与随机生成的怪物战斗获得经验，战斗结束后自动回血
5. **NPC对话**：与NPC对话回答选择题获得经验奖励（支持选择题模式）
6. **玩家交互**：查看在线玩家列表、实时聊天系统
7. **玩家PK**：向其他在线玩家发起挑战进行PK战斗（支持状态锁定）
8. **存档系统**：使用JSON格式保存和加载角色数据

## 文件说明

### 服务器端
- `NetworkGameServer.java` - 游戏服务器主程序
- `NetworkBattleSystem.java` - 网络版战斗系统（支持PVE和PVP）
- `JsonUtil.java` - JSON工具类，用于读写角色和NPC数据
- `Scene.java` - 场景类，定义游戏场景
- `npcs.json` - NPC对话数据配置文件（支持选择题）

### 客户端
- `NetworkGameClient.java` - 游戏客户端程序

### 其他核心文件
- `Figure.java` - 角色基类
- `Monster.java` - 怪物类
- `Npc.java` - NPC类（支持选择题）
- `Skill.java` - 技能类
- `XiaoYan.java` - 玩家角色类（萧言）
- `Mo.java` - 玩家角色类（莫）

## 使用方法

### 1. 编译项目

首先编译所有Java文件（包含Gson库）：
```bash
javac -encoding UTF-8 -cp "lib/gson-2.10.1.jar" -d out/production/USST src/com/mudgame/NetworkGameServer.java src/com/mudgame/NetworkGameClient.java src/com/mudgame/NetworkBattleSystem.java src/com/mudgame/JsonUtil.java src/com/mudgame/Figure.java src/com/mudgame/XiaoYan.java src/com/mudgame/Mo.java src/com/mudgame/Skill.java src/com/mudgame/Npc.java src/com/mudgame/Monster.java src/com/mudgame/Scene.java
```

### 2. 启动服务器

**方法一：使用启动脚本（推荐，解决中文乱码）**

**Windows CMD:**
```bash
start_server.bat
```

**Windows PowerShell:**
```bash
.\start_server.ps1
```

**方法二：手动启动**
```bash
# Windows
java -Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8 -cp "out/production/USST;lib/gson-2.10.1.jar" com.mudgame.NetworkGameServer

# Linux/Mac
java -Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8 -cp "out/production/USST:lib/gson-2.10.1.jar" com.mudgame.NetworkGameServer
```

服务器默认监听端口：**4444**

### 3. 启动客户端

在不同的终端或机器上运行客户端（最多4个）：

**方法一：使用启动脚本（推荐，解决中文乱码）**

**Windows CMD:**
```bash
# 连接本地服务器
start_client.bat

# 连接指定服务器
start_client.bat <服务器地址> <端口>
```

**Windows PowerShell:**
```bash
# 连接本地服务器
.\start_client.ps1

# 连接指定服务器
.\start_client.ps1 <服务器地址> <端口>
```

**方法二：手动启动**
```bash
# Windows - 连接本地服务器
java -Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8 -cp "out/production/USST;lib/gson-2.10.1.jar" com.mudgame.NetworkGameClient

# Windows - 连接指定服务器
java -Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8 -cp "out/production/USST;lib/gson-2.10.1.jar" com.mudgame.NetworkGameClient <服务器地址> <端口>

# Linux/Mac
java -Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8 -cp "out/production/USST:lib/gson-2.10.1.jar" com.mudgame.NetworkGameClient <服务器地址> <端口>
```

**注意：** 使用启动脚本可以自动设置UTF-8编码，解决中文乱码问题。如果手动启动，请确保添加 `-Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8` 参数。

### 4. 游戏操作

#### 角色选择

连接成功后，首先选择角色类型：
```
请选择角色类型:
1. 萧言 - 出生于蛇岛，擅长火系技能
2. 莫 - 出生于云山，擅长风系技能
请选择(1-2):
```

#### 主菜单

选择角色后，会显示当前场景地图和主菜单：

```
┌─────────────────────────────────┐
│         新手村        │
├─────────────────────────────────┤
│ 宁静的村庄，适合新手修炼 │
└─────────────────────────────────┘

当前位置: 新手村
场景描述: 宁静的村庄，适合新手修炼

===== 主菜单 =====
1. 打怪练级
2. NPC对话
3. 查看状态
4. 保存游戏
5. 加载游戏
6. 查看在线玩家
7. 聊天 (chat <消息> 或直接输入消息)
8. PK挑战 (pk <玩家名>)
9. 切换场景
0. 退出游戏
请选择(0-9):
```

**命令说明：**

- **输入 `1` 或 `battle`**：开始打怪
  - 战斗中使用 `11` 普通攻击，`12` 使用技能
  - 战斗结束后自动恢复满血

- **输入 `2` 或 `npc`**：与NPC对话
  - NPC会提出问题，以选择题形式回答
  - 选择正确的选项可获得经验奖励

- **输入 `3` 或 `status`**：查看角色状态
  - 显示角色等级、血量、攻击、防御、经验、技能等信息

- **输入 `4` 或 `save`**：保存游戏（JSON格式）
  - 角色数据会保存到服务器端

- **输入 `5` 或 `load`**：加载游戏
  - 从服务器端加载之前保存的角色数据

- **输入 `6` 或 `list`**：查看在线玩家列表
  - 显示所有在线玩家及其状态（PK中/战斗中/可挑战）

- **输入 `7 chat <消息>` 或直接输入消息**：聊天
  - 支持两种方式：`chat 消息内容` 或直接输入消息内容
  - 消息会广播给所有在线玩家，带时间戳

- **输入 `8 pk <玩家名>`**：向指定玩家发起PK挑战
  - 被挑战的玩家输入 `accept` 接受挑战
  - 被挑战的玩家输入 `reject` 拒绝挑战
  - PK中使用 `11` 普通攻击，`12` 使用技能
  - PK中的玩家无法被其他玩家挑战

- **输入 `9` 或 `scene`**：切换场景
  - 显示所有可用场景列表
  - 选择场景编号进行切换
  - 不同场景有不同的功能

- **输入 `0` 或 `quit`**：退出游戏

## 游戏场景

游戏包含5个不同的场景，每个场景有不同的特色：

1. **新手村** - 宁静的村庄，适合新手修炼
   - 可进行：打怪练级、NPC对话

2. **武林广场** - 江湖人士聚集地，可以切磋武艺
   - 可进行：打怪练级、NPC对话、PK挑战

3. **藏经阁** - 存放武学秘籍的地方
   - 可进行：NPC对话、查看状态

4. **练功房** - 专门用于修炼的场所
   - 可进行：打怪练级、查看状态

5. **江湖客栈** - 休息和交流的地方
   - 可进行：NPC对话、查看在线玩家、聊天

## 战斗系统

### PVE战斗（打怪）

- 战斗中使用 `11` 进行普通攻击，`12` 使用技能
- 战斗结束后自动恢复满血
- 击败怪物可获得经验值

### PVP战斗（玩家PK）

- PK中使用 `11` 进行普通攻击，`12` 使用技能
- 只能输入 `11` 或 `12`，其他输入会提示错误
- PK中的玩家无法被其他玩家挑战
- 战斗中的玩家无法被挑战
- 获胜者获得50点经验奖励

## NPC对话系统

NPC对话采用选择题模式：

```
你遇到 NPC：书生
年轻人，你可知武学的真谛？

武林中最强的是什么？

请选择答案:
1. 心
2. 剑
3. 内功
4. 外功
请输入选项编号(1-4):
```

- 输入选项编号（1-4）即可回答
- 回答正确获得经验奖励
- NPC数据存储在 `npcs.json` 文件中

## 数据存储

### NPC数据（npcs.json）

NPC对话数据存储在 `src/com/mudgame/npcs.json` 文件中，格式如下：
```json
[
  {
    "id": 1,
    "name": "书生",
    "question": "武林中最强的是什么？",
    "answer": "心",
    "exp": 50,
    "dialogue": "年轻人，你可知武学的真谛？",
    "options": [
      "心",
      "剑",
      "内功",
      "外功"
    ]
  }
]
```

### 角色存档

每个玩家的存档保存在 `src/com/mudgame/save_<玩家名>.json` 文件中，使用JSON格式存储角色数据，包括角色类型（萧言/莫）。

## 网络协议

- **协议**：TCP
- **默认端口**：4444
- **通信方式**：文本命令（一行一条命令）
- **编码**：UTF-8

## 注意事项

1. 确保服务器先启动，再启动客户端
2. 最多支持4个客户端同时连接
3. PK战斗是回合制的，需要双方玩家依次操作
4. 游戏数据保存在服务器端的JSON文件中
5. 如果服务器关闭，所有客户端连接会断开
6. **中文乱码问题**：使用提供的启动脚本（`.bat` 或 `.ps1`）可以自动设置UTF-8编码，解决中文显示乱码问题
7. 如果使用命令行手动启动，请确保：
   - 添加 `-Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8` 参数
   - 在CMD中运行 `chcp 65001` 设置代码页为UTF-8
   - 在PowerShell中设置 `[Console]::OutputEncoding = [System.Text.Encoding]::UTF8`
8. **PK系统**：PK中只能输入 `11` 或 `12`，其他输入会提示错误
9. **状态锁定**：PK中或战斗中的玩家无法被其他玩家挑战
10. **自动回血**：每次战斗结束后自动恢复满血

## 游戏特色

### 角色系统

- **萧言**：出生于蛇岛，擅长火系技能
  - 初始技能：火系突刺
  - 10级技能：水影步
  - 20级技能：蛇舞连击

- **莫**：出生于云山，擅长风系技能
  - 初始技能：风系突刺
  - 10级技能：云影步
  - 20级技能：风云连击

### 场景系统

- 5个不同的游戏场景
- 每个场景有不同的功能
- 可以随时切换场景
- 场景地图实时显示

### 聊天系统

- 支持两种聊天方式：`chat 消息` 或直接输入消息
- 消息带时间戳显示
- 发送者也能看到自己发送的消息

## 扩展功能

可以修改以下文件来扩展游戏功能：
- `npcs.json` - 添加更多NPC和问题
- `Scene.java` - 添加新场景
- `NetworkGameServer.java` - 修改场景初始化逻辑

## 更新日志

### 最新版本特性

1. ✅ 战斗结束后自动回血
2. ✅ PK系统选项验证（只接受11和12）
3. ✅ NPC对话改为选择题模式
4. ✅ 场景系统，地图显示
5. ✅ 5个不同的游戏场景
6. ✅ 场景切换功能
7. ✅ 改进的聊天系统（支持直接输入消息）
8. ✅ PK状态锁定（防止PK中的玩家被挑战）
9. ✅ 玩家列表显示状态（PK中/战斗中/可挑战）

---

*最后更新：2025-01-02*
