package xju.fjj.webpan.entity.query;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 分页查询信息的基类
 * @date 2023/10/22 17:38
 */
public class BaseQuery {
    @NotNull
    @Min(0)
    private Integer pageNo;
    @NotNull
    @Min(1)
    private Integer pageSize;

    private String orderBy;

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}
