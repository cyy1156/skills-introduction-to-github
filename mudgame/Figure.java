package com.mudgame;

import java.util.ArrayList;
import java.util.List;

public class Figure {

    // 修改Figure类中的属性声明
    protected int LifeValue;
    protected int Exp;
    protected int Grade;
    protected int MaxLifeValue;
    protected String name;
    protected int defend;
    protected int attack;
    protected List<Skill> skills;

    public int getMaxLifeValue() {
        return MaxLifeValue;
    }

    //初始化
    public  Figure(String name)
    {
        this.name=name;
        this.Grade=1;
        this.Exp =0;
        this.defend=5;
        this.attack=10;
        this.MaxLifeValue=100;
        this.LifeValue= MaxLifeValue;
        this.skills= new ArrayList<>();
    }

    public void setDefend(int defend) {
        this.defend = defend;
    }

    //遭受伤害
    public void takeDamage (int attack) {
        int oldLifeValue = this.LifeValue;

        int totalattack=attack-defend;
        if(totalattack<0)
        {
            totalattack=1;
        }
        this.LifeValue = Math.max(0, oldLifeValue  - totalattack); // 血量最低为0
        // 扣血效果显示（MUD风格文本提示）
        System.out.println(name + " 受到 " + attack + " 点伤害！");
        System.out.println("血量变化：" + oldLifeValue + " → " + this.LifeValue);
        if (this.LifeValue == 0) {
            System.out.println(name + " 已阵亡！");
        }
    }
    
    //网络版本的遭受伤害（使用PrintWriter输出）
    public void takeDamageNetwork(int attack, java.io.PrintWriter out) {
        int oldLifeValue = this.LifeValue;

        int totalattack = attack - defend;
        if (totalattack < 0) {
            totalattack = 1;
        }
        this.LifeValue = Math.max(0, oldLifeValue - totalattack); // 血量最低为0
        // 扣血效果显示（MUD风格文本提示）
        out.println(name + " 受到 " + attack + " 点伤害！");
        out.println("血量变化：" + oldLifeValue + " → " + this.LifeValue);
        if (this.LifeValue == 0) {
            out.println(name + " 已阵亡！");
        }
    }


    //升级
    private void Upgrade()
    {

            Grade++;
            LifeValue+=10;
            MaxLifeValue+=10;
            defend+=5;
            attack+=5;
            Exp =0;
           System.out.println("升级啦！现在"+Grade+"级");

    }
    //显示状态
    public void showStatus() {
        System.out.println("===== 角色状态 =====");
        System.out.println("名字：" + name);
        System.out.println("等级：" + Grade);
        System.out.println("血量：" + LifeValue + "/" + MaxLifeValue);
        System.out.println("攻击：" + attack);
        System.out.println("防御：" + defend);
        System.out.println("经验："+Exp);
        System.out.println("技能：");
        for (Skill s : skills) System.out.println("- " + s.getName());
    }
    public void learnSkill(Skill s) {
        skills.add(s);
    }
    public List<Skill> getSkills() { return skills; }
    public int getAttack() {
        return attack;
    }
    public int getDefend() {
        return defend;
    }
    public String getName() {
        return name;
    }
    public int getGrade() {
        return Grade;
    }
    public int getLifeValue() {
        return LifeValue;
    }
    public int getExp() {
        return Exp;
    }
    boolean isAlive() { return LifeValue > 0; }
    public void gainExp(int rewardexp)
    {
        Exp+=rewardexp;
        if(Exp >=100)
        {
            Upgrade();
            Exp =0;
        }
    }
    public void healToFull()
    {
        LifeValue=MaxLifeValue;
    }
}
