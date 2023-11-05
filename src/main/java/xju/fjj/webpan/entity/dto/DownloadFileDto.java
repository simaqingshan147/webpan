package xju.fjj.webpan.entity.dto;

import java.io.Serializable;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: redis中保存待下载文件信息的dto
 * @date 2023/10/24 16:00
 */
public class DownloadFileDto implements Serializable {
    private Integer fileId;   //待下载文件id
    private String path;   //待下载文件保存路径
    private String fileName;  //待下载文件名

    public DownloadFileDto() {
    }

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
