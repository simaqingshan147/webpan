package xju.fjj.webpan.entity.pojo;

import java.io.Serializable;
import java.util.Date;

/**
* &#064;TableName  share_info
 */
public class ShareInfo implements Serializable {

    /**
    * 分享id
    */
    private Integer shareId;
    /**
     * 文件或目录所属用户id
     */
    private String userId;
    /**
    * 文件或目录id
    */
    private Integer documentId;
    /**
    * 0:文件 1:目录
    */
    private Integer isFolder;
    /**
    * 0:1天 1:3天 2:7天 3:30天 4:永久有效
    */
    private Integer validType;
    /**
    * 分享开始时间
    */
    private Date shareTime;
    /**
    * 验证码
    */
    private String code;
    /**
    * 分享文件/目录浏览次数
    */
    private Integer showCount;

    public ShareInfo() {
    }

    /**
    * 分享id
    */
    public void setShareId(Integer shareId){
    this.shareId = shareId;
    }

    /**
    * 文件或目录id
    */
    public void setDocumentId(Integer documentId){
    this.documentId = documentId;
    }

    /**
    * 0:文件 1:目录
    */
    public void setIsFolder(Integer isFolder) {
        this.isFolder = isFolder;
    }

    /**
    * 0:1天 1:3天 2:7天 3:30天 4:永久有效
    */
    public void setValidType(Integer validType){
    this.validType = validType;
    }

    /**
    * 分享开始时间
    */
    public void setShareTime(Date shareTime){
    this.shareTime = shareTime;
    }

    /**
    * 验证码
    */
    public void setCode(String code){
    this.code = code;
    }

    /**
    * 分享文件/目录浏览次数
    */
    public void setShowCount(Integer showCount){
    this.showCount = showCount;
    }


    /**
    * 分享id
    */
    public Integer getShareId(){
    return this.shareId;
    }

    /**
    * 文件或目录id
    */
    public Integer getDocumentId(){
    return this.documentId;
    }

    /**
    * 0:文件 1:目录
    */
    public Integer getIsFolder() {
        return isFolder;
    }

    /**
    * 0:1天 1:3天 2:7天 3:30天 4:永久有效
    */
    public Integer getValidType(){
    return this.validType;
    }

    /**
    * 分享开始时间
    */
    public Date getShareTime(){
    return this.shareTime;
    }

    /**
    * 验证码
    */
    public String getCode(){
    return this.code;
    }

    /**
    * 分享文件/目录浏览次数
    */
    public Integer getShowCount(){
    return this.showCount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
