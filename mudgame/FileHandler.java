package com.mudgame;

import java.io.*;

public class FileHandler {
    private static final String SAVE_FILE = "D:/java/USST/src/com/mudgame/save.txt";


    // 保存角色信息到文件
    public static void saveGame(Figure player) {
        try (PrintWriter out = new PrintWriter(new FileWriter(SAVE_FILE))) {
            out.println(player.getName());
            out.println(player.getGrade());
            out.println(player.getExp());
            out.println(player.getLifeValue());
            out.println(player.getMaxLifeValue());
            out.println(player.getDefend());
            out.println(player.getAttack());

            // 保存技能
            out.println(player.getSkills().size());
            for (Skill skill : player.getSkills()) {
                out.println(skill.getName());
                out.println(skill.getDamageMultiplier());
                out.println(skill.getDescription());
            }
            System.out.println("游戏已保存！");
        } catch (IOException e) {
            System.out.println("保存失败：" + e.getMessage());
        }
    }

    // 从文件读取角色信息
    public static Figure loadGame() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            System.out.println("没有找到存档文件！");
            return null;
        }

        try (BufferedReader in = new BufferedReader(new FileReader(SAVE_FILE))) {
            String name = in.readLine();
            Figure player = new XiaoYan(); // 假设目前只有萧言角色

            player = new Figure(name) {
                // 空实现，仅用于初始化
            };

            player.Grade = Integer.parseInt(in.readLine());
            player.Exp = Integer.parseInt(in.readLine());
            player.LifeValue = Integer.parseInt(in.readLine());
            player.MaxLifeValue = Integer.parseInt(in.readLine());
            player.defend = Integer.parseInt(in.readLine());
            player.attack = Integer.parseInt(in.readLine());

            // 读取技能
            int skillCount = Integer.parseInt(in.readLine());
            for (int i = 0; i < skillCount; i++) {
                String skillName = in.readLine();
                int multiplier = Integer.parseInt(in.readLine());
                String desc = in.readLine();
                player.learnSkill(new Skill(skillName, multiplier, desc));
            }

            System.out.println("存档加载成功！");
            return player;
        } catch (IOException e) {
            System.out.println("加载失败：" + e.getMessage());
            return null;
        }
    }
}