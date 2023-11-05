package xju.fjj.webpan.entity.dto;

import java.io.Serializable;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 返回文件上传消息的dto
 * @date 2023/10/25 20:16
 */
public class UploadResultDto implements Serializable {
    private Integer fileId;
    private String status;

    public UploadResultDto() {
    }

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
