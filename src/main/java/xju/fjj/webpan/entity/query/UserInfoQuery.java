package xju.fjj.webpan.entity.query;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.hibernate.validator.constraints.Length;

/**
 * @description: 用户消息分页查询参数封装类
 * @author 新疆大学 冯俊杰
 * @date 2023.10.15
 * @version 1.0
 */
public class UserInfoQuery extends BaseQuery{
    /*
        下面不包括UserInfo的所有属性
        虽然所有属性都有可能修改，但不是所有属性都会通过前端传参来修改
    */
    @Length(min = 14,max = 16)
    private String userId;
    @Length(max = 21)
    private String nickName;
    @Min(0)
    @Max(1)
    private Integer status;

    public UserInfoQuery(){
        super();
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickNameFuzzy(String nickNameFuzzy) {
        this.nickName = nickNameFuzzy;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }


    public void setNickName(String nickName) {
        this.nickName = nickName;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
