package com.mudgame;

public class Mo extends Figure {

    public Mo()
    {
        super("莫");
        System.out.println("莫出生于云山，励志成为武林高手。");
        initialise();
    }
    
    public void initialise() {
        Skill firstskill = new Skill("风系突刺", 2, "风系强化刺击");
        learnSkill(firstskill);
        System.out.println("你获得了第一技能" + firstskill.getName());
        int Grade = this.getGrade();
        if (Grade == 10) {
            Skill secondskill = new Skill("云影步", 1, "轻盈回避动作");
            learnSkill(secondskill);
            System.out.println("你获得了新技能" + secondskill.getName());
        }
        if (Grade == 20) {
            Skill thirdskill = new Skill("风云连击", 3, "快速三连打");
            learnSkill(thirdskill);
            System.out.println("你获得了新技能" + thirdskill.getName());
        }
    }
}
