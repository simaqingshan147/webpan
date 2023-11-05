package xju.fjj.webpan.entity.enums;
/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 文件类型枚举类
 * @date 2023/10/22 17:57
 */
public enum FileCategoryEnums {
    IMAGE(1,"image","图片"),
    DOC(2,"doc","文档"),
    VIDEO(3,"video","视频"),
    MUSIC(4,"music","音频"),
    OTHER(5,"other","其他");
    private final Integer code;
    private final String category;

    FileCategoryEnums(Integer code,String category, String description) {
        this.category = category;
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public String getCategory() {
        return category;
    }

    public static FileCategoryEnums getByCategory(String category){
        for (FileCategoryEnums item : FileCategoryEnums.values()){
            if(item.getCategory().equals(category))
                return item;
        }
        return null;
    }
}
