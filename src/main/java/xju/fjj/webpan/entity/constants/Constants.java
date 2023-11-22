package xju.fjj.webpan.entity.constants;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description  存储不会轻易修改的变量值
 * @date  2023/10/16 0:04
 */
public class Constants {
    /*session相关*/


    public static final String CHECK_CODE_KEY = "check_code_key";  //session存储验证码的键
    public static final String SESSION_KEY = "session_key";       //session存储用户信息的键
    public static final String SESSION_SHARE_KEY_PREFIX = "session_share_key";  //session存储分享文件信息的键的前缀
    /*文件相关*/

    public static final int MAIN_FOLDER_ID = 0;         //首页文件和目录的pid，即“首页文件夹"的dir_id
    public static final String FILE_FOLDER_FILE = "file/";  //文件存储的项目下相对路径
    public static final String FILE_FOLDER_TEMP = "temp/";   //临时文件处理的项目下相对路径
    public static final String TS_NAME = "index.ts";
    public static final String M3U8_NAME = "index.m3u8";
    public static final int LENGTH_5 = 5;
    public static final int LENGTH_10 = 10;
    public static final int LENGTH_15 = 15;
    public static final Long MB = 1024*1024L;
    /*头像相关*/
    public static final String FILE_FOLDER_AVATAR = "avatar/";  //头像存储的文件下相对路径
    public static final String AVATAR_DEFAULT_NAME = "default_avatar.jpg";   //默认头像文件名
    public static final String AVATAR_SUFFIX = ".jpg";   //头像文件的后缀
    /*封面相关*/
    public static final String FILE_FOLDER_COVER = "cover/";
    public static final String DEFAULT_COVER_FOLDER= "defaultCover/";
    public static final String FILE_COVER_NAME = "file.png";
    public static final String FOLDER_COVER_NAME = "folder.png";
    public static final String COVER_SUFFIX = ".png";
    /*redis相关*/


    public static final Integer REDIS_ONE_MIN = 60;
    public static final Integer REDIS_FIVE_MIN = 300;
    public static final Integer REDIS_ONE_HOUR = 3600;
    public static final Integer REDIS_ONE_DAY = 86400;
    public static final String REDIS_USER_SPACE_PREFIX = "webpan:user:space:";
    /*文件相关*/
    public static final String REDIS_DOWNLOAD_FILE_PREFIX = "webpan:file:download:";
    public static final String REDIS_TEMP_FILE_SIZE_PREFIX = "webpan:file:temp:";
    /*邮件相关*/
    public static final int EMAIL_CODE_TYPE_REGISTER = 0;   //注册用邮箱验证码
    public static final int EMAIL_CODE_TYPE_RESET = 1;      //更新密码用邮箱验证码
    public static final String REDIS_EMAIL_CODE_REGISTER_PREFIX = "webpan:email:register:";  //redis注册用邮箱验证码的键的前缀
    public static final String REDIS_EMAIL_CODE_RESET_PREFIX = "webpan:email:reset:";     //redis重置密码用邮箱验证码的键的前缀
    public static final Integer REDIS_EMAIL_CODE_REGISTER_TIME = REDIS_FIVE_MIN;
    public static final Integer REDIS_EMAIL_CODE_RESET_TIME = REDIS_FIVE_MIN*2;
    public static final Integer LENGTH_150 = 150;
}
