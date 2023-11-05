package xju.fjj.webpan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xju.fjj.webpan.entity.pojo.ShareInfo;
import xju.fjj.webpan.entity.query.ShareInfoQuery;

import java.util.List;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: share_info表mapper，处理分享信息
 * @date 2023/10/31 22:01
 */
@Mapper
public interface ShareInfoMapper {

    /*根据分页条件查询*/
    List<ShareInfo> selectShareInfoByPage(ShareInfoQuery query);

    void insert(ShareInfo shareInfo);

    void update(ShareInfo shareInfo);

    int deleteShareInfoBatch(@Param("userId") String userId,
                             @Param("shareIds") List<Integer> shareIds);

    ShareInfo selectShareInfoById(Integer shareId);
}
