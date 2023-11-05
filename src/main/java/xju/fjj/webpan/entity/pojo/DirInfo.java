package xju.fjj.webpan.entity.pojo;
import java.io.Serializable;
import java.util.Date;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: dir_info表的pojo类
 * @date 2023/10/22 17:22
 */
public class DirInfo implements Serializable {

    /**
    * 目录id
    */
    private Integer dirId;
    /**
    * 用户id
    */
    private String userId;
    /**
    * 目录的上层目录id
    */
    private Integer dirPid;
    /**
    * 目录名
    */
    private String dirName;
    /**
     * 目录创建时间
     */
    private Date createTime;
    /**
    * 目录最后更新时间
    */
    private Date updateTime;
    /**
    * 0:删除 1:回收站 2:正常
    */
    private Integer status;
    /**
    * 进入回收站时间
    */
    private Date recoveryTime;

    public DirInfo() {
    }

    public Integer getDirId() {
        return dirId;
    }

    public void setDirId(Integer dirId) {
        this.dirId = dirId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getDirPid() {
        return dirPid;
    }

    public void setDirPid(Integer dirPid) {
        this.dirPid = dirPid;
    }

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
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
