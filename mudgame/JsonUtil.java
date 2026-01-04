package com.mudgame;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

/**
 * JSON工具类，使用Gson库进行JSON序列化和反序列化
 * 
 * 使用前需要添加Gson依赖：
 * 1. 下载gson-2.10.1.jar（或更新版本）
 * 2. 编译时添加到classpath: javac -cp ".;gson-2.10.1.jar" ...
 * 3. 运行时添加到classpath: java -cp ".;gson-2.10.1.jar" ...
 * 
 * 或者使用Maven添加依赖：
 * <dependency>
 *     <groupId>com.google.code.gson</groupId>
 *     <artifactId>gson</artifactId>
 *     <version>2.10.1</version>
 * </dependency>
 */
public class JsonUtil {
    
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()  // 格式化输出，便于阅读
            .create();
    
    /**
     * 将角色数据转换为JSON字符串
     */
    public static String figureToJson(Figure figure) {
        try {
            // 创建一个临时的DTO对象来序列化，因为Figure的字段名不标准
            FigureDTO dto = new FigureDTO(figure);
            return gson.toJson(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 将角色数据转换为JSON字符串（带角色类型）
     */
    public static String figureToJson(Figure figure, String roleType) {
        try {
            FigureDTO dto = new FigureDTO(figure, roleType);
            return gson.toJson(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 从JSON字符串解析角色数据
     */
    public static Figure figureFromJson(String jsonStr) {
        try {
            FigureDTO dto = gson.fromJson(jsonStr, FigureDTO.class);
            if (dto == null) {
                return null;
            }
            
            // 根据角色类型创建对应的角色实例
            Figure figure;
            if (dto.roleType != null && dto.roleType.equals("Mo")) {
                figure = new Mo();
            } else {
                figure = new XiaoYan(); // 默认为萧言，兼容旧存档
            }
            
            figure.name = dto.name;
            figure.Grade = dto.grade;
            figure.Exp = dto.exp;
            figure.LifeValue = dto.lifeValue;
            figure.MaxLifeValue = dto.maxLifeValue;
            figure.defend = dto.defend;
            figure.attack = dto.attack;
            
            // 恢复技能列表
            if (dto.skills != null) {
                for (SkillDTO skillDto : dto.skills) {
                    figure.learnSkill(new Skill(skillDto.name, skillDto.damageMultiplier, skillDto.description));
                }
            }
            
            return figure;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 从JSON文件读取NPC列表
     */
    public static List<Npc> loadNpcsFromFile(String filePath) {
        List<Npc> npcs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // 使用Gson直接解析JSON数组
            Type listType = new TypeToken<List<NpcDTO>>(){}.getType();
            List<NpcDTO> npcDtos = gson.fromJson(reader, listType);
            
            if (npcDtos != null) {
                for (NpcDTO npcDto : npcDtos) {
                    if (npcDto.options != null && !npcDto.options.isEmpty()) {
                        npcs.add(new Npc(npcDto.name, npcDto.question, npcDto.answer, npcDto.exp, npcDto.options, npcDto.dialogue));
                    } else {
                        // 兼容旧格式
                        npcs.add(new Npc(npcDto.name, npcDto.question, npcDto.answer, npcDto.exp));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return npcs;
    }
    
    /**
     * Figure数据传输对象，用于JSON序列化
     */
    private static class FigureDTO {
        String name;
        int grade;
        int exp;
        int lifeValue;
        int maxLifeValue;
        int defend;
        int attack;
        List<SkillDTO> skills;
        String roleType; // 角色类型：XiaoYan 或 Mo
        
        FigureDTO(Figure figure) {
            this.name = figure.getName();
            this.grade = figure.getGrade();
            this.exp = figure.getExp();
            this.lifeValue = figure.getLifeValue();
            this.maxLifeValue = figure.getMaxLifeValue();
            this.defend = figure.getDefend();
            this.attack = figure.getAttack();
            this.skills = new ArrayList<>();
            if (figure.getSkills() != null) {
                for (Skill skill : figure.getSkills()) {
                    this.skills.add(new SkillDTO(skill));
                }
            }
            // 根据实例类型判断角色类型
            if (figure instanceof Mo) {
                this.roleType = "Mo";
            } else {
                this.roleType = "XiaoYan"; // 默认为萧言
            }
        }
        
        FigureDTO(Figure figure, String roleType) {
            this(figure);
            this.roleType = roleType;
        }
    }
    
    /**
     * Skill数据传输对象，用于JSON序列化
     */
    private static class SkillDTO {
        String name;
        int damageMultiplier;
        String description;
        
        SkillDTO(Skill skill) {
            this.name = skill.getName();
            this.damageMultiplier = skill.getDamageMultiplier();
            this.description = skill.getDescription();
        }
    }
    
    /**
     * Npc数据传输对象，用于JSON序列化
     */
    private static class NpcDTO {
        String name;
        String question;
        String answer;
        int exp;
        String dialogue;
        List<String> options;
        // 忽略id字段（如果存在）
    }
}

