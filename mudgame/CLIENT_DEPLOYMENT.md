# 客户端部署说明

## 问题：客户端需要哪些文件？

### ✅ 答案：客户端**不需要**Gson库！

## 详细分析

### 客户端代码分析

查看 `NetworkGameClient.java` 的代码，它只使用了：

```java
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
```

**关键发现：**
- ❌ 没有导入 `JsonUtil`
- ❌ 没有导入 `com.google.gson`
- ❌ 没有使用任何JSON相关类
- ✅ 只使用Java标准库

### 客户端功能

客户端只负责：
1. **连接服务器** - 使用 `Socket`
2. **接收消息** - 使用 `BufferedReader` 读取服务器消息
3. **发送命令** - 使用 `PrintWriter` 发送用户输入
4. **显示界面** - 使用 `System.out` 显示服务器返回的消息

**所有游戏逻辑和数据处理都在服务器端！**

---

## 📦 客户端部署文件清单

### 必需文件（最小部署）

```
客户端目录/
├── NetworkGameClient.class          # 编译后的客户端类文件
└── (可选) start_client.bat          # 启动脚本
```

### 不需要的文件

```
❌ lib/gson-2.10.1.jar               # 不需要Gson库
❌ JsonUtil.class                    # 不需要JSON工具类
❌ Figure.class                      # 不需要游戏逻辑类
❌ Monster.class                     # 不需要游戏逻辑类
❌ Npc.class                         # 不需要游戏逻辑类
❌ Skill.class                       # 不需要游戏逻辑类
❌ NetworkGameServer.class           # 不需要服务器类
❌ NetworkBattleSystem.class         # 不需要战斗系统类
```

---

## 🚀 客户端部署步骤

### 方法一：单独编译客户端（推荐）

#### 1. 编译客户端

```bash
# 只需要编译 NetworkGameClient.java
javac -encoding UTF-8 -d client_out src/com/mudgame/NetworkGameClient.java
```

**注意：** 不需要添加 Gson 库到 classpath！

#### 2. 运行客户端

```bash
# Windows
java -Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8 -cp client_out com.mudgame.NetworkGameClient

# Linux/Mac
java -Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8 -cp client_out com.mudgame.NetworkGameClient
```

#### 3. 连接指定服务器

```bash
java -Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8 -cp client_out com.mudgame.NetworkGameClient <服务器IP> 8888
```

### 方法二：从完整项目提取

如果已经有完整的编译输出，只需要：

```bash
# 复制客户端类文件
cp out/production/USST/com/mudgame/NetworkGameClient.class client_out/com/mudgame/

# 运行（不需要Gson库）
java -Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8 -cp client_out com.mudgame.NetworkGameClient
```

---

## 📋 客户端启动脚本（简化版）

### Windows CMD (client_start.bat)

```batch
@echo off
chcp 65001 >nul
java -Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8 -cp . com.mudgame.NetworkGameClient %1 %2
pause
```

### Windows PowerShell (client_start.ps1)

```powershell
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$env:JAVA_TOOL_OPTIONS = "-Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8"

if ($args.Count -eq 0) {
    java -Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8 -cp . com.mudgame.NetworkGameClient
} elseif ($args.Count -eq 1) {
    java -Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8 -cp . com.mudgame.NetworkGameClient $args[0]
} else {
    java -Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8 -cp . com.mudgame.NetworkGameClient $args[0] $args[1]
}

Read-Host "按Enter键退出"
```

---

## 🔍 验证客户端不需要Gson

### 测试编译（不包含Gson）

```bash
# 测试：只编译客户端，不添加Gson库
javac -encoding UTF-8 -d test_client src/com/mudgame/NetworkGameClient.java

# 如果编译成功，说明客户端不依赖Gson
```

### 测试运行（不包含Gson）

```bash
# 测试：运行客户端，不添加Gson库到classpath
java -cp test_client com.mudgame.NetworkGameClient localhost 8888

# 如果运行成功，说明客户端运行时不需要Gson
```

---

## 📊 服务器端 vs 客户端依赖对比

| 依赖项 | 服务器端 | 客户端 |
|--------|---------|--------|
| **Gson库** | ✅ 需要 | ❌ **不需要** |
| **JsonUtil** | ✅ 需要 | ❌ **不需要** |
| **游戏逻辑类** | ✅ 需要 | ❌ **不需要** |
| **Java标准库** | ✅ 需要 | ✅ 需要 |
| **UTF-8编码支持** | ✅ 需要 | ✅ 需要 |

---

## 💡 为什么客户端不需要Gson？

### 架构设计

```
┌─────────────┐                    ┌─────────────┐
│   客户端     │                    │   服务器端   │
│             │                    │             │
│ 只发送命令   │                    │ 处理所有逻辑 │
│ 只显示消息   │                    │ 使用JsonUtil│
│             │                    │ 使用Gson库  │
│ 不需要知道   │                    │ 保存/加载   │
│ JSON的存在   │                    │ 角色数据    │
└─────────────┘                    └─────────────┘
```

### 数据流向

1. **客户端 → 服务器**：
   - 发送：`"1"` (打怪命令)
   - 发送：`"TestPlayer"` (角色名)
   - **纯文本字符串，不是JSON**

2. **服务器 → 客户端**：
   - 返回：`"欢迎来到MUD武侠世界！"`
   - 返回：`"你的血量: 100/100"`
   - **纯文本字符串，不是JSON**

3. **JSON处理（仅在服务器端）**：
   - 服务器保存角色数据时：`JsonUtil.figureToJson(player)` → 保存到文件
   - 服务器加载角色数据时：`JsonUtil.figureFromJson(json)` → 从文件读取
   - **客户端完全不知道这个过程**

---

## ✅ 总结

### 客户端部署清单

**最小部署（必需）：**
- ✅ `NetworkGameClient.class` - 客户端类文件
- ✅ Java运行时环境（JRE 8+）

**可选（推荐）：**
- ✅ 启动脚本（`.bat` 或 `.ps1`）
- ✅ UTF-8编码设置

**不需要：**
- ❌ Gson库（`gson-2.10.1.jar`）
- ❌ 任何游戏逻辑类
- ❌ 任何服务器端类
- ❌ JSON处理相关类

### 部署大小对比

- **完整服务器部署**：~500KB+（包含所有类 + Gson库）
- **最小客户端部署**：~5KB（只有NetworkGameClient.class）

**客户端部署大小减少99%！** 🎉

---

*最后更新：2025-01-02*

