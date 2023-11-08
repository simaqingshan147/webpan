package xju.fjj.webpan.entity.dto;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 保存分享文件信息的session
 * @date 2023/11/2 20:29
 */
public class SessionShareDto {
    private Integer shareId;  //分享id
    private String userId;   //分享文件/目录所属用户id
    private Integer id;       //分享文件/目录id

    public SessionShareDto() {
    }

    public Integer getShareId() {
        return shareId;
    }

    public void setShareId(Integer shareId) {
        this.shareId = shareId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
