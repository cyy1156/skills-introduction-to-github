package com.mudgame;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * MUD游戏网络服务器
 * 支持最多5个客户端同时连接
 */
public class NetworkGameServer {
    private static final int PORT = 4444;
    private static final int MAX_CLIENTS = 5;
    
    private ServerSocket serverSocket;
    private Map<String, ClientHandler> clients = new ConcurrentHashMap<>();
    private List<Npc> npcs = new ArrayList<>();
    private List<Scene> scenes = new ArrayList<>();
    private Random random = new Random();
    
    public static void main(String[] args) {
        // 设置控制台输出编码为UTF-8
        try {
            System.setProperty("file.encoding", "UTF-8");
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
            System.setErr(new PrintStream(System.err, true, "UTF-8"));
        } catch (Exception e) {
            System.err.println("Warning: Failed to set UTF-8 encoding: " + e.getMessage());
        }
        NetworkGameServer server = new NetworkGameServer();
        server.start();
    }
    
    public void start() {
        try {
            // 加载NPC数据
            loadNpcs();
            // 初始化场景
            initializeScenes();
            
            serverSocket = new ServerSocket(PORT);/*代码里 new ServerSocket(PORT) 这个写法，默认会让服务器监听电脑上所有可用的网络接口，包括：
            内网 IP 对应的接口（比如 192.168.1.100）
            本地回环地址 127.0.0.1（仅本机访问用）*/
            System.out.println("====== MUD游戏服务器启动 ======");
            System.out.println("服务器监听端口: " + PORT);
            System.out.println("最大支持客户端数: " + MAX_CLIENTS);
            System.out.println("等待客户端连接...");
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                
                if (clients.size() >= MAX_CLIENTS) {
                    PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"), true);
                    out.println("服务器已满，最多支持" + MAX_CLIENTS + "个玩家！");
                    clientSocket.close();
                    continue;
                }
                
                System.out.println("[客户端连接] 新客户端尝试连接，IP: " + clientSocket.getRemoteSocketAddress());
                ClientHandler handler = new ClientHandler(clientSocket, this);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.out.println("服务器错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadNpcs() {
        try {
            String npcFile = "src/com/mudgame/npcs.json";
            npcs = JsonUtil.loadNpcsFromFile(npcFile);
            System.out.println("已加载 " + npcs.size() + " 个NPC");
        } catch (Exception e) {
            System.out.println("加载NPC失败，使用默认NPC: " + e.getMessage());
            npcs.add(new Npc("书生", "武林中最强的是什么？", "心", 50));
        }
    }
    
    private void initializeScenes() {
        Scene scene1 = new Scene("新手村", "宁静的村庄，适合新手修炼");
        scene1.addAction("1. 打怪练级");
        scene1.addAction("2. NPC对话");
        scenes.add(scene1);
        
        Scene scene2 = new Scene("武林广场", "江湖人士聚集地，可以切磋武艺");
        scene2.addAction("1. 打怪练级");
        scene2.addAction("2. NPC对话");
        scene2.addAction("8. PK挑战");
        scenes.add(scene2);
        
        Scene scene3 = new Scene("藏经阁", "存放武学秘籍的地方");
        scene3.addAction("2. NPC对话");
        scene3.addAction("3. 查看状态");
        scenes.add(scene3);
        
        Scene scene4 = new Scene("练功房", "专门用于修炼的场所");
        scene4.addAction("1. 打怪练级");
        scene4.addAction("3. 查看状态");
        scenes.add(scene4);
        
        Scene scene5 = new Scene("江湖客栈", "休息和交流的地方");
        scene5.addAction("2. NPC对话");
        scene5.addAction("6. 查看在线玩家");
        scene5.addAction("7. 聊天");
        scenes.add(scene5);
        
        System.out.println("已初始化 " + scenes.size() + " 个场景");
    }
    
    public Scene getRandomScene() {
        if (scenes.isEmpty()) {
            return null;
        }
        return scenes.get(random.nextInt(scenes.size()));
    }
    
    public List<Scene> getScenes() {
        return scenes;
    }
    
    public synchronized void registerClient(String playerName, ClientHandler handler) {
        clients.put(playerName, handler);
        broadcastMessage("系统", playerName + " 加入了游戏！", playerName);
        sendPlayerList();
    }
    
    public synchronized void removeClient(String playerName) {
        clients.remove(playerName);
        broadcastMessage("系统", playerName + " 离开了游戏。", null);
        sendPlayerList();
    }
    
    public Map<String, ClientHandler> getClients() {
        return clients;
    }
    
    public List<Npc> getNpcs() {
        return npcs;
    }
    
    public void broadcastMessage(String from, String message, String exclude) {
        String timestamp = java.time.LocalTime.now().toString().substring(0, 8); // HH:mm:ss格式
        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
            if (!entry.getKey().equals(exclude)) {
                entry.getValue().sendMessage("[" + timestamp + "] <" + from + "> " + message);
            } else {
                // 发送者自己也能看到消息（带标记）
                entry.getValue().sendMessage("[" + timestamp + "] <你> " + message);
            }
        }
    }
    
    private void sendPlayerList() {
        StringBuilder list = new StringBuilder("在线玩家: ");
        for (String name : clients.keySet()) {
            list.append(name).append(" ");
        }
        for (ClientHandler handler : clients.values()) {
            handler.sendMessage(list.toString());
        }
    }
    
    public ClientHandler getClientHandler(String playerName) {
        return clients.get(playerName);
    }
    
    public Monster generateMonster() {
        return new Monster();
    }
    
    public Npc getRandomNpc() {
        if (npcs.isEmpty()) {
            return new Npc("书生", "武林中最强的是什么？", "心", 50);
        }
        return npcs.get(random.nextInt(npcs.size()));
    }
}

/**
 * 客户端处理器
 */
class ClientHandler implements Runnable {
    private Socket socket;
    private NetworkGameServer server;
    private PrintWriter out;
    private BufferedReader in;
    private Figure player;
    private String playerName;
    private boolean running = true;
    private Scene currentScene;  // 当前所在场景
    
    public ClientHandler(Socket socket, NetworkGameServer server) {
        this.socket = socket;
        this.server = server;
    }
    
    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            
            // 登录/注册
            out.println("欢迎来到MUD武侠世界！");
            out.println("请选择角色类型:");
            out.println("1. 萧言 - 出生于蛇岛，擅长火系技能");
            out.println("2. 莫 - 出生于云山，擅长风系技能");
            out.println("请选择(1-2):");
            
            String roleChoice = in.readLine();
            System.out.println("[客户端连接] 收到角色选择: " + (roleChoice != null ? roleChoice : "空"));
            int roleType = 1; // 默认选择萧言
            if (roleChoice != null && !roleChoice.trim().isEmpty()) {
                try {
                    roleType = Integer.parseInt(roleChoice.trim());
                    if (roleType < 1 || roleType > 2) {
                        roleType = 1; // 无效选择，默认萧言
                    }
                } catch (NumberFormatException e) {
                    roleType = 1; // 无效输入，默认萧言
                }
            }
            
            out.println("请输入角色名称:");
            playerName = in.readLine();
            System.out.println("[客户端连接] 收到角色名称: " + (playerName != null ? playerName : "空"));
            
            if (playerName == null || playerName.trim().isEmpty()) {
                playerName = "玩家" + System.currentTimeMillis();
            }
            playerName = playerName.trim();
            
            // 检查是否已有同名玩家
            if (server.getClients().containsKey(playerName)) {
                playerName = playerName + "_" + System.currentTimeMillis();
            }
            
            // 根据选择创建角色
            if (roleType == 1) {
                player = new XiaoYan();
                out.println("你选择了角色：萧言");
                System.out.println("[客户端连接] " + playerName + " 选择了角色：萧言");
            } else {
                player = new Mo();
                out.println("你选择了角色：莫");
                System.out.println("[客户端连接] " + playerName + " 选择了角色：莫");
            }
            player.name = playerName;
            
            server.registerClient(playerName, this);
            // 随机分配初始场景
            currentScene = server.getRandomScene();
            out.println("角色创建成功: " + playerName);
            System.out.println("[客户端连接] " + playerName + " 角色创建成功，已加入游戏，初始场景: " + (currentScene != null ? currentScene.getName() : "无"));
            sendStatus();
            showMainMenu();
            
            // 处理客户端消息
            String input;
            while (running && (input = in.readLine()) != null) {
                // 如果正在PK或战斗中，命令由战斗系统处理，这里不处理
                if (isInPvP || isInBattle) {
                    // 战斗系统的命令由NetworkBattleSystem直接读取，这里只是占位
                    continue;
                }
                handleCommand(input);
            }
            
        } catch (IOException e) {
            System.out.println("[客户端断开] " + (playerName != null ? playerName : "未知玩家") + " 断开连接: " + e.getMessage());
        } finally {
            if (playerName != null) {
                System.out.println("[客户端断开] " + playerName + " 已从服务器移除");
                server.removeClient(playerName);
            }
            closeConnection();
        }
    }
    
    private void handleCommand(String command) {
        try {
            if (command == null || command.trim().isEmpty()) {
                return;
            }
            
            command = command.trim();
            
            // 服务器端显示客户端操作日志
            System.out.println("[客户端操作] " + playerName + " 执行命令: " + command);
            
            // 如果正在PK中，将命令传递给PK系统处理
            if (isInPvP) {
                // PK中的命令由NetworkBattleSystem处理，这里只记录
                return;
            }
            
            // 如果正在打怪中，检查是否是战斗命令
            if (isInBattle) {
                // 战斗中的命令由NetworkBattleSystem处理，这里只记录
                return;
            }
            
            String[] parts = command.split(" ", 2);
            String cmd = parts[0].toLowerCase();
            String param = parts.length > 1 ? parts[1] : "";
            
            switch (cmd) {
                case "1":
                case "battle":
                case "fight":
                    System.out.println("[客户端操作] " + playerName + " 开始打怪");
                    startBattle();
                    break;
                case "2":
                case "npc":
                case "talk":
                    System.out.println("[客户端操作] " + playerName + " 与NPC对话");
                    talkToNpc();
                    break;
                case "3":
                case "status":
                case "info":
                    System.out.println("[客户端操作] " + playerName + " 查看状态");
                    sendStatus();
                    showMainMenu();
                    break;
                case "4":
                case "save":
                    System.out.println("[客户端操作] " + playerName + " 保存游戏");
                    saveGame();
                    showMainMenu();
                    break;
                case "5":
                case "load":
                    System.out.println("[客户端操作] " + playerName + " 加载游戏");
                    loadGame();
                    showMainMenu();
                    break;
                case "6":
                case "list":
                case "players":
                    System.out.println("[客户端操作] " + playerName + " 查看在线玩家列表");
                    listPlayers();
                    showMainMenu();
                    break;
                case "7":
                case "chat":
                    if (param.isEmpty()) {
                        out.println("用法: chat <消息>");
                        out.println("或者直接输入消息内容（不需要chat前缀）");
                        showMainMenu();
                    } else {
                        System.out.println("[客户端操作] " + playerName + " 发送聊天消息: " + param);
                        server.broadcastMessage(playerName, param, playerName);
                        out.println("消息已发送！");
                        showMainMenu();
                    }
                    break;
                case "8":
                case "pk":
                case "challenge":
                    if (param.isEmpty()) {
                        out.println("用法: pk <玩家名>");
                        showMainMenu();
                    } else {
                        // 检查自己是否在PK中
                        if (isInPvP) {
                            out.println("你正在PK中，无法发起新的挑战！");
                            return;
                        }
                        System.out.println("[客户端操作] " + playerName + " 向 " + param + " 发起PK挑战");
                        challengePlayer(param);
                    }
                    break;
                case "accept":
                    if (pendingChallenge != null) {
                        System.out.println("[客户端操作] " + playerName + " 接受了 " + pendingChallenge.playerName + " 的PK挑战");
                        acceptChallenge();
                    } else {
                        out.println("当前没有待处理的挑战");
                        showMainMenu();
                    }
                    break;
                case "reject":
                    if (pendingChallenge != null) {
                        System.out.println("[客户端操作] " + playerName + " 拒绝了 " + pendingChallenge.playerName + " 的PK挑战");
                        out.println("你拒绝了 " + pendingChallenge.playerName + " 的挑战。");
                        pendingChallenge.out.println(playerName + " 拒绝了你的挑战。");
                        pendingChallenge = null;
                        showMainMenu();
                    } else {
                        out.println("当前没有待处理的挑战");
                        showMainMenu();
                    }
                    break;
                case "9":
                case "scene":
                case "change":
                    System.out.println("[客户端操作] " + playerName + " 切换场景");
                    changeScene();
                    showMainMenu();
                    break;
                case "0":
                case "quit":
                case "exit":
                    System.out.println("[客户端操作] " + playerName + " 退出游戏");
                    out.println("再见！");
                    running = false;
                    break;
                default:
                    // 如果不是已知命令，检查是否是聊天消息（不以数字开头的消息）
                    if (!cmd.matches("^\\d+$")) {
                        // 可能是聊天消息，直接发送
                        System.out.println("[客户端操作] " + playerName + " 发送聊天消息: " + command);
                        server.broadcastMessage(playerName, command, playerName);
                        out.println("消息已发送！");
                        showMainMenu();
                    } else {
                        System.out.println("[客户端操作] " + playerName + " 执行了未知命令: " + command);
                        out.println("未知命令: " + command);
                        showMainMenu();
                    }
            }
        } catch (Exception e) {
            out.println("处理命令错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void showMainMenu() {
        // 如果正在PK或战斗中，不显示菜单
        if (isInPvP || isInBattle) {
            return;
        }
        
        // 显示当前场景地图
        if (currentScene != null) {
            out.println("\n" + currentScene.getMapDisplay());
            out.println("当前位置: " + currentScene.getName());
            out.println("场景描述: " + currentScene.getDescription());
        } else {
            // 如果没有场景，随机分配一个
            currentScene = server.getRandomScene();
            if (currentScene != null) {
                out.println("\n" + currentScene.getMapDisplay());
                out.println("当前位置: " + currentScene.getName());
                out.println("场景描述: " + currentScene.getDescription());
            }
        }
        
        out.println("\n===== 主菜单 =====");
        out.println("1. 打怪练级");
        out.println("2. NPC对话");
        out.println("3. 查看状态");
        out.println("4. 保存游戏");
        out.println("5. 加载游戏");
        out.println("6. 查看在线玩家");
        out.println("7. 聊天 (chat <消息> 或直接输入消息)");
        out.println("8. PK挑战 (pk <玩家名>)");
        out.println("9. 切换场景");
        out.println("0. 退出游戏");
        out.println("请选择(0-9):");
    }
    
    private void startBattle() {
        try {
            // 检查是否在PK中
            if (isInPvP) {
                out.println("你正在PK中，无法开始打怪！");
                return;
            }
            
            isInBattle = true;
            Monster monster = server.generateMonster();
            System.out.println("[战斗系统] " + playerName + " 遇到怪物: " + monster.getName() + " (等级: " + monster.getGrade() + ", 血量: " + monster.getLifeValue() + ")");
            out.println("\n你遇到了怪物：" + monster.getName());
            out.println("等级: " + monster.getGrade() + ", 血量: " + monster.getLifeValue());
            out.println("战斗开始！");
            
            NetworkBattleSystem.fight(player, monster, this, server);
            
            if (player.isAlive()) {
                System.out.println("[战斗系统] " + playerName + " 战斗胜利，当前血量: " + player.getLifeValue() + "/" + player.getMaxLifeValue());
            } else {
                System.out.println("[战斗系统] " + playerName + " 战斗失败，角色阵亡");
            }
            
            isInBattle = false;
            showMainMenu();
        } catch (Exception e) {
            isInBattle = false;
            System.out.println("[战斗系统] " + playerName + " 战斗发生错误: " + e.getMessage());
            out.println("战斗错误: " + e.getMessage());
            e.printStackTrace();
            showMainMenu();
        }
    }
    
    private void talkToNpc() {
        try {
            Npc npc = server.getRandomNpc();
            System.out.println("[NPC对话] " + playerName + " 遇到NPC: " + npc.getName() + ", 问题: " + npc.getQuestion());
            out.println("\n你遇到 NPC：" + npc.getName());
            if (npc.getDialogue() != null && !npc.getDialogue().isEmpty()) {
                out.println(npc.getDialogue());
            }
            out.println("\n" + npc.getQuestion());
            
            // 如果是选择题，显示选项
            if (npc.hasOptions()) {
                out.println("\n请选择答案:");
                List<String> options = npc.getOptions();
                for (int i = 0; i < options.size(); i++) {
                    out.println((i + 1) + ". " + options.get(i));
                }
                out.println("请输入选项编号(1-" + options.size() + "):");
            } else {
                out.println("请输入答案:");
            }
            
            String answer = in.readLine();
            System.out.println("[NPC对话] " + playerName + " 回答: " + (answer != null ? answer : "空"));
            if (npc.checkAnswer(answer)) {
                System.out.println("[NPC对话] " + playerName + " 回答正确，获得经验: " + npc.getExp());
                out.println("\n回答正确！获得经验：" + npc.getExp());
                player.gainExp(npc.getExp());
                sendStatus();
            } else {
                System.out.println("[NPC对话] " + playerName + " 回答错误，正确答案: " + npc.getAnswer());
                if (npc.hasOptions()) {
                    out.println("\n回答错误，没有奖励。");
                } else {
                    out.println("\n回答错误，没有奖励。正确答案是: " + npc.getAnswer());
                }
            }
            
            showMainMenu();
        } catch (IOException e) {
            System.out.println("[NPC对话] " + playerName + " NPC对话发生错误: " + e.getMessage());
            out.println("NPC对话错误: " + e.getMessage());
            showMainMenu();
        }
    }
    
    public void sendStatus() {
        out.println("\n===== 角色状态 =====");
        out.println("名字：" + player.getName());
        out.println("等级：" + player.getGrade());
        out.println("血量：" + player.getLifeValue() + "/" + player.getMaxLifeValue());
        out.println("攻击：" + player.getAttack());
        out.println("防御：" + player.getDefend());
        out.println("经验：" + player.getExp());
        out.println("技能：");
        for (Skill s : player.getSkills()) {
            out.println("- " + s.getName());
        }
    }
    
    private void saveGame() {
        try {
            // 确定角色类型
            String roleType = (player instanceof Mo) ? "Mo" : "XiaoYan";
            String json = JsonUtil.figureToJson(player, roleType);
            String fileName = "src/com/mudgame/save_" + playerName + ".json";
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
                writer.print(json);
            }
            System.out.println("[存档系统] " + playerName + " 保存游戏成功，角色类型: " + roleType);
            out.println("游戏已保存！");
        } catch (IOException e) {
            System.out.println("[存档系统] " + playerName + " 保存游戏失败: " + e.getMessage());
            out.println("保存失败：" + e.getMessage());
        }
    }
    
    private void loadGame() {
        try {
            String fileName = "src/com/mudgame/save_" + playerName + ".json";
            File file = new File(fileName);
            if (!file.exists()) {
                System.out.println("[存档系统] " + playerName + " 尝试加载存档，但文件不存在: " + fileName);
                out.println("没有找到存档文件！");
                return;
            }
            
            System.out.println("[存档系统] " + playerName + " 开始加载存档: " + fileName);
            StringBuilder json = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
            }
            
            Figure loaded = JsonUtil.figureFromJson(json.toString());
            if (loaded != null) {
                String loadedRoleType = (loaded instanceof Mo) ? "Mo" : "XiaoYan";
                System.out.println("[存档系统] " + playerName + " 加载存档成功，角色类型: " + loadedRoleType + ", 等级: " + loaded.getGrade());
                player = loaded;
                player.name = playerName; // 保持当前玩家名
                out.println("存档加载成功！");
                sendStatus();
            } else {
                System.out.println("[存档系统] " + playerName + " 加载存档失败：数据格式错误");
                out.println("加载失败：数据格式错误");
            }
        } catch (IOException e) {
            System.out.println("[存档系统] " + playerName + " 加载存档失败: " + e.getMessage());
            out.println("加载失败：" + e.getMessage());
        }
    }
    
    private void listPlayers() {
        System.out.println("[玩家列表] " + playerName + " 查看在线玩家列表");
        out.println("\n===== 在线玩家 =====");
        Map<String, ClientHandler> clients = server.getClients();
        int index = 1;
        for (String name : clients.keySet()) {
            ClientHandler handler = clients.get(name);
            if (handler != this) {
                String status = "";
                if (handler.isInPvP()) {
                    status = " [PK中]";
                } else if (handler.isInBattle()) {
                    status = " [战斗中]";
                } else {
                    status = " (可挑战)";
                }
                out.println(index + ". " + name + status);
                index++;
            }
        }
        if (index == 1) {
            out.println("当前只有你一人在线。");
        }
    }
    
    private void challengePlayer(String targetName) {
        ClientHandler target = server.getClientHandler(targetName);
        if (target == null || target == this) {
            System.out.println("[PK系统] " + playerName + " 向 " + targetName + " 发起挑战失败：玩家不在线或无效");
            out.println("玩家 " + targetName + " 不在线或无效！");
            showMainMenu();
            return;
        }
        
        // 检查目标玩家是否在PK中
        if (target.isInPvP()) {
            System.out.println("[PK系统] " + playerName + " 向 " + targetName + " 发起挑战失败：目标玩家正在PK中");
            out.println("玩家 " + targetName + " 正在PK中，无法接受挑战！");
            showMainMenu();
            return;
        }
        
        // 检查目标玩家是否正在打怪
        if (target.isInBattle()) {
            System.out.println("[PK系统] " + playerName + " 向 " + targetName + " 发起挑战失败：目标玩家正在战斗中");
            out.println("玩家 " + targetName + " 正在战斗中，无法接受挑战！");
            showMainMenu();
            return;
        }
        
        System.out.println("[PK系统] " + playerName + " 向 " + targetName + " 发起PK挑战");
        out.println("向 " + targetName + " 发起挑战...");
        target.sendMessage(playerName + " 向你发起PK挑战！(输入 accept 接受，或 reject 拒绝)");
        target.setPendingChallenge(this);
        showMainMenu();
    }
    
    private ClientHandler pendingChallenge = null;
    private boolean isInPvP = false;  // 是否正在PK中
    private boolean isInBattle = false;  // 是否正在打怪中
    
    public void setPendingChallenge(ClientHandler challenger) {
        this.pendingChallenge = challenger;
    }
    
    public ClientHandler getPendingChallenge() {
        return pendingChallenge;
    }
    
    public boolean isInPvP() {
        return isInPvP;
    }
    
    public void setInPvP(boolean inPvP) {
        this.isInPvP = inPvP;
    }
    
    public boolean isInBattle() {
        return isInBattle;
    }
    
    public void setInBattle(boolean inBattle) {
        this.isInBattle = inBattle;
    }
    
    public void acceptChallenge() {
        if (pendingChallenge != null) {
            // 检查自己是否在PK中或战斗中
            if (isInPvP || isInBattle) {
                out.println("你正在战斗中，无法接受挑战！");
                pendingChallenge.out.println(playerName + " 正在战斗中，无法接受挑战。");
                pendingChallenge = null;
                showMainMenu();
                return;
            }
            
            // 检查挑战者是否在PK中或战斗中
            if (pendingChallenge.isInPvP() || pendingChallenge.isInBattle()) {
                out.println("挑战者正在战斗中，挑战已取消。");
                pendingChallenge = null;
                showMainMenu();
                return;
            }
            
            // 设置PK状态
            this.isInPvP = true;
            pendingChallenge.isInPvP = true;
            
            System.out.println("[PK系统] " + playerName + " 接受了 " + pendingChallenge.playerName + " 的PK挑战，开始PVP战斗");
            out.println("你接受了 " + pendingChallenge.playerName + " 的挑战！");
            pendingChallenge.out.println(playerName + " 接受了你的挑战！");
            NetworkBattleSystem.playerVsPlayer(pendingChallenge.player, this.player, 
                                              pendingChallenge, this, server);
            System.out.println("[PK系统] " + playerName + " 与 " + pendingChallenge.playerName + " 的PVP战斗结束");
            
            // 清除PK状态
            this.isInPvP = false;
            pendingChallenge.isInPvP = false;
            pendingChallenge = null;
            this.pendingChallenge = null;
        }
    }
    
    public void sendMessage(String message) {
        out.println(message);
    }
    
    public BufferedReader getInput() {
        return in;
    }
    
    public PrintWriter getOutput() {
        return out;
    }
    
    public Figure getPlayer() {
        return player;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    private void changeScene() {
        List<Scene> allScenes = server.getScenes();
        if (allScenes.isEmpty()) {
            out.println("没有可用场景！");
            return;
        }
        
        out.println("\n===== 场景列表 =====");
        for (int i = 0; i < allScenes.size(); i++) {
            Scene scene = allScenes.get(i);
            String marker = (currentScene != null && scene.getName().equals(currentScene.getName())) ? " [当前]" : "";
            out.println((i + 1) + ". " + scene.getName() + " - " + scene.getDescription() + marker);
        }
        out.println("请选择要前往的场景(1-" + allScenes.size() + "):");
        
        try {
            String choiceStr = in.readLine();
            int choice = Integer.parseInt(choiceStr);
            if (choice >= 1 && choice <= allScenes.size()) {
                Scene newScene = allScenes.get(choice - 1);
                if (currentScene != null && newScene.getName().equals(currentScene.getName())) {
                    out.println("你已经在 " + newScene.getName() + " 了！");
                } else {
                    currentScene = newScene;
                    System.out.println("[场景切换] " + playerName + " 切换到场景: " + currentScene.getName());
                    out.println("你已到达 " + currentScene.getName() + "！");
                    out.println(currentScene.getDescription());
                }
            } else {
                out.println("无效选择！");
            }
        } catch (Exception e) {
            out.println("切换场景失败: " + e.getMessage());
        }
    }
    
    private void closeConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

