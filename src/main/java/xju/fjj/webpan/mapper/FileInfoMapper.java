package xju.fjj.webpan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xju.fjj.webpan.entity.vo.Document;
import xju.fjj.webpan.entity.pojo.FileInfo;
import xju.fjj.webpan.entity.query.FileInfoQuery;

import java.util.List;

@Mapper
public interface FileInfoMapper{
    List<FileInfo> selectFilesByIds(@Param("query") FileInfoQuery query,
                                    @Param("fileIds") List<Integer> fileIds);
    List<Document> selectFileAndDirByPage(FileInfoQuery query);

    int selectFileCount(FileInfoQuery query);

    int updateFileInfo(FileInfo fileInfo);

    FileInfo selectFileByFileId(Integer fileId);

    void updateBatchDirIdByIds(@Param("fileIds") List<Integer> toMoveFiles,
                               @Param("dirId") Integer dirId);

    void updateBatchStatusByIds(@Param("fileIds") List<Integer> toRecycleFileIds,
                                @Param("status") Integer status);

    Long selectUseSpace(@Param("userId") String userId,
                        @Param("status") Integer status);

    FileInfo selectFileByMd5AndStatus(@Param("fileMd5") String fileMd5,
                                      @Param("status") Integer status);

    void insert(FileInfo fileInfo);

    void insertBatch(List<FileInfo> fileInfos);
}
