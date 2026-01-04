package com.mudgame;
import java.util.Scanner;

public class BattleScene extends Scene {
    // 添加无参构造方法，调用父类带参构造初始化场景信息
    public BattleScene() {
        super("战斗场景", "充满危险的战斗区域，怪物出没，适合练级");
    }

    @Override
    public String getName() {
        return "battle";
    }

    @Override
    public void enter(Figure p, Scanner sc) {
        Monster m = new Monster();
        System.out.println("\n你遇到了怪物：" + m.getName());
        BattleSystem.fight(p, m, sc);
    }
}