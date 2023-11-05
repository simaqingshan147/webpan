package xju.fjj.webpan.entity.enums;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 返回状态码和返回信息的枚举
 * @date 2023/10/15 18:30
 */
public enum ResultCodeEnum {
    CODE_200(200, "请求成功"),
    CODE_404(404, "请求地址不存在"),
    CODE_500(500, "服务器返回错误，请联系管理员"),
    CODE_600(600, "请求参数错误"),
    CODE_601(601, "信息已经存在");

    private final Integer code;
    private final String msg;

    ResultCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
