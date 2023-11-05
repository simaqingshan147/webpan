package xju.fjj.webpan.entity.dto;

import java.io.Serializable;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: redis中保存用户空间信息的dto
 * @date 2023/10/18 22:07
 */
public class UserSpaceDto implements Serializable{
    private Long useSpace;
    private Long totalSpace;

    public UserSpaceDto() {
    }

    public Long getUseSpace() {
        return useSpace;
    }

    public void setUseSpace(Long useSpace) {
        this.useSpace = useSpace;
    }

    public Long getTotalSpace() {
        return totalSpace;
    }

    public void setTotalSpace(Long totalSpace) {
        this.totalSpace = totalSpace;
    }
}
