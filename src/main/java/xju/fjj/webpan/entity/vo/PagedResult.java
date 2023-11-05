package xju.fjj.webpan.entity.vo;

import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 分页数据和数据的封装类,没有Vo后缀是因为它不会直接返给前端,只是前端数据的一部分
 * @date 2023/10/15 0:01
 */
public class PagedResult<T> {
    private int totalCount;  //记录总数
    private int pageSize;    //每页的行数
    private int pageNo;      //当前页码
    private int pageTotal;   //页面总数
    private T list;    //数据,此时数据一定是多条数据

    public PagedResult() {
    }

    public PagedResult(int totalCount, int pageSize, int pageNo, int pageTotal,T list) {
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.pageNo = pageNo;
        this.pageTotal = pageTotal;
        this.list = list;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageTotal() {
        return pageTotal;
    }

    public void setPageTotal(int pageTotal) {
        this.pageTotal = pageTotal;
    }

    public T getList() {
        return list;
    }

    public void setList(T list) {
        this.list = list;
    }

    public static <T> PagedResult<List<T>> convert(PageInfo<T> pageInfo){
        PagedResult<List<T>> pagedResult = new PagedResult<>();
        //封装分页信息
        pagedResult.setPageNo(pageInfo.getPageNum());
        pagedResult.setPageSize(pageInfo.getPageSize());
        pagedResult.setTotalCount( (int) pageInfo.getTotal());
        pagedResult.setPageTotal(pageInfo.getPages());
        pagedResult.setList(pageInfo.getList());
        return pagedResult;
    }

    @Override
    public String toString() {
        return "PagedResult{" +
                "totalCount=" + totalCount +
                ", pageSize=" + pageSize +
                ", pageNo=" + pageNo +
                ", pageTotal=" + pageTotal +
                ", list=" + list +
                '}';
    }
}
