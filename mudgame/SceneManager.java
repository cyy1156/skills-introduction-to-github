package com.mudgame;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class SceneManager {
    private Map<String, Scene> scenes = new HashMap<>();
    private Figure player;
    private Scanner scanner;

    public SceneManager(Figure player, Scanner scanner) {
        this.player = player;
        this.scanner = scanner;
    }

    public void registerScene(Scene scene) {
        scenes.put(scene.getName(), scene);
    }

    public void enterScene(String name) {
        if (scenes.containsKey(name))
            scenes.get(name).enter(player, scanner);
        else
            System.out.println("场景不存在：" + name);
    }
}
