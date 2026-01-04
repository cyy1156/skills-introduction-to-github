package com.mudgame;
import com.javaclass.keyboardinput;
import java.util.Scanner;

public class Game {
    private Scanner scanner = new Scanner(System.in);
    private Figure player;
    private SceneManager sceneManager;

    public void start() {
        System.out.println("******** 欢迎进入 MUD 武侠世界 ********");
        System.out.println("1. 新游戏");
        System.out.println("2. 加载游戏");

        int choice = keyboardinput.getInt(1, 2);
        if (choice == 1) {
            System.out.println("选择角色：萧言");
            player = new XiaoYan();
        } else {
            player = FileHandler.loadGame();
            if (player == null) {
                System.out.println("开始新游戏...");
                player = new XiaoYan();
            }
        }

        sceneManager = new SceneManager(player, scanner);
        sceneManager.registerScene(new BattleScene());
        sceneManager.registerScene(new NpcScene());

        mainMenu();
    }

    private void mainMenu() {
        while (true) {
            System.out.println("\n===== 主菜单 =====");
            System.out.println("1. 打怪练级");
            System.out.println("2. NPC 过招");
            System.out.println("3. 查看状态");
            System.out.println("4. 保存游戏");
            System.out.println("5. 加载游戏");
            System.out.println("6. 退出游戏");

            int ch = keyboardinput.getInt(1, 6);

            switch (ch) {
                case 1: sceneManager.enterScene("battle"); break;
                case 2: sceneManager.enterScene("npc"); break;
                case 3: player.showStatus(); break;
                case 4: FileHandler.saveGame(player); break;
                case 5:
                    Figure loaded = FileHandler.loadGame();
                    if (loaded != null) player = loaded;
                    break;
                case 6:
                    System.out.println("退出游戏，江湖再会！");
                    return;
            }
        }
    }

    /* 工具方法：限定输入范围 */

        public static void main(String[] args) {
            Game game = new Game();
            game.start();
        }
    }


