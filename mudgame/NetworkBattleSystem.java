package com.mudgame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 网络版战斗系统
 */
public class NetworkBattleSystem {

    // 自定义异常类：输入非11/12时抛出
    public static class MissCannarinException extends Exception {
        public MissCannarinException() {
            super("请重新输入（仅支持11-12）");
        }
    }

    public static void fight(Figure player, Monster monster, ClientHandler handler, NetworkGameServer server) {
        PrintWriter out = handler.getOutput();
        BufferedReader in = handler.getInput();
        String playerName = handler.getPlayerName();

        int playercount = 0;
        int monstercount = 0;
        int playerolddefence = player.getDefend();
        int monsterolddefence = monster.getDefend();

        System.out.println("[战斗系统] " + playerName + " 开始与 " + monster.getName() + " 战斗");

        while (player.isAlive() && monster.isAlive()) {
            out.printf("\n你 HP:%d | %s HP:%d\n", player.getLifeValue(), monster.getName(), monster.getLifeValue());
            out.println("11. 普通攻击");
            out.println("12. 使用技能");
            out.println("请选择(11-12):");

            try {
                String choiceStr = in.readLine();
                System.out.println("[战斗系统] " + playerName + " 战斗选择: " + (choiceStr != null ? choiceStr : "空"));
                int choice = Integer.parseInt(choiceStr);

                // 校验输入是否为11或12，否则抛出自定义异常
                if (choice != 11 && choice != 12) {
                    throw new MissCannarinException();
                }

                if (choice == 11) {
                    int dmg = Math.max(1, player.getAttack() - monster.getDefend());
                    System.out.println("[战斗系统] " + playerName + " 使用普通攻击，造成 " + dmg + " 点伤害");
                    monster.takeDamageNetwork(dmg, out);
                    if (playercount == 0) {
                        out.println(monster.getName() + "的防御力为" + monsterolddefence);
                        out.println("你造成了 " + dmg + " 点伤害！");
                        monster.setDefend(0);
                    } else {
                        out.println("怪物只能防御一次伤害");
                        out.println("你造成了 " + dmg + " 点伤害！");
                    }
                    playercount = 1;
                } else if (choice == 12) {
                    System.out.println("[战斗系统] " + playerName + " 选择使用技能");
                    useSkillNetwork(player, monster, in, out, playerName);
                }

                if (!monster.isAlive()) {
                    System.out.println("[战斗系统] " + playerName + " 击败了怪物 " + monster.getName());
                    break;
                }

                int enemyDmg = Math.max(1, monster.getAttack() - player.getDefend());
                System.out.println("[战斗系统] " + playerName + " 受到怪物攻击，受到 " + enemyDmg + " 点伤害，当前血量: " + player.getLifeValue() + "/" + player.getMaxLifeValue());
                player.takeDamageNetwork(enemyDmg, out);
                if (monstercount == 0) {
                    out.println("你的防御力为" + playerolddefence);
                    out.println("你被造成了 " + enemyDmg + " 点伤害！");
                    player.setDefend(0);
                } else {
                    out.println(player.getName() + "的防御力为" + playerolddefence + "防御力只能抵挡一次伤害");
                    out.println("你被造成了 " + enemyDmg + " 点伤害！");
                }
                monstercount = 1;
            } catch (MissCannarinException e) {
                // 捕获自定义异常，提示用户仅支持11-12
                out.println(e.getMessage());
                continue;
            } catch (IOException | NumberFormatException e) {
                out.println("输入错误，请输入有效的数字（11-12）");
                continue;
            }
        }

        if (player.isAlive()) {
            System.out.println("[战斗系统] " + playerName + " 战斗胜利，获得经验: " + monster.getExp() + "，当前等级: " + player.getGrade());
            out.println("\n你击败了 " + monster.getName());
            player.gainExp(monster.getExp());
            // 战斗结束后自动回血
            player.healToFull();
            out.println("战斗结束，已自动恢复满血！");
            handler.sendStatus();
        } else {
            System.out.println("[战斗系统] " + playerName + " 战斗失败，已自动恢复满血");
            out.println("\n你被击败了……已自动恢复满血。");
            player.healToFull();
        }
        player.setDefend(playerolddefence);
        monster.setDefend(monsterolddefence);
    }

    private static void useSkillNetwork(Figure player, Monster monster, BufferedReader in, PrintWriter out, String playerName) {
        try {
            java.util.List<Skill> skills = player.getSkills();
            if (skills.isEmpty()) {
                out.println("你没有技能！");
                return;
            }

            out.println("选择技能：");
            for (int i = 0; i < skills.size(); i++) {
                out.println((i + 1) + ". " + skills.get(i).getName());
            }
            out.println("请选择:");

            String choiceStr = in.readLine();
            System.out.println("[战斗系统] " + playerName + " 选择技能: " + (choiceStr != null ? choiceStr : "空"));
            int ch = Integer.parseInt(choiceStr);

            if (ch < 1 || ch > skills.size()) {
                System.out.println("[战斗系统] " + playerName + " 技能选择无效");
                out.println("无效选择！请输入技能对应的编号");
                return;
            }

            Skill s = skills.get(ch - 1);
            System.out.println("[战斗系统] " + playerName + " 使用技能: " + s.getName() + " (伤害倍数: " + s.getDamageMultiplier() + ")");
            int dmg = s.calculateDamage(player.getAttack()) - monster.getDefend();
            dmg = Math.max(1, dmg);
            System.out.println("[战斗系统] " + playerName + " 使用技能 " + s.getName() + " 对怪物造成 " + dmg + " 点伤害");
            monster.takeDamageNetwork(dmg, out);
            out.println("你施放了 " + s.getName() + "，造成 " + dmg + " 点伤害！");
        } catch (IOException | NumberFormatException e) {
            out.println("技能使用错误！请输入有效的技能编号");
        }
    }

    /**
     * 玩家VS玩家战斗
     */
    public static void playerVsPlayer(Figure player1, Figure player2,
                                      ClientHandler handler1, ClientHandler handler2,
                                      NetworkGameServer server) {
        String player1Name = handler1.getPlayerName();
        String player2Name = handler2.getPlayerName();
        System.out.println("[PK系统] PVP战斗开始: " + player1Name + " vs " + player2Name);
        PrintWriter out1 = handler1.getOutput();
        PrintWriter out2 = handler2.getOutput();
        BufferedReader in1 = handler1.getInput();
        BufferedReader in2 = handler2.getInput();

        out1.println("\n========== PK战斗开始 ==========");
        out1.println("对手: " + player2.getName());
        out2.println("\n========== PK战斗开始 ==========");
        out2.println("对手: " + player1.getName());

        int player1OldDefend = player1.getDefend();
        int player2OldDefend = player2.getDefend();
        int player1Count = 0;
        int player2Count = 0;

        boolean player1Turn = true;

        while (player1.isAlive() && player2.isAlive()) {
            if (player1Turn) {
                // 玩家1的回合
                out1.println("\n=== 你的回合 ===");
                out1.printf("你 HP:%d | %s HP:%d\n", player1.getLifeValue(), player2.getName(), player2.getLifeValue());
                out1.println("11. 普通攻击");
                out1.println("12. 使用技能");
                out1.println("请选择(11-12):");

                out2.println("\n=== " + player1.getName() + " 的回合，等待中... ===");
                out2.printf("%s HP:%d | 你 HP:%d\n", player1.getName(), player1.getLifeValue(), player2.getLifeValue());

                try {
                    String choiceStr = in1.readLine();
                    System.out.println("[PK系统] " + player1Name + " (PVP回合) 选择: " + (choiceStr != null ? choiceStr : "空"));
                    int choice;
                    try {
                        choice = Integer.parseInt(choiceStr);
                    } catch (NumberFormatException e) {
                        out1.println("无效输入！请输入 11 或 12");
                        continue;
                    }

                    // 校验输入是否为11或12，否则抛出自定义异常
                    if (choice != 11 && choice != 12) {
                        throw new MissCannarinException();
                    }

                    if (choice == 11) {
                        int dmg = Math.max(1, player1.getAttack() - player2.getDefend());
                        System.out.println("[PK系统] " + player1Name + " 对 " + player2Name + " 使用普通攻击，造成 " + dmg + " 点伤害");
                        player2.takeDamageNetwork(dmg, out2);
                        player2.takeDamageNetwork(dmg, out1);

                        if (player2Count == 0) {
                            out1.println("你造成了 " + dmg + " 点伤害！");
                            out2.println(player1.getName() + " 对你造成了 " + dmg + " 点伤害！");
                            player2.setDefend(0);
                        } else {
                            out1.println("你造成了 " + dmg + " 点伤害！");
                            out2.println(player1.getName() + " 对你造成了 " + dmg + " 点伤害！");
                        }
                        player2Count = 1;
                    } else if (choice == 12) {
                        System.out.println("[PK系统] " + player1Name + " (PVP回合) 选择使用技能");
                        useSkillPVP(player1, player2, in1, out1, out2, handler1.getPlayerName());
                    }
                } catch (MissCannarinException e) {
                    // 捕获自定义异常，提示用户仅支持11-12
                    out1.println(e.getMessage());
                    continue;
                } catch (IOException e) {
                    out1.println("输入错误，请重试");
                    continue;
                }
            } else {
                // 玩家2的回合
                out2.println("\n=== 你的回合 ===");
                out2.printf("你 HP:%d | %s HP:%d\n", player2.getLifeValue(), player1.getName(), player1.getLifeValue());
                out2.println("11. 普通攻击");
                out2.println("12. 使用技能");
                out2.println("请选择(11-12):");

                out1.println("\n=== " + player2.getName() + " 的回合，等待中... ===");
                out1.printf("%s HP:%d | 你 HP:%d\n", player2.getName(), player2.getLifeValue(), player1.getLifeValue());

                try {
                    String choiceStr = in2.readLine();
                    System.out.println("[PK系统] " + player2Name + " (PVP回合) 选择: " + (choiceStr != null ? choiceStr : "空"));
                    int choice;
                    try {
                        choice = Integer.parseInt(choiceStr);
                    } catch (NumberFormatException e) {
                        out2.println("无效输入！请输入 11 或 12");
                        continue;
                    }

                    // 校验输入是否为11或12，否则抛出自定义异常
                    if (choice != 11 && choice != 12) {
                        throw new MissCannarinException();
                    }

                    if (choice == 11) {
                        int dmg = Math.max(1, player2.getAttack() - player1.getDefend());
                        System.out.println("[PK系统] " + player2Name + " 对 " + player1Name + " 使用普通攻击，造成 " + dmg + " 点伤害");
                        player1.takeDamageNetwork(dmg, out1);
                        player1.takeDamageNetwork(dmg, out2);

                        if (player1Count == 0) {
                            out2.println("你造成了 " + dmg + " 点伤害！");
                            out1.println(player2.getName() + " 对你造成了 " + dmg + " 点伤害！");
                            player1.setDefend(0);
                        } else {
                            out2.println("你造成了 " + dmg + " 点伤害！");
                            out1.println(player2.getName() + " 对你造成了 " + dmg + " 点伤害！");
                        }
                        player1Count = 1;
                    } else if (choice == 12) {
                        System.out.println("[PK系统] " + player2Name + " (PVP回合) 选择使用技能");
                        useSkillPVP(player2, player1, in2, out2, out1, handler2.getPlayerName());
                    }
                } catch (MissCannarinException e) {
                    // 捕获自定义异常，提示用户仅支持11-12
                    out2.println(e.getMessage());
                    continue;
                } catch (IOException e) {
                    out2.println("输入错误，请重试");
                    continue;
                }
            }

            if (!player1.isAlive() || !player2.isAlive()) break;

            player1Turn = !player1Turn;
        }

        // 战斗结束
        if (!player1.isAlive()) {
            System.out.println("[PK系统] PVP战斗结束: " + player2Name + " 获胜，击败了 " + player1Name);
            out1.println("\n========== 你被击败了！ ==========");
            out2.println("\n========== 你获胜了！ ==========");
            player1.healToFull();
            player2.gainExp(50); // 获胜奖励经验
            handler2.sendStatus();
        } else if (!player2.isAlive()) {
            System.out.println("[PK系统] PVP战斗结束: " + player1Name + " 获胜，击败了 " + player2Name);
            out2.println("\n========== 你被击败了！ ==========");
            out1.println("\n========== 你获胜了！ ==========");
            player2.healToFull();
            player1.gainExp(50); // 获胜奖励经验
            handler1.sendStatus();
        }

        player1.setDefend(player1OldDefend);
        player2.setDefend(player2OldDefend);

        handler1.showMainMenu();
        handler2.showMainMenu();
    }

    private static void useSkillPVP(Figure attacker, Figure defender, BufferedReader in,
                                    PrintWriter outAttacker, PrintWriter outDefender, String attackerName) {
        try {
            java.util.List<Skill> skills = attacker.getSkills();
            if (skills.isEmpty()) {
                outAttacker.println("你没有技能！");
                return;
            }

            outAttacker.println("选择技能：");
            for (int i = 0; i < skills.size(); i++) {
                outAttacker.println((i + 1) + ". " + skills.get(i).getName());
            }
            outAttacker.println("请选择:");

            String choiceStr = in.readLine();
            System.out.println("[PK系统] " + attackerName + " (PVP技能选择) 选择: " + (choiceStr != null ? choiceStr : "空"));
            int ch = Integer.parseInt(choiceStr);

            if (ch < 1 || ch > skills.size()) {
                System.out.println("[PK系统] " + attackerName + " (PVP技能选择) 无效选择");
                outAttacker.println("无效选择！请输入技能对应的编号");
                return;
            }

            Skill s = skills.get(ch - 1);
            int dmg = s.calculateDamage(attacker.getAttack()) - defender.getDefend();
            dmg = Math.max(1, dmg);
            System.out.println("[PK系统] " + attackerName + " 在PVP中使用技能 " + s.getName() + " 对 " + defender.getName() + " 造成 " + dmg + " 点伤害");
            defender.takeDamageNetwork(dmg, outAttacker);
            defender.takeDamageNetwork(dmg, outDefender);
            outAttacker.println("你施放了 " + s.getName() + "，对 " + defender.getName() + " 造成 " + dmg + " 点伤害！");
            outDefender.println(attackerName + " 施放了 " + s.getName() + "，对你造成 " + dmg + " 点伤害！");
        } catch (IOException | NumberFormatException e) {
            outAttacker.println("技能使用错误！请输入有效的技能编号");
        }
    }
}