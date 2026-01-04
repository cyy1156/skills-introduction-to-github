package com.mudgame;

import java.util.List;
import java.util.ArrayList;

public class Npc {
    private String name;
    private String question;
    private String answer;
    private int exp;
    private List<String> options;  // 问题选项列表
    private String dialogue;

    // 构造方法（基础参数：无选项）
    public Npc(String name, String question, String answer, int exp) {
        this.name = name;
        this.answer = answer;
        this.exp = exp;
        this.question = question;
        this.options = new ArrayList<>();
    }

    // 重载构造方法（支持选择题+对话内容）
    public Npc(String name, String question, String answer, int exp, List<String> options, String dialogue) {
        this.name = name;
        this.question = question;
        this.answer = answer;
        this.exp = exp;
        this.options = options != null ? options : new ArrayList<>();
        this.dialogue = dialogue;
    }

    public boolean checkAnswer(String input) {
        // 支持数字选项（1, 2, 3等）和文字答案
        if (input.trim().matches("^\\d+$")) {
            int choice = Integer.parseInt(input.trim());
            if (choice >= 1 && choice <= options.size()) {
                String selectedOption = options.get(choice - 1);
                return selectedOption.equalsIgnoreCase(answer);
            }
        }
        return input.equalsIgnoreCase(answer);
    }

    public String getName() {
        return name;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public int getExp() {
        return exp;
    }

    public List<String> getOptions() {
        return options;
    }

    public String getDialogue() {
        return dialogue;
    }

    public boolean hasOptions() {
        return options != null && !options.isEmpty();
    }
}