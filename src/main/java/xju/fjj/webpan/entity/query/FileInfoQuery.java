package xju.fjj.webpan.entity.query;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 文件分页查询参数封装类
 * @date 2023/10/22 17:41
 */
public class FileInfoQuery extends BaseQuery{
    /*
    private Integer id;   //文件或目录id
    不应该有主键，因为凡是用到query类的都不是精准查询，而是可能返回多个结果的不定条件查询或者模糊查询
    */
    @Length(min = 15,max = 15,message = "userId参数错误")
    private String userId;   //文件或目录的用户id
    @NotNull(message = "pid参数不能为Null")
    @Min(value = 0,message = "pid参数错误")
    private Integer pid;  //查询文件或目录的所属目录id
    @Length(max = 50,message = "name参数错误")
    private String name;  //查询文件或目录名
    private List<Integer> type;  //查询文件或目录的类型
    private Integer status;  //查询文件或目录的状态
    public FileInfoQuery() {
        super();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public List<Integer> getType() {
        return type;
    }

    public void setType(List<Integer> type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
