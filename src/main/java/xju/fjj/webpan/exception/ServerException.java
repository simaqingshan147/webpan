package xju.fjj.webpan.exception;

import xju.fjj.webpan.entity.enums.ResultCodeEnum;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 自定义异常的基类
 * @date 2023/10/15 18:12
 */
public class ServerException extends RuntimeException{
    private Integer code;   //异常状态码
    private String info;  //异常信息

    public ServerException(){
        super();
    }

    public ServerException(ResultCodeEnum codeEnum, String msg){
        super(msg);
        this.code = codeEnum.getCode();
        this.info = codeEnum.getMsg();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setMessage(String message) {
        this.info = message;
    }
}
