package com.mudgame;

public class Skill {
    private String name;
    private int damageMultiplier;

    public String getDescription() {
        return description;
    }

    private String description;

    public Skill(String name, int damageMultiplier, String description) {
        this.name = name;
        this.damageMultiplier = damageMultiplier;
        this.description = description;
    }

    public String getName() { return name; }
    public int getDamageMultiplier() { return damageMultiplier; }

    public int calculateDamage(int playerAttack) {
        return playerAttack * damageMultiplier;
    }
}
