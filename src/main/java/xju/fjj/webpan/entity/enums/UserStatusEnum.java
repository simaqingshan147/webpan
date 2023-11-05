package xju.fjj.webpan.entity.enums;
/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 用户状态码和状态信息的枚举
 * @date 2023/10/15 18:30
 */
public enum UserStatusEnum {
    DISABLE(0,"禁用"),
    ENABLE(1,"启用");

    private final Integer status;
    private final String  description;

    UserStatusEnum(Integer status, String description) {
        this.status = status;
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    /*通过状态获取对应的枚举*/
    public static UserStatusEnum getByStatus(Integer status){
        for(UserStatusEnum item : UserStatusEnum.values()){
            if(item.getStatus().equals(status))
                return item;
        }
        return null;
    }
}
