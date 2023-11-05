package xju.fjj.webpan.entity.enums;

import java.util.Date;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 有效期状态码枚举
 * @date 2023/10/31 22:09
 */
public enum ValidTypeEnums {
    DAY_1(0, 1, "1天"),
    DAY_7(1, 7, "7天"),
    DAY_30(2, 30, "30天"),
    FOREVER(3, Integer.MAX_VALUE, "永久有效");

    private Integer type;
    private Integer days;
    private String description;

    ValidTypeEnums(Integer type, Integer days, String description) {
        this.type = type;
        this.days = days;
        this.description = description;
    }

    public Integer getType() {
        return type;
    }

    public Integer getDays() {
        return days;
    }

    public static Integer getDaysByType(Integer type) {
        for(ValidTypeEnums item : ValidTypeEnums.values()){
            if(item.getType().equals(type))
                return item.getDays();
        }
        return 0;
    }

}
