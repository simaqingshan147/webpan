package xju.fjj.webpan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xju.fjj.webpan.entity.pojo.DirInfo;
import xju.fjj.webpan.entity.query.FileInfoQuery;

import java.util.Date;
import java.util.List;

@Mapper
public interface DirInfoMapper {

    int insertDir(DirInfo dirInfo);

    List<DirInfo> selectDirs(@Param("query") FileInfoQuery query,
                             @Param("excludeDirs")List<Integer> excludeDirs);

    List<DirInfo> selectDirsByIds(@Param("query") FileInfoQuery query,
                                  @Param("dirIds") List<Integer> dirIds);
    int selectDirCount(FileInfoQuery query);

    DirInfo selectDirByDirId(Integer dirId);

    int updateDirInfo(DirInfo dirInfo);

    int updateBatchDirPidByIds(@Param("dirIds") List<Integer> toMoveDirs,
                               @Param("dirPid") Integer dirPid);

    void updateBatchStatusByIds(@Param("dirIds") List<Integer> toRecycleDirIds,
                                @Param("status") Integer status);

    List<Integer> selectExpireDirs(@Param("lastValidTime") Date lastValidTime);

    void deleteBatchDirs(List<Integer> delDirIds);
}
