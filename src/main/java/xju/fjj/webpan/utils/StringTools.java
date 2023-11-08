package xju.fjj.webpan.utils;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 关于字符串处理的工具类
 * @date 2023/10/18 21:06
 */
public class StringTools {
    /*生成指定长度的随机数字字符串*/
    public static String getRandomNumber(int length) {
        //返回纯数字字符串
        return RandomStringUtils.random(length,false,true);
    }

    /*获取文件的后缀*/
    public static String getFileSuffix(String fileName){
        int index = fileName.lastIndexOf(".");
        if(index == -1)
            return "";
        return fileName.substring(++index);
    }
}
