package xju.fjj.webpan.entity.query;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.hibernate.validator.constraints.Length;
import xju.fjj.webpan.entity.constants.Constants;

import java.util.Date;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: share_info表分页查询参数封装类
 * @date 2023/10/31 22:33
 */
public class ShareInfoQuery extends BaseQuery{
    @Length(min = 15,max = 15,message = "userId参数错误")
    private String userId;

    public ShareInfoQuery() {
        super();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
