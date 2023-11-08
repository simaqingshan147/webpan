package xju.fjj.webpan.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xju.fjj.webpan.entity.constants.Constants;
import xju.fjj.webpan.entity.dto.SessionUserDto;
import xju.fjj.webpan.entity.dto.UploadResultDto;
import xju.fjj.webpan.entity.enums.FileCategoryEnums;
import xju.fjj.webpan.entity.enums.FileStatusEnums;
import xju.fjj.webpan.entity.enums.FileTypeEnums;
import xju.fjj.webpan.entity.enums.ResultCodeEnum;
import xju.fjj.webpan.entity.pojo.DirInfo;
import xju.fjj.webpan.entity.query.FileInfoQuery;
import xju.fjj.webpan.entity.vo.Document;
import xju.fjj.webpan.entity.vo.PagedResult;
import xju.fjj.webpan.entity.vo.ResponseVo;
import xju.fjj.webpan.exception.ServerException;

import java.util.List;
import java.util.Map;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 文件处理控制器,处理首页文件信息
 * @date 2023/10/22 17:35
 */
@RestController("fileInfoController")
@RequestMapping("/file")
public class FileInfoController extends BaseFileController{

    /*获取首页的文件和目录列表*/
    @PostMapping("/loadDataList")
    public ResponseVo<?> loadDataList(@Validated FileInfoQuery query,
                                      String category){
        query.setOrderBy("update_time desc");
        //如果没有指定目录,则按主目录查起
        if(query.getPid() == null)
            query.setPid(Constants.MAIN_FOLDER_ID);
        //如果指定文件类型,则按类型查询
        FileCategoryEnums fileCategory = FileCategoryEnums.getByCategory(category);
        if(fileCategory != null)
            query.setType(FileTypeEnums.getTypesByCategory(fileCategory));
        //查询正常使用的文件或目录
        query.setStatus(FileStatusEnums.USING.getStatus());
        //查询首页下的文件和目录
        query.setPid(Constants.MAIN_FOLDER_ID);
        PagedResult<List<Document>> result = fileInfoService.findListByPage(query);
        return success(result);
    }

    /*文件分片上传*/
    @PostMapping("/uploadFile")
    public ResponseVo<UploadResultDto> uploadFile(HttpSession session,
                                    MultipartFile file,
                                    Integer fileId,
                                    @NotBlank String fileName,
                                    @NotNull @Min(Constants.MAIN_FOLDER_ID) Integer dirId,
                                    @NotBlank String fileMd5,
                                    @NotNull Integer chunkIndex,
                                    @NotNull Integer chunks){
        String userId = getUserInfoFromSession(session).getUserId();
        UploadResultDto uploadResultDto = fileInfoService.uploadFile(userId,fileId,file,fileName,dirId,fileMd5,chunkIndex,chunks);
        return success(uploadResultDto);
    }

    /*获取文件封面*/
    @GetMapping("/getImage/{imageFolder}/{imageName}")
    public void getImage(HttpServletResponse response,
                         @PathVariable(value = "imageFolder",required = false) String imageFolder,
                         @PathVariable(value = "imageName",required = false) String imageName,
                         @RequestParam(value = "isFolder",defaultValue = "false")Boolean isFolder){
        super.getImage(response,imageFolder,imageName,isFolder);
    }

    /*获取一长串目录下的最末目录的id,也就是获取当前所属目录id*/
    @GetMapping("/getFolderInfo")
    public ResponseVo<Map<String, Object>> getFolderInfo(@NotBlank String path){
        return super.getFolderInfo(path);
    }

    /*预览获取视频索引或切片文件*/
    @GetMapping("/getVideoInfo/{fileId}")
    public void getVideoInfo(HttpServletResponse response,
                        HttpSession session,
                        @PathVariable("fileId") @NotBlank String fileId){
        SessionUserDto userDto = getUserInfoFromSession(session);
        super.getVideoInfo(response, userDto.getUserId(), fileId);
    }

    /*预览获取文件*/
    @GetMapping("/getFile/{fileId}")
    public void getFile(HttpServletResponse response,
                        HttpSession session,
                        @PathVariable("fileId") @NotNull Integer fileId){
        SessionUserDto userDto = getUserInfoFromSession(session);
        super.getFile(response,userDto.getUserId(),fileId);
    }

    /*重命名文件或目录*/
    @PostMapping("/rename")
    public ResponseVo<Document> rename(@NotNull Integer id,
                                       @NotNull Integer pid,
                                       @NotBlank String newName,
                                       boolean isFolder){
        Document result = fileInfoService.rename(id,pid,newName,isFolder);
        if(result == null)
            throw new ServerException(ResultCodeEnum.CODE_600,"该文件不存在");
        return success(result);
    }

    /*将(多个)文件或目录移入回收站*/
    @PostMapping("/recycle")
    public ResponseVo<?> recycle(HttpSession session,
                                 @NotNull List<Integer> dirIds,
                                 @NotNull List<Integer> fileIds){
        String userId = getUserInfoFromSession(session).getUserId();
        fileInfoService.changeFilesOrDirs(userId,dirIds,fileIds,FileStatusEnums.RECOVERY.getStatus());
        return success(null);
    }

    /*新建文件夹*/
    @PostMapping("/newFolder")
    public ResponseVo<Document> createFolder(HttpSession session,
                                             @NotNull Integer pid,
                                             @NotBlank String dirName){
        String userId = getUserInfoFromSession(session).getUserId();
        //检查同级目录下有没有同名文件夹
        FileInfoQuery query = new FileInfoQuery();
        query.setName(dirName);
        query.setPid(pid);
        query.setUserId(userId);
        query.setStatus(FileStatusEnums.USING.getStatus());
        if(fileInfoService.getDirCount(query) != 0)
            throw new ServerException(ResultCodeEnum.CODE_601,"存在同名文件夹");

        DirInfo dirInfo = fileInfoService.createFolder(pid,userId,dirName);
        return success(new Document(dirInfo));
    }

    /*
        获取 除了dir_id为pid的和选中要移动的 之外的所有目录,实现移动功能
        pid为当前文件目录,如果不排除,相当于不移动
        checkedDirIds为选中(要移动的)目录的dir_id
    * */
    @PostMapping("/getAllFolder")
    public ResponseVo<?> getAllFolder(@NotNull @Min(Constants.MAIN_FOLDER_ID) Integer pid,
                                      List<Integer> checkedDirIds){
        FileInfoQuery query = new FileInfoQuery();
        query.setStatus(FileStatusEnums.USING.getStatus());
        //当前文件在主目录下，则不添加pid到排除目录,因为主目录不在表中
        if(!pid.equals(Constants.MAIN_FOLDER_ID))
            checkedDirIds.add(pid);
        return success(fileInfoService.getDir(query,checkedDirIds));
    }

    /*正常用户创建文件下载链接*/
    @PostMapping("/createDownloadUrl/{fileId}")
    public ResponseVo<String> createDownloadUrl(HttpSession session,
                                           @PathVariable("fileId")Integer fileId){
        String userId = getUserInfoFromSession(session).getUserId();
        return super.createDownLoadUrl(fileId,userId,false);
    }

    /*修改(多个)文件或目录的所属目录,即移动(多个)文件或目录*/
    @PostMapping("changeFolder")
    public ResponseVo<?> changeFolder(@NotNull List<Integer> dirIds,
                                      @NotNull List<Integer> fileIds,
                                      @NotBlank Integer dirId){
        fileInfoService.moveFilesOrDirs(dirIds,fileIds,dirId);
        return success(null);
    }
}
