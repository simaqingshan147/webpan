package xju.fjj.webpan.entity.pojo;

import java.io.Serializable;
import java.util.Date;
/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: file_info表的pojo类
 * @date 2023/10/16 1:22
 */
public class FileInfo implements Serializable {
    /**
    * 文件id
    */
    private Integer fileId;
    /**
    * 用户id
    */
    private String userId;
    /**
    * 所属目录id
    */
    private Integer dirId;
    /**
    * 文件md5
    */
    private String fileMd5;
    /**
    * 文件名
    */
    private String fileName;
    /**
    * 文件路径
    */
    private String filePath;
    /**
    * 文件大小,单位字节
    */
    private Long fileSize;
    /**
    * 封面路径
    */
    private String fileCover;
    /**
     * 文件创建时间
     */
    private Date createTime;
    /**
    * 最后更新时间
    */
    private Date updateTime;
    /**
    * 文件类型
    */
    private Integer fileType;
    /**
    * 0:删除 1:回收站 2:正常
    */
    private Integer status;
    /**
    * 进入回收站时间
    */
    private Date recoveryTime;

    public FileInfo() {
    }

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getDirId() {
        return dirId;
    }

    public void setDirId(Integer dirId) {
        this.dirId = dirId;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileCover() {
        return fileCover;
    }

    public void setFileCover(String fileCover) {
        this.fileCover = fileCover;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getFileType() {
        return fileType;
    }

    public void setFileType(Integer fileType) {
        this.fileType = fileType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getRecoveryTime() {
        return recoveryTime;
    }

    public void setRecoveryTime(Date recoveryTime) {
        this.recoveryTime = recoveryTime;
    }
}
