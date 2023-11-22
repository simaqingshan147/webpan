package xju.fjj.webpan.service;

import org.springframework.web.multipart.MultipartFile;
import xju.fjj.webpan.entity.dto.DownloadFileDto;
import xju.fjj.webpan.entity.dto.UploadResultDto;
import xju.fjj.webpan.entity.pojo.DirInfo;
import xju.fjj.webpan.entity.pojo.FileInfo;
import xju.fjj.webpan.entity.query.FileInfoQuery;
import xju.fjj.webpan.entity.vo.Document;
import xju.fjj.webpan.entity.vo.PagedResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 文件处理的service
 * @date 2023/10/22 17:57
 */
public interface FileInfoService {
    PagedResult<List<Document>> findListByPage(FileInfoQuery query);

    PagedResult<List<Document>> getFileByPage(FileInfoQuery query);

    List<Document> getDir(FileInfoQuery query, List<Integer> excludeDirs);

    DirInfo getDirByDirId(Integer dirId);

    int getDirCount(FileInfoQuery query);

    DirInfo createFolder(Integer pid, String userId, String dirName);

    Document rename(Integer id, Integer pid, String newName, boolean isFolder);

    void moveFilesOrDirs(List<Integer> toMoveDirs, List<Integer> toMoveFiles, Integer dirId);

    FileInfo getFileByFileId(Integer fileId);

    void saveDownloadFileDto(String code, DownloadFileDto fileDto);

    DownloadFileDto getDownloadFileDto(String code);

    void changeFilesOrDirs(String userId,List<Integer> dirIds, List<Integer> fileIds,Integer status);

    UploadResultDto uploadFile(String userId, Integer fileId, MultipartFile file, String fileName, Integer dirId, String fileMd5, Integer chunkIndex, Integer chunks);

    File getFile(String userId, Integer fileId);

    File getVideo(String userId, String fileName);


    List<Document> findRecycleList(String userId);

    void saveShare(String userId, List<Map<String, Integer>> documents, String userId1, Integer dirId);

    List<FileInfo> selectFilesByIds(FileInfoQuery query, ArrayList<Integer> integers);

    void deleteFileBatch(List<Integer> fileIds);

    void removeExpireDirsAndFiles();

    void autoRemove(String userId);
}

