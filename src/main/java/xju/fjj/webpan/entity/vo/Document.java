package xju.fjj.webpan.entity.vo;

import xju.fjj.webpan.entity.constants.Constants;
import xju.fjj.webpan.entity.pojo.DirInfo;
import xju.fjj.webpan.entity.pojo.FileInfo;

import java.util.Date;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 文件和目录类的共同信息,返回给前端的文件信息类
 * @date 2023/10/22 16:01
 */
public class Document{
    private Integer id;      //文件或目录id
    private Integer pid;    //文件或目录的上层目录id
    private String name;    //文件或目录名
    private Date updateTime;  //文件或目录更新时间
    private String cover;     //文件或目录的封面
    private boolean isFolder;  //是否为目录
    private Integer type;     //文件或目录类型
    private Integer status;   //文件或目录状态

    public Document(){

    }

    public Document(FileInfo fileInfo){
        this.id = fileInfo.getFileId();
        this.pid = fileInfo.getDirId();
        this.name = fileInfo.getFileName();
        this.updateTime = fileInfo.getUpdateTime();
        this.cover = fileInfo.getFileCover();
        this.isFolder = false;
        this.type = fileInfo.getFileType();
        this.status = fileInfo.getStatus();
    }

    public Document(DirInfo dirInfo){
        this.id = dirInfo.getDirId();
        this.pid = dirInfo.getDirPid();
        this.name = dirInfo.getDirName();
        this.updateTime = dirInfo.getUpdateTime();
        this.cover = Constants.DEFAULT_COVER_FOLDER+Constants.FOLDER_COVER_NAME;
        //this.type = dirInfo.getFileType();
        this.isFolder = true;
        this.type = null;
        this.status = dirInfo.getStatus();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setFolder(boolean folder) {
        isFolder = folder;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
