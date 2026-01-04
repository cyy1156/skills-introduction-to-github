,MUD 游戏网络版设计文档
前言
本文档详细描述了 MUD 游戏网络版的整体架构，明确了各个模块的职责及核心类的功能说明。
一、 核心类结构
1. 服务端核心类
   服务端主类
   NetworkGameServer - 服务端主程序
   初始化服务端 Socket 连接
   监听并处理最多 4 个客户端连接
   管理游戏内 NPC 和怪物信息
   维护客户端连接状态及游戏全局数据
   辅助类
   ClientHandler - 客户端处理器（内嵌在 NetworkGameServer.java 中）
   单独处理每个客户端的网络通信
   接收客户端发送的指令
   解析指令并调用对应业务逻辑
   向客户端返回处理结果
   游戏实体类
   Figure - 游戏角色基类
   存储角色通用属性（血量、攻击力、等级、位置等）
   提供角色属性操作的通用方法
   定义角色行为的抽象接口
   XiaoYan - 玩家角色类（继承 Figure 类）
   实现玩家专属的属性和行为
   维护玩家的背包和技能信息
   Monster - 怪物类
   存储怪物专属属性（掉落物品、仇恨值等）
   实现怪物的攻击、移动、死亡等行为逻辑
   Npc - NPC 类
   存储 NPC 的对话内容和功能类型
   实现 NPC 交互逻辑
   提供任务接取和提交功能
   Skill - 技能类
   存储技能名称、伤害值、冷却时间
   提供技能释放逻辑
   定义技能的作用目标类型
   工具与业务类
   JsonUtil - JSON 工具类
   角色对象序列化方法figureToJson
   JSON 字符串反序列化为角色对象方法figureFromJson
   从文件加载 NPC 列表方法loadNpcsFromFile
   NetworkBattleSystem - 网络战斗系统
   PVE 战斗（玩家 vs 怪物）逻辑实现
   PVP 战斗（玩家 vs 玩家）逻辑实现
   战斗结算和奖励发放
   Java 核心 API 依赖
   java.net.ServerSocket - 服务端监听 Socket
   java.net.Socket - 客户端连接 Socket
   java.io.BufferedReader - 字符缓冲输入流（读取数据）
   java.io.PrintWriter - 字符打印输出流（发送数据）
   java.util.Map - 存储客户端连接与角色的映射关系
   java.util.List - 存储 NPC 列表等集合数据
   java.util.concurrent.ConcurrentHashMap - 线程安全的 Map，用于多客户端并发场景
2. 客户端核心类
   客户端主类
   NetworkGameClient - 客户端主程序
   建立与服务端的 Socket 连接
   接收用户输入的游戏指令
   向服务端发送指令并接收响应结果
   展示游戏界面和反馈信息
   Java 核心 API 依赖
   java.net.Socket - 建立与服务端的 TCP 连接
   java.io.BufferedReader - 读取服务端响应数据和用户输入
   java.io.PrintWriter - 向服务端发送用户指令
   java.nio.charset.StandardCharsets - 提供 UTF-8 字符编码支持，保证数据传输编码统一
   说明：客户端无需持有游戏实体类（如 Figure、Monster、Npc）的实例，仅需接收服务端传输的 JSON 格式数据并展示。
   二、 核心业务流程
   游戏核心运行流程
   整个游戏的核心是服务端统一管理所有游戏实体，流程如下：
   服务端初始化 - 启动与加载
   初始化 ServerSocket 并监听指定端口
   加载 NPC 配置文件、怪物数据、地图数据并存储在内存中
   客户端连接阶段
   客户端：启动程序并发起与服务端的 TCP 连接
   服务端：接收到连接请求后，创建 ClientHandler 线程单独处理该客户端
   游戏交互阶段
   服务端：接收客户端指令，操作 Figure、Monster、Npc 等游戏实体，处理业务逻辑
   客户端：发送用户指令，接收服务端返回的 JSON 数据并展示
   三、 服务端与客户端类对比
   服务端类结构
   游戏实体类
   Figure - 角色基类
   XiaoYan - 玩家角色类
   Monster - 怪物类
   Npc - NPC 类
   Skill - 技能类
   业务与工具类
   NetworkGameServer - 服务端主类
   ClientHandler - 客户端连接处理器
   JsonUtil - JSON 工具类
   NetworkBattleSystem - 网络战斗系统
   依赖的核心 Java API
   ServerSocket - 服务端监听 Socket 连接
   客户端类结构
   客户端主类
   NetworkGameClient - 客户端主类
   依赖的核心 Java API
   StandardCharsets - 提供 UTF-8 字符编码，保证与服务端数据传输编码一致
   四、 类职责对照表
   模块	服务端对应类	客户端对应类	核心职责
   主程序	NetworkGameServer	NetworkGameClient	启动程序，统筹整体流程
   网络通信	ServerSocket, Socket	Socket	建立 TCP 连接，实现数据传输
   游戏实体	Figure, XiaoYan, Monster, Npc, Skill	无	存储游戏数据，实现实体行为
   业务工具	JsonUtil, NetworkBattleSystem	无	处理 JSON 转换、战斗等业务逻辑
   连接管理	ClientHandler	无	单独处理每个客户端的通信
   IO 操作	BufferedReader, PrintWriter	BufferedReader, PrintWriter	读取和发送字符数据
   编码支持	手动指定 UTF-8 编码	StandardCharsets	保证数据传输的 UTF-8 编码统一
   五、 架构示意图
   客户端 - 服务端架构示意图
   plaintext
   ┌──────────────────────────────────┐                    ┌──────────────────────────────────┐
   │              客户端               │                    │              服务端               │
   │  ┌─────────────┐                  │                    │  ┌─────────────┐                  │
   │  │  玩家交互层  │                  │                    │  │  业务逻辑层  │                  │
   │  └─────────────┘                  │                    │  └─────────────┘                  │
   │  │ NetworkGame │                  │                    │  │ NetworkGame │                  │
   │  │   Client    │ ──── TCP连接 ────┼────────────────────┼──│   Server    │                  │
   │  └─────────────┘                  │                    │  └─────────────┘                  │
   │  │  数据传输层  │                  │                    │  │  数据管理层  │                  │
   │  └─────────────┘                  │                    │  └─────────────┘                  │
   │  - 发送用户指令                   │                    │  - 管理客户端连接                 │
   │  - 接收服务端响应                 │                    │  - 维护游戏实体数据               │
   │  - 展示游戏界面                   │                    │  - 处理战斗逻辑                   │
   │                                  │                    │  - 存储NPC/怪物数据              │
   │                                  │                    │  - 处理JSON序列化/反序列化        │
   │                                  │                    │  - 游戏状态维护                   │
   └──────────────────────────────────┘                    └──────────────────────────────────┘
   后续优化方向
   界面优化：实现图形化界面（Swing/JavaFX）替代控制台 UI
   数据持久化：将玩家数据、游戏配置存储到数据库（MySQL）
   协议优化：自定义通信协议，替代简单的字符串指令
   性能优化：优化多客户端并发处理，提升服务器承载量
   功能扩展：增加组队、交易、副本等更多游戏玩法
   六、 总结
   核心技术点
   基于 Java Socket 实现 TCP 网络通信
   基于 Java IO 流实现字符数据的读取与发送
   基于 UTF-8 编码保证跨端数据传输的一致性
   核心设计思想
   服务端中心化：所有游戏实体和业务逻辑均由服务端统一管理，保证数据安全
   客户端轻量化：客户端仅负责交互展示，不处理核心业务，降低客户端复杂度
   待实现功能
   图形化用户界面
   游戏数据持久化存储
   更多游戏玩法扩展
   服务器性能优化与容错处理
   七、 类依赖关系示意图
   服务端类依赖关系
   plaintext
   NetworkGameServer
   ├── 依赖 ClientHandler (多实例)
   ├───── 依赖 Figure
   ├─────  └── 依赖 XiaoYan
   ├───── 依赖 Monster
   ├───── 依赖 Npc
   ├───── 依赖 Skill
   ├───── 依赖 JsonUtil
   ├───── 依赖 NetworkBattleSystem
   │       ├── 依赖 Figure
   │       └── 依赖 Monster
   └── 依赖 JsonUtil
   └── 依赖 (第三方Gson库)
   客户端类依赖关系
   plaintext
   NetworkGameClient
   └── 依赖 (Java核心API)
   文档更新时间：2025-01-02
