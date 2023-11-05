package xju.fjj.webpan.entity.dto;

import java.io.Serializable;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 存于session中反映用户登录状态的dto
 * @date 2023/10/18 21:54
 */
public class SessionUserDto implements Serializable{
    private String userId;
    private String nickName;
    private boolean isAdmin;
    private String email;

    public SessionUserDto() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
