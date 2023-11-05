package xju.fjj.webpan.entity.vo;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 前端数据的封装类
 * @date 2023/10/15 2:35
 */
public class ResponseVo<T> {
    private String status;  //请求状态
    private int code;      //请求吗
    private String info;   //请求信息
    private T data;

    public ResponseVo() {
    }

    public ResponseVo(String status, int code, String info, T data) {
        this.status = status;
        this.code = code;
        this.info = info;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
