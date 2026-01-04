package com.mudgame;
import com.javaclass.keyboardinput;
public class rule {
    static void showrule()
    {
        System.out.println("*******欢迎来到MUD_YoungGame*********");
        System.out.println("1.角色介绍");
        System.out.println("2.属性规则");
        System.out.println("3.关于我们");

        int choice=keyboardinput.getInt(1,3);
        switch (choice)
        {
            case 1:
                System.out.println("1.萧言");
                System.out.println("2.王楚");
                int choice1=keyboardinput.getInt(1,2);
                switch (choice1)
                {
                    case 1:
                        System.out.println("萧言，出生于蛇岛，励志要成为一个武林高手");
                        System.out.println("第一技能：火");
                        System.out.println("第二技能：水");
                        System.out.println("第三技能：蛇");
                    case 2:
                        System.out.println("王楚，出生于水镇，励志要成为武林高手");
                        System.out.println("第一技能：土");
                        System.out.println("第二技能：火");
                        System.out.println("第三技能：水");
                }
                break;
            case 2:
                System.out.println("每个角色的初始血量为100，等级为零（最高二十级），以及防御能力和进攻能力，无经验值（经验打怪随机掉落）"+"\n"
                        +"\n"+"每次经验值达到100可以升级，每次升级可以加10血量，加5防御和5进攻"+"\n"
                        +"\n"+"每个角色会有一个半生技能，达到10级和20级会获得二三技能"
                        +"\n"+"技能会随着等级的增加技能效果会增加");

                break;
            default:
                break;
        }

    }
}
