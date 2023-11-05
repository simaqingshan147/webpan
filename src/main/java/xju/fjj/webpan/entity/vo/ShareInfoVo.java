package xju.fjj.webpan.entity.vo;

import xju.fjj.webpan.entity.pojo.UserInfo;

import java.util.Date;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 返回给前端的分享文件/文件夹和所属用户信息的vo
 * @date 2023/11/2 21:12
 */
public class ShareInfoVo {
    private String userId;  //所属用户id
    private String nickName;
    private Boolean currentUser;
    private Integer id;
    private String  name;
    private Date shareTime;
    private Date expireTime;

    public static ShareInfoVo covert(UserInfo userInfo, FileShareVo fileShareVo) {
        ShareInfoVo shareInfoVo = new ShareInfoVo();
        shareInfoVo.setUserId(userInfo.getUserId());
        shareInfoVo.setNickName(userInfo.getNickName());
        shareInfoVo.setId(fileShareVo.getId());
        shareInfoVo.setName(fileShareVo.getName());
        shareInfoVo.setShareTime(fileShareVo.getShareTime());
        shareInfoVo.setExpireTime(fileShareVo.getExpireTime());
        return shareInfoVo;
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

    public Boolean getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Boolean currentUser) {
        this.currentUser = currentUser;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getShareTime() {
        return shareTime;
    }

    public void setShareTime(Date shareTime) {
        this.shareTime = shareTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }
}
