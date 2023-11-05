package xju.fjj.webpan.service;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 邮箱验证码相关service
 * @date 2023/10/19 17:36
 */
public interface EmailCodeService {
    void sendEmailCode(String email, Integer type);
    boolean checkEmailCode(String email, String code, Integer type);
}
