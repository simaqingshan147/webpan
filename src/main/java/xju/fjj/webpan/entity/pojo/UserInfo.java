package xju.fjj.webpan.entity.pojo;

import xju.fjj.webpan.entity.query.UserInfoQuery;

import java.io.Serializable;
import java.util.Date;

public class UserInfo implements Serializable{
    /**
     * 用户id
     */
    private String userId;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 密码
     */
    private String password;

    /**
     * 注册时间
     */
    private Date joinTime;

    /**
     * 最近登录时间
     */
    private Date lastLoginTime;

    /**
     * 0:封禁 1:解禁
     */
    private Integer status;

    /**
     * 使用空间,单位byte
     */
    private Long useSpace;

    /**
     * 总空间,单位byte
     */
    private Long totalSpace;

    public UserInfo() {
    }

    public UserInfo(UserInfoQuery query){
        this.userId = query.getUserId();
        this.nickName = query.getNickName();
        this.status = query.getStatus();
    }

    public UserInfo(String userId, String nickName, String email, String password, Date joinTime, Date lastLoginTime, Integer status, Long useSpace, Long totalSpace) {
        this.userId = userId;
        this.nickName = nickName;
        this.email = email;
        this.password = password;
        this.joinTime = joinTime;
        this.lastLoginTime = lastLoginTime;
        this.status = status;
        this.useSpace = useSpace;
        this.totalSpace = totalSpace;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(Date joinTime) {
        this.joinTime = joinTime;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getUseSpace() {
        return useSpace;
    }

    public void setUseSpace(Long useSpace) {
        this.useSpace = useSpace;
    }

    public Long getTotalSpace() {
        return totalSpace;
    }

    public void setTotalSpace(Long totalSpace) {
        this.totalSpace = totalSpace;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userId='" + userId + '\'' +
                ", nickName='" + nickName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", joinTime=" + joinTime +
                ", lastLoginTime=" + lastLoginTime +
                ", status=" + status +
                ", useSpace=" + useSpace +
                ", totalSpace=" + totalSpace +
                '}';
    }
}

