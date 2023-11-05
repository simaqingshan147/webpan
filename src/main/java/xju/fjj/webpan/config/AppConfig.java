package xju.fjj.webpan.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 读取配置文件中变量的bean
 * @date 2023/10/16 0:12
 */
@Component("appConfig")
public class AppConfig {
    @Value("${project.folder}")
    private String projectFolder;

    @Value("${project.adminEmails:34900139755@qq.com}")
    private String adminEmails;

    @Value("${project.totalSpace:5}")
    private Integer initialTotalSpace;

    @Value("${project.email.register.title:webpan用户注册}")
    private String registerEmailTitle;

    @Value("${project.email.register.content:您好,您的邮箱验证码是:%s,5分钟有效}")
    private String registerEmailContent;

    @Value("${project.email.resetPwd.title:webpan密码重置}")
    private String resetPwdEmailTitle;

    @Value("${project.email.resetPwd.content:您好,您的邮箱验证码是:%s,10分钟有效}")
    private String resetPwdEmailContent;

    public AppConfig() {
    }

    public String getProjectFolder() {
        return projectFolder;
    }

    public String getAdminEmails() {
        return adminEmails;
    }

    public Integer getInitialTotalSpace() {
        return initialTotalSpace;
    }

    public String getRegisterEmailTitle() {
        return registerEmailTitle;
    }

    public String getRegisterEmailContent() {
        return registerEmailContent;
    }

    public String getResetPwdEmailTitle() {
        return resetPwdEmailTitle;
    }

    public String getResetPwdEmailContent() {
        return resetPwdEmailContent;
    }
}
