package xju.fjj.webpan.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import xju.fjj.webpan.config.AppConfig;
import xju.fjj.webpan.entity.constants.Constants;
import xju.fjj.webpan.entity.dto.DownloadFileDto;
import xju.fjj.webpan.entity.enums.FileStatusEnums;
import xju.fjj.webpan.entity.enums.ResultCodeEnum;
import xju.fjj.webpan.entity.pojo.DirInfo;
import xju.fjj.webpan.entity.pojo.FileInfo;
import xju.fjj.webpan.entity.query.FileInfoQuery;
import xju.fjj.webpan.entity.vo.ResponseVo;
import xju.fjj.webpan.exception.ServerException;
import xju.fjj.webpan.service.FileInfoService;
import xju.fjj.webpan.utils.StringTools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 多个controller共用的文件处理方法的集合
 * @date 2023/11/8 23:48
 */
public class BaseFileController extends BaseController{
    @Resource
    FileInfoService fileInfoService;
    @Resource
    AppConfig appConfig;

    /*获取一长串目录下的最末目录的id,也就是获取当前所属目录id*/
    public ResponseVo<Map<String, Object>> getFolderInfo(String path){
        String[] dirs = path.split("/");
        //最末目录的id,因为目录id是unique的，所以无需递归查询
        String dir = dirs[dirs.length - 1];
        DirInfo dirInfo = fileInfoService.getDirByDirId(Integer.parseInt(dir));
        if(dirInfo == null)
            throw new ServerException(ResultCodeEnum.CODE_600,"目录参数错误");
        Map<String,Object> idAndName = new HashMap<>();
        idAndName.put("dirId",dirInfo.getDirId());
        idAndName.put("dirName",dirInfo.getDirName());
        return success(idAndName);
    }

    /*检测下载权限,创建下载链接并返回*/
    public ResponseVo<String> createDownLoadUrl(Integer fileId,
                                                String userId,
                                                Boolean isAdmin){
        FileInfoQuery query = new FileInfoQuery();
        FileInfo fileInfo;
        if(!isAdmin){
            query.setUserId(userId);
            query.setStatus(FileStatusEnums.USING.getStatus());
            List<FileInfo> list = fileInfoService.selectFilesByIds(query, new ArrayList<>(fileId));
            if(list == null || list.isEmpty())
                throw  new ServerException(ResultCodeEnum.CODE_404,"文件不存在");
            fileInfo = list.get(0);
        }
        //如果为管理员,则都可以下载
        fileInfo = fileInfoService.getFileByFileId(fileId);

        //生成随机下载码作为key，设定时限存入redis,防止随便下载
        String code = StringTools.getRandomNumber(Constants.LENGTH_10);
        DownloadFileDto fileDto = new DownloadFileDto();
        fileDto.setFileId(fileInfo.getFileId());
        fileDto.setFileName(fileInfo.getFileName());
        fileDto.setPath(fileInfo.getFilePath());
        fileInfoService.saveDownloadFileDto(code,fileDto);
        //返回code即可
        return success(code);
    }

    /*获取文件封面*/
    public void getImage(HttpServletResponse response,
                         String imageFolder,
                         String imageName,
                         Boolean isFolder){
        //指定文件封面但是未指定封面路径
        if(!isFolder && (imageFolder == null|| imageName == null)){
            imageFolder = Constants.DEFAULT_COVER_FOLDER;
            imageName = Constants.FILE_COVER_NAME;
            //指定为文件夹
        }else if(isFolder){
            imageFolder = Constants.DEFAULT_COVER_FOLDER;
            imageName = Constants.FOLDER_COVER_NAME;
        }
        String imageSuffix = StringTools.getFileSuffix(imageName);
        String filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_COVER + imageFolder + "/" + imageName;
        response.setContentType("image/"+imageSuffix);
        response.setHeader("Cache-Control","max-age=2592000");
        File image = new File(filePath);
        readFile(response,image);
    }

    /*预览获取文件*/
    public void getFile(HttpServletResponse response,
                        String userId,
                        Integer fileId){
        File result = fileInfoService.getFile(userId, fileId);
        if(result == null || !result.exists())
            throw new ServerException(ResultCodeEnum.CODE_404,"视频文件不存在");
        readFile(response,result);
    }

    /*预览获取视频索引或切片文件*/
    public void getVideoInfo(HttpServletResponse response,
                             String userId,
                             String fileId){
        File result;
        //fileId是fileId_xxxx.ts，则返回对应ts文件
        if(fileId.endsWith("ts"))
            result = fileInfoService.getVideo(userId,fileId);
            //fileId是fileId，则返回视频的m3u8索引
        else
            result = fileInfoService.getFile(userId,Integer.getInteger(fileId));
        if(result == null || !result.exists())
            throw new ServerException(ResultCodeEnum.CODE_404,"视频文件不存在");
        readFile(response,result);
    }
}
