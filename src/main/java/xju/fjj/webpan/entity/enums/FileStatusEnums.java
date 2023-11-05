package xju.fjj.webpan.entity.enums;
/**
 * @description: 文件状态枚举类
 * @author 新疆大学 冯俊杰
 * @date 2023/10/22 18:15
 * @version 1.0
 */
public enum FileStatusEnums {
    TRANSFER(0,"转码中"),
    TRANSFER_FAIL(1,"转码失败"),
    USING(3,"使用中"),
    RECOVERY(4,"移入回收站"),
    DELETE(5,"已删除");
    private final Integer status;
    private final String description;

    FileStatusEnums(Integer status, String description) {
        this.status = status;
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }
}
