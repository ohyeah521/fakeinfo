package org.gosky.fakeinfo;

public class GeneratorSmsContent {
    private static final String[] messages = new String[]{"大家好", "人人影视走你", "人人美剧一二三", "人人我来了", "今天", "TMD.......",
            "good job", "大王派我来巡山", "小伙们,go go go", "对不起", "好想睡觉啊", "睡不着", "一个人过的第九年", "纸牌屋要出来了", "今年还能不能看到冰与火之歌",
            "怎么下载", "有电视机"};
    private static final String[] messages2 = new String[]{"今天是个好日子", "想说点什么", "看美剧", "乌合之众们", "哈里波特大", "伊丽沙白素珍",
            "海贼王的男人", "小丽", "好日子", "快过年了", "好晚了现在", "这是第几次了", "Time after Time", "大王"};
    private static final String[] messages3 = new String[]{"我爱你!!", "加油!!", "振作起来啊!!", "不给力啊!!", "啊啊啊!!", "好难受!!!",
            "不行啊这样!!", "不想这样了.....", "HELP!!!!", "我也不想这样啊", "积分怎么不动了?", "我的信心快用光了,你们倒是给力点啊", "刷积分真尼玛坑", "再过两天就过年回家了"};


    public static String random()  {
       return messages[(int) (Math.random() * messages.length)] + ","
                + messages2[(int) (Math.random() * messages2.length)] + ","
                + messages3[(int) (Math.random() * messages3.length)];
    }
}