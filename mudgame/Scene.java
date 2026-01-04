package com.mudgame;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * 游戏场景类
 */
public class Scene {
    private String name;
    private String description;
    private List<String> availableActions;


    public Scene(String name, String description) {
        this.name = name;
        this.description = description;
        this.availableActions = new ArrayList<>();
    }

    public void addAction(String action) {
        availableActions.add(action);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getAvailableActions() {
        return availableActions;
    }

    public String getMapDisplay() {
        StringBuilder map = new StringBuilder();
        // 修复乱码的分隔符（使用中文全角分隔符，清晰规整）
        map.append("════════════════════════════════════════════════════════════════\n");
        map.append("**               ").append(name).append("                     **\n");
        map.append("════════════════════════════════════════════════════════════════\n");
        map.append("**            ").append(description).append("                  **\n");
        map.append("════════════════════════════════════════════════════════════════\n");
        return map.toString();
    }
    public void enter(Figure player, Scanner scanner) {
        System.out.println("进入场景：" + name + "，暂无特殊逻辑");
    }
}