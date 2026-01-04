package com.mudgame;
import java.util.List;
import java.util.Scanner;
public class BattleSystem {
    public static void fight(Figure p, Monster m, Scanner sc) {
        System.out.println("战斗开始！");
        int playercount=0;
        int monstercount=0;
        int playerolddefence=p.getDefend();
        int monsterolddefence= m.getDefend();
        while (p.isAlive() && m.isAlive()) {

            System.out.printf("\n你 HP:%d | %s HP:%d\n", p.getLifeValue(), m.getName(), m.getLifeValue());
            System.out.println("1. 普通攻击");
            System.out.println("2. 使用技能");

            int choice = getInt(sc, 1, 2);

            if (choice == 1) {
                int dmg = Math.max(1, p.getAttack() -m.getDefend());
                m.takeDamage(dmg);
                if(playercount==0) {
                    System.out.println(m.getName()+"的防御力为" + monsterolddefence);
                    System.out.println("你造成了 " + dmg + " 点伤害！");

                    m.setDefend(0);
                }if(playercount==1) {
                    System.out.println("怪物只能防御一次伤害");
                    System.out.println("你造成了 " + dmg + " 点伤害！");

                }
                playercount=1;
            } else {
                useSkill(p, m, sc);
            }

            if (!m.isAlive()) break;

            int enemyDmg = Math.max(1, m.getAttack() - p.getDefend());
            p.takeDamage(enemyDmg);
            if(monstercount==0) {
                System.out.println("你的防御力为" + playerolddefence);
                System.out.println("你被造成了 " + enemyDmg + " 点伤害！");

                p.setDefend(0);
            }if(monstercount==1) {
                System.out.println(p.getName()+"的防御力为" + playerolddefence+"防御力只能抵挡一次伤害");
                System.out.println("你造成了 " + enemyDmg + " 点伤害！");

            }
            monstercount=1;
        }

        if (p.isAlive()) {
            System.out.println("\n你击败了 " + m.getName());
            p.gainExp(m.getExp());

        } else {
            System.out.println("\n你被击败了……已自动恢复满血。");
            p.healToFull();
        }
        p.setDefend(playerolddefence);
        m.setDefend(monsterolddefence);
    }

    private static void useSkill(Figure p, Monster m, Scanner sc) {
        List<Skill> skills = p.getSkills();
        System.out.println("选择技能：");
        for (int i = 0; i < skills.size(); i++) {
            System.out.println((i + 1) + ". " + skills.get(i).getName());
        }

        int ch = getInt(sc, 1, skills.size());
        Skill s = skills.get(ch - 1);

        int dmg = s.calculateDamage(p.getAttack()) - m.getDefend();
        dmg = Math.max(1, dmg);
        m.takeDamage(dmg);

        System.out.println("你施放了 " + s.getName() + "，造成 " + dmg + " 点伤害！");
    }

    private static int getInt(Scanner sc, int min, int max) {
        while (true) {
            try {
                int v = Integer.parseInt(sc.nextLine());
                if (v >= min && v <= max) return v;
            } catch (Exception ignored) {}
            System.out.print("请输入 " + min + "-" + max + "：");
        }
    }
}
