package xju.fjj.webpan.service.impl;

import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import xju.fjj.webpan.component.RedisUtils;
import xju.fjj.webpan.config.AppConfig;
import xju.fjj.webpan.entity.constants.Constants;
import xju.fjj.webpan.entity.enums.ResultCodeEnum;
import xju.fjj.webpan.entity.pojo.UserInfo;
import xju.fjj.webpan.exception.ServerException;
import xju.fjj.webpan.mapper.UserInfoMapper;
import xju.fjj.webpan.service.EmailCodeService;
import xju.fjj.webpan.utils.StringTools;

import java.util.Date;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 邮件服务实现类
 * @date 2023/10/19 17:37
 */
@Service
public class EmailCodeServiceImpl implements EmailCodeService {
    @Resource
    private RedisUtils<String> redisUtils;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private AppConfig appConfig;
    @Resource
    private JavaMailSender mailSender;

    @Override
    /*根据类型发送邮箱验证码,包装*/
    public void sendEmailCode(String email, Integer type) {
        //注册
        if(type == Constants.EMAIL_CODE_TYPE_REGISTER) {
            UserInfo userInfo = userInfoMapper.selectByEmail(email);
            if (userInfo != null)
                throw new ServerException(ResultCodeEnum.CODE_601, "邮箱已存在");
        }
        String key = getPrefix(type)+email;
        String code = StringTools.getRandomNumber(Constants.LENGTH_5);
        int time = getTime(type);
        //删除之前的验证码
        redisUtils.delete(key);
        //存储新验证码
        redisUtils.set(key,code,time);
        //发送验证码
        send(email,code,type);
    }

    /*验证邮箱验证码*/
    public boolean checkEmailCode(String email,String code,Integer type){
        String key = getPrefix(type)+email;
        String realEmailCode = redisUtils.getAndDelete(key);
        if(realEmailCode == null)
            return false;
        return code.equals(realEmailCode);
    }


    /*根据类型发送验证码,发送逻辑*/
    private void send(String toEmail,String code,Integer type){
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String title = getTitle(type);
            String content = getContent(type);
            helper.setFrom(appConfig.getAdminEmails());
            helper.setTo(toEmail);
            helper.setSubject(title);
            helper.setText(String.format(content,code));
            helper.setSentDate(new Date());
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new ServerException(ResultCodeEnum.CODE_500,"邮件发送失败");
        }
    }


    private String getTitle(Integer type) {
        if(type == Constants.EMAIL_CODE_TYPE_REGISTER)
            return appConfig.getRegisterEmailTitle();
        else if (type == Constants.EMAIL_CODE_TYPE_RESET)
            return appConfig.getResetPwdEmailTitle();
        return "无主题";
    }

    private String getContent(Integer type) {
        if(type == Constants.EMAIL_CODE_TYPE_REGISTER)
            return appConfig.getRegisterEmailContent();
        else if (type == Constants.EMAIL_CODE_TYPE_RESET)
            return appConfig.getResetPwdEmailContent();
        return "";
    }

    private String getPrefix(Integer type){
        return switch (type){
            case Constants.EMAIL_CODE_TYPE_REGISTER -> Constants.REDIS_EMAIL_CODE_REGISTER_PREFIX;
            case Constants.EMAIL_CODE_TYPE_RESET -> Constants.REDIS_EMAIL_CODE_RESET_PREFIX;
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    private int getTime(Integer type) {
        return switch (type){
            case Constants.EMAIL_CODE_TYPE_REGISTER -> Constants.REDIS_EMAIL_CODE_REGISTER_TIME;
            case Constants.EMAIL_CODE_TYPE_RESET -> Constants.REDIS_EMAIL_CODE_RESET_TIME;
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

}
