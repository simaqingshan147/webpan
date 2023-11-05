package xju.fjj.webpan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import xju.fjj.webpan.component.RedisComponent;
import xju.fjj.webpan.config.AppConfig;
import xju.fjj.webpan.entity.constants.Constants;
import xju.fjj.webpan.entity.dto.DownloadFileDto;
import xju.fjj.webpan.entity.dto.UploadResultDto;
import xju.fjj.webpan.entity.dto.UserSpaceDto;
import xju.fjj.webpan.entity.enums.FileStatusEnums;
import xju.fjj.webpan.entity.enums.FileTypeEnums;
import xju.fjj.webpan.entity.enums.ResultCodeEnum;
import xju.fjj.webpan.entity.enums.UploadStatusEnums;
import xju.fjj.webpan.entity.pojo.DirInfo;
import xju.fjj.webpan.entity.pojo.FileInfo;
import xju.fjj.webpan.entity.query.FileInfoQuery;
import xju.fjj.webpan.entity.vo.Document;
import xju.fjj.webpan.entity.vo.PagedResult;
import xju.fjj.webpan.exception.ServerException;
import xju.fjj.webpan.mapper.DirInfoMapper;
import xju.fjj.webpan.mapper.FileInfoMapper;
import xju.fjj.webpan.service.FileInfoService;
import xju.fjj.webpan.utils.FfmpegUtils;
import xju.fjj.webpan.utils.StringTools;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 文件管理service的实现类
 * @date 2023/10/22 18:38
 */
@Service("fileInfoService")
public class FileInfoServiceImpl implements FileInfoService {
    @Resource
    AppConfig appConfig;
    @Resource
    FileInfoMapper fileInfoMapper;
    @Resource
    DirInfoMapper dirInfoMapper;
    @Resource
    RedisComponent redisComponent;
    @Resource
    @Lazy
    FileInfoServiceImpl fileInfoService;

    /*分页查询文件和目录*/
    @Override
    public PagedResult<List<Document>> findListByPage(FileInfoQuery query){
        //如果指定了文件类型，就只查询文件
        if(query.getType() != null)
            return getFileByPage(query);
        //否则按条件查询文件和目录
        PageHelper.startPage(query.getPageNo(),query.getPageSize());
        List<Document> documents = fileInfoMapper.selectFileAndDirByPage(query);
        PageInfo<Document> pageInfo = new PageInfo<>(documents);
        //校正目录信息的返回格式
        for(Document document : pageInfo.getList()){
            if(document.getCover().isEmpty()){
                document.setCover(Constants.DEFAULT_COVER_FOLDER+Constants.FOLDER_COVER_NAME);
                document.setType(null);
            }
        }
        return PagedResult.convert(pageInfo);
    }


    /*根据条件查询(名称模糊)目录信息*/
    @Override
    public List<Document> getDir(FileInfoQuery query,List<Integer> excludeDirs) {
        List<DirInfo> dirInfos = dirInfoMapper.selectDirs(query,excludeDirs);
        if(dirInfos == null)
            return null;
        List<Document> results = new ArrayList<>();
        for(DirInfo dirInfo : dirInfos)
            results.add(new Document(dirInfo));
        return results;
    }

    /*根据目录id精准查询*/
    @Override
    public DirInfo getDirByDirId(Integer dirId) {
        return dirInfoMapper.selectDirByDirId(dirId);
    }

    /*根据条件精准查询符合条件的目录个数*/
    @Override
    public int getDirCount(FileInfoQuery query) {
        return dirInfoMapper.selectDirCount(query);
    }

    @Override
    public DirInfo createFolder(Integer pid, String userId, String dirName){
        Date now = new Date();
        DirInfo dirInfo = new DirInfo();
        dirInfo.setDirName(dirName);
        dirInfo.setUserId(userId);
        dirInfo.setStatus(FileStatusEnums.USING.getStatus());
        dirInfo.setCreateTime(now);
        dirInfo.setUpdateTime(now);
        int i = dirInfoMapper.insertDir(dirInfo);
        if(i == 1 && dirInfo.getDirId() != null)
            return dirInfo;
        return null;
    }

    @Override
    public Document rename(Integer id, Integer pid, String newName, boolean isFolder) {
        //查询新名称是否被占用
        FileInfoQuery query = new FileInfoQuery();
        query.setPid(pid);
        query.setName(newName);
        int i = isFolder ? dirInfoMapper.selectDirCount(query) : fileInfoMapper.selectFileCount(query);
        if(i != 0)
            throw new ServerException(ResultCodeEnum.CODE_601,"文件名已存在");
        //根据id更新目录或文件的名称
        if (isFolder){
            DirInfo dirInfo = new DirInfo();
            dirInfo.setDirId(id);
            dirInfo.setDirName(newName);
            dirInfo.setUpdateTime(new Date());
            i = dirInfoMapper.updateDirInfo(dirInfo);
            //更新失败因为该目录id不存在,返回null
            if(i == 0)
                return null;
            else
                return new Document(dirInfoMapper.selectDirByDirId(id));
        }else{
            FileInfo fileInfo = new FileInfo();
            fileInfo.setFileId(id);
            fileInfo.setFileName(newName);
            fileInfo.setUpdateTime(new Date());
            i = fileInfoMapper.updateFileInfo(fileInfo);
            if(i == 0)
                return null;
            else
                return new Document(fileInfoMapper.selectFileByFileId(id));
        }
    }

    /*移动多个文件或目录到所属目录*/
    @Override
    public void moveFilesOrDirs(List<Integer> toMoveDirs,List<Integer> toMoveFiles,  Integer dirId){
        if(!toMoveDirs.isEmpty()){
            //如果待移除目录中包含目标目录，则移出
            toMoveDirs.remove(dirId);
            dirInfoMapper.updateBatchDirPidByIds(toMoveDirs,dirId);
        }
        if(!toMoveFiles.isEmpty()){
            fileInfoMapper.updateBatchDirIdByIds(toMoveFiles,dirId);
        }
    }

    /*根据文件id获取文件信息*/
    @Override
    public FileInfo getFileByFileId(Integer fileId) {
        return fileInfoMapper.selectFileByFileId(fileId);
    }


    /*分页查询文件*/
    private PagedResult<List<Document>> getFileByPage(FileInfoQuery query){
        PageHelper.startPage(query.getPageNo(),query.getPageSize());
        List<FileInfo> fileInfos = fileInfoMapper.selectFilesByIds(query,null);
        PageInfo<FileInfo> pageInfo = new PageInfo<>(fileInfos);
        List<Document> documents = new ArrayList<>();
        for(FileInfo fileInfo : fileInfos)
            documents.add(new Document(fileInfo));
        PagedResult<List<Document>> pagedResult = new PagedResult<>();
        pagedResult.setList(documents);
        //封装分页信息
        pagedResult.setPageNo(pageInfo.getPageNum());
        pagedResult.setPageSize(pageInfo.getPageSize());
        pagedResult.setTotalCount( (int) pageInfo.getTotal());
        pagedResult.setPageTotal(pageInfo.getPages());
        return pagedResult;
    }
    //保存待下载文件信息到redis
    @Override
    public void saveDownloadFileDto(String code, DownloadFileDto fileDto) {
        redisComponent.saveDownloadFileDto(code,fileDto);
    }

    //通过code(key)从redis获取待下载文件信息
    @Override
    public DownloadFileDto getDownloadFileDto(String code) {
        return redisComponent.getDownloadFileDto(code);
    }

    /*从上到下遍历整颗文档树,遇到删除文件夹剪枝,遇到正常文件夹继续搜索*/
    @Override
    public List<Document> findRecycleList(String userId) {
        List<Document> resultDirs = new ArrayList<>();
        List<Document> resultFiles = new ArrayList<>();
        List<Integer> searchDirs = new ArrayList<>();
        searchDirs.add(Constants.MAIN_FOLDER_ID);
        FileInfoQuery query = new FileInfoQuery();
        query.setUserId(userId);
        for(Integer dirId:searchDirs){
            query.setPid(dirId);
            //搜索当前目录下所有回收站文件
            query.setStatus(FileStatusEnums.RECOVERY.getStatus());
            query.setOrderBy("recovery_time desc");
            List<FileInfo> files = fileInfoMapper.selectFilesByIds(query, null);
            if(files != null && !files.isEmpty()){
                for(FileInfo fileInfo : files)
                    resultFiles.add(new Document(fileInfo));
            }
            //搜索当前目录下所有状态目录
            query.setStatus(null);
            query.setOrderBy("update_time desc");
            List<DirInfo> dirs = dirInfoMapper.selectDirsByIds(query, null);
            if(dirs  != null && !dirs.isEmpty()){
                for(DirInfo dirInfo : dirs){
                    //如果是回收站中目录,添加到结果
                    if(dirInfo.getStatus().equals(FileStatusEnums.RECOVERY.getStatus()))
                        resultDirs.add(new Document(dirInfo));
                    //如果是正常目录,继续往下搜索
                    else if (dirInfo.getStatus().equals(FileStatusEnums.USING.getStatus()))
                        searchDirs.add(dirInfo.getDirId());
                }
            }
        }
        //搜索完毕,合并返回
        resultDirs.addAll(resultFiles);
        return resultDirs;
    }

    /*将文件夹和文件删除或恢复*/
    @Override
    public void changeFilesOrDirs(String userId,List<Integer> dirIds, List<Integer> fileIds,Integer status) {
        FileInfoQuery query = new FileInfoQuery();
        query.setUserId(userId);
        query.setStatus(status);
        if(!dirIds.isEmpty()){
            //层次遍历,查找所有需要的文件和目录
            for (Integer dirId : dirIds) {
                //获取文件夹下的所有文件夹,尾插实现层次遍历
                query.setPid(dirId);
                List<DirInfo> dirInfos = dirInfoMapper.selectDirsByIds(query, null);
                if (dirInfos != null && !dirInfos.isEmpty()) {
                    for (DirInfo dirInfo : dirInfos)
                        dirIds.add(dirInfo.getDirId());
                }
                List<FileInfo> fileInfos = fileInfoMapper.selectFilesByIds(query, null);
                if (fileInfos != null && !fileInfos.isEmpty()) {
                    for (FileInfo fileInfo : fileInfos)
                        fileIds.add(fileInfo.getFileId());
                }
            }
            //目录和其子目录已经遍历完毕,更新
            dirInfoMapper.updateBatchStatusByIds(dirIds,status);
        }
        //目录和子目录下文件已经遍历(添加)完成,更新至回收站
        if(!fileIds.isEmpty()){
            fileInfoMapper.updateBatchStatusByIds(fileIds,status);
        }
        //更新用户空间
        Long newSpace = fileInfoMapper.selectUseSpace(userId,FileStatusEnums.USING.getStatus());
        UserSpaceDto useSpaceDto = redisComponent.getUseSpaceDto(userId);
        useSpaceDto.setUseSpace(newSpace);
        redisComponent.saveUserSpaceDto(userId,useSpaceDto);
    }

    /*将其他用户的(多个)文件/目录保存到自己的目录下*/
    @Override
    public void saveShare(String fromUserId, List<Map<String, Integer>> documents, String toUserId, Integer pid) {
        if(documents.isEmpty())
            return;
        List<Integer> fileIds = new ArrayList<>();
        List<Integer> dirIds = new ArrayList<>();
        Date now = new Date();
        //将选中分类
        for(Map<String, Integer> document:documents){
            if(document.get("isFolder").equals(0))
                fileIds.add(document.get("id"));
            else if (document.get("isFolder").equals(1))
                dirIds.add(document.get("id"));
        }
        FileInfoQuery query = new FileInfoQuery();
        query.setUserId(fromUserId);
        query.setStatus(FileStatusEnums.USING.getStatus());
        if(!fileIds.isEmpty()){
            List<FileInfo> fileInfos = fileInfoMapper.selectFilesByIds(query, fileIds);
            for(FileInfo fileInfo:fileInfos){
                fileInfo.setFileId(null);
                fileInfo.setDirId(pid);
                fileInfo.setFileMd5(null);
                fileInfo.setUpdateTime(now);
                fileInfo.setFileName(autoName(fromUserId,pid,fileInfo.getFileName()));
            }
            fileInfoMapper.insertBatch(fileInfos);
        }
        if(!dirIds.isEmpty()){
            for (Integer dirId : dirIds){
                DirInfo dirInfo = dirInfoMapper.selectDirByDirId(dirId);
                move(dirInfo,pid,toUserId,query,now);
            }
        }
    }

    /*递归移动dirInfo和dirInfo下目录,文件到toUserId用户下*/
    private void move(DirInfo dirInfo,Integer pid,String toUserId,FileInfoQuery query,Date now){
        query.setPid(dirInfo.getDirId());
        //目录下文件
        List<FileInfo> subFiles = fileInfoMapper.selectFilesByIds(query, null);
        //目录下目录
        List<DirInfo> subDirs = dirInfoMapper.selectDirs(query, null);
        //插入目录,获取新dirId
        dirInfo.setDirPid(pid);
        dirInfo.setUserId(toUserId);
        dirInfo.setDirName(autoName(toUserId,pid, dirInfo.getDirName()));
        dirInfo.setUpdateTime(now);
        dirInfoMapper.insertDir(dirInfo);
        //dirInfo移动后的dirId即是新的pid
        if(dirInfo.getDirId() != null){
            //插入目录下文件
            if(subFiles != null && !subFiles.isEmpty()){
                for(FileInfo subFile:subFiles){
                    subFile.setFileId(null);
                    //维持目录结构
                    subFile.setDirId(dirInfo.getDirId());
                    subFile.setFileMd5(null);
                    subFile.setUpdateTime(now);
                    subFile.setFileName(autoName(query.getUserId(),pid,subFile.getFileName()));
                }
                fileInfoMapper.insertBatch(subFiles);
            }
            if(subDirs != null && !subDirs.isEmpty()){
                for(DirInfo subDir:subDirs)
                    move(subDir,dirInfo.getDirId(),toUserId,query,now);
            }
        }
    }

    @Override
    public UploadResultDto uploadFile(String userId, Integer fileId, MultipartFile file, String fileName, Integer dirId, String fileMd5, Integer chunkIndex, Integer chunks) {
        File temp;
        Date now = new Date();
        UserSpaceDto spaceDto = redisComponent.getUseSpaceDto(userId);
        try{
            UploadResultDto result = new UploadResultDto();
            //第一片上传，判断是否可以秒传
            if(chunkIndex.equals(0)){
                FileInfo dbFile = fileInfoMapper.selectFileByMd5AndStatus(fileMd5,FileStatusEnums.USING.getStatus());
                //文件已存在(哪怕是其他用户的文件),执行秒传
                if(dbFile != null){
                    //文件继续上传会超出容量,注意重复文件也算容量
                    //此时之前上传的文件大小为0
                    if(dbFile.getFileSize()+spaceDto.getUseSpace()>spaceDto.getTotalSpace())
                        throw new ServerException(ResultCodeEnum.CODE_500,"容量不足");

                    //插入重复文件消息,无需上传
                    //dbFile.setFileId(fileId)   第一片上传没有fileId
                    dbFile.setUserId(userId);  //userId可能不同,因为md5相同的可能是其他用户的文件
                    dbFile.setDirId(dirId);
                    dbFile.setFileMd5(null);   //md5必须唯一
                    dbFile.setUpdateTime(now);
                    dbFile.setStatus(FileStatusEnums.USING.getStatus());  //状态正常
                    dbFile.setFileName(autoName(userId,dirId,fileName));  //重命名同名文件
                    fileInfoMapper.updateFileInfo(dbFile);

                    //更新redis中的用户空间信息
                    spaceDto.setUseSpace(spaceDto.getUseSpace()+ dbFile.getFileSize());
                    redisComponent.removeUserSpaceDto(userId);
                    redisComponent.saveUserSpaceDto(userId,spaceDto);

                    //秒传完成,返回秒传标志,完成传输
                    result.setFileId(dbFile.getFileId());  //后续fileId设为db生成的自增id
                    result.setStatus(UploadStatusEnums.UPLOAD_SECONDS.getCode());  //上传为秒传
                    return result;
                }
            }
            //非秒传

            String tempFolder = appConfig.getProjectFolder()+Constants.FILE_FOLDER_TEMP;
            String currentFolder = userId + fileMd5;
            temp = new File(tempFolder+currentFolder);
            if(!temp.exists())
                temp.mkdirs();
            //获取之前上传的文件的大小
            Long tempSize = redisComponent.getFileTempSize(userId,fileMd5);
            //文件继续上传会超出容量
            if(file.getSize()+tempSize+spaceDto.getUseSpace()>spaceDto.getTotalSpace())
                throw new ServerException(ResultCodeEnum.CODE_500,"容量不足");
            //保存文件到临时目录
            File newFile = new File(temp.getPath()+"/"+chunkIndex);
            file.transferTo(newFile);
            //更新文件大小
            redisComponent.updateFileTempSize(userId,fileMd5,file.getSize());

            //不是最后一片,返回继续上传标志
            if(chunkIndex < chunks-1){
                result.setFileId(fileId);  //非秒传时,在完成上传前都是null
                result.setStatus(UploadStatusEnums.UPLOADING.getCode());
                return  result;
            }

            //最后一片上传完成,记录数据库,异步合并分片

            //按月份保存文件
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            String month = sdf.format(now);
            //文件后缀
            String fileSuffix = StringTools.getFileSuffix(fileName);
            //真实文件名
            String realFileName = currentFolder + fileSuffix;
            //文件总大小
            Long totalFileSize = redisComponent.getFileTempSize(userId,fileMd5);
            //插入文件信息
            FileInfo fileInfo = new FileInfo();
            fileInfo.setUserId(userId);
            fileInfo.setFileMd5(fileMd5);
            fileInfo.setFileName(autoName(userId,dirId,fileName));  //避免重命名
            fileInfo.setFilePath(month+"/"+realFileName);   // yyyy-MM/userId+fileMd5.xxx
            fileInfo.setDirId(dirId);
            fileInfo.setCreateTime(now);
            fileInfo.setUpdateTime(now);
            fileInfo.setFileSize(totalFileSize);
            fileInfo.setFileType(FileTypeEnums.getTypeBySuffix(fileSuffix).getType());
            fileInfo.setStatus(FileStatusEnums.TRANSFER.getStatus());  //传输状态,待合并分片
            fileInfoMapper.insert(fileInfo);
            //获取db返回的自增id
            result.setFileId(fileInfo.getFileId());
            //更新用户空间信息
            spaceDto.setUseSpace(spaceDto.getUseSpace()+totalFileSize);
            redisComponent.saveUserSpaceDto(userId,spaceDto);
            //事务提交后调用异步方法合并传输文件片,创建封面
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    fileInfoService.transferFile(userId,fileInfo.getFileId(),fileMd5);
                }
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /*异步方法合并传输文件片,并从临时文件夹移出*/
    @Async
    public void transferFile(String userId, Integer fileId, String fileMd5) {
        FileInfo fileInfo = fileInfoMapper.selectFileByFileId(fileId);
        String cover = null;
        File target = null;
        boolean transferSuccess = true;
        try {
            //如果文件信息不存在或不符合
            if(fileInfo == null || !fileInfo.getStatus().equals(FileStatusEnums.TRANSFER.getStatus()))
                return;

            //temp文件夹路径 temp/
            String tempFolder = appConfig.getProjectFolder()+Constants.FILE_FOLDER_TEMP;
            //分片文件存储路径 temp/userId+fileMd5/
            String currentFolder = userId + fileMd5;
            File temp = new File(tempFolder+currentFolder);
            if(!temp.exists())
                temp.mkdirs();

            //文件后缀
            String suffix = StringTools.getFileSuffix(fileInfo.getFileName());
            //按月份保存文件
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            String month = sdf.format(fileInfo.getCreateTime());
                //真实文件保存路径 file/yyyy-MM/
            String targetFolder = appConfig.getProjectFolder()+Constants.FILE_FOLDER_FILE + month;
            target = new File(targetFolder);
            if(!target.exists())
                target.mkdirs();
                //真实文件名 userId+fileMd5.xxx
            String targetFileName = currentFolder + suffix;
                //真实文件路径 file/yyyy-MM/userId+fileMd5.xxx
            String targetFilePath = target.getPath()+"/"+targetFileName;
            //合并分片文件,移动到目标文件路径
            FfmpegUtils.union(temp,targetFilePath,fileInfo.getFileName());

            //如果是视频文件
            if(fileInfo.getFileType().equals(FileTypeEnums.VIDEO.getType())){
                //分割为.ts文件
                FfmpegUtils.cutFile4Video(fileId,targetFilePath);
                //生成视频缩略图(封面)
                    //cover文件夹下封面文件的相对路径
                cover = month+"/"+currentFolder+Constants.COVER_SUFFIX;
                    //封面文件
                File coverFile = new File(appConfig.getProjectFolder()+Constants.FILE_FOLDER_COVER+cover);
                FfmpegUtils.createCover4Video(target,Constants.LENGTH_150,coverFile);
            //如果是图片文件
            }else if (fileInfo.getFileType().equals(FileTypeEnums.IMAGE.getType())){
                //生成图片缩略图(封面)
                    //cover文件夹下封面文件的相对路径
                cover = month+"/"+targetFileName.replace(".","_.");
                File coverFile = new File(appConfig.getProjectFolder()+Constants.FILE_FOLDER_COVER+cover);
                Boolean created = FfmpegUtils.createThumbForImage(target,Constants.LENGTH_150,coverFile);
                //创建缩略图失败,直接使用原图作为封面
                if(!created)
                    FileUtils.copyFile(target,coverFile);
            }
        }catch (Exception e){
            transferSuccess = false;
            e.printStackTrace();
        }finally {
            //更新之前未插入的信息
            FileInfo updateInfo = new FileInfo();
            updateInfo.setFileId(fileId);
            if (target != null)
                updateInfo.setFileSize(target.getTotalSpace());
            updateInfo.setFileCover(cover);
            updateInfo.setStatus(transferSuccess ? FileStatusEnums.USING.getStatus() : FileStatusEnums.TRANSFER_FAIL.getStatus());
            fileInfoMapper.updateFileInfo(updateInfo);
        }
    }
    /*根据文件id获取预览文件或视频索引*/
    @Override
    public File getFile(String userId,Integer fileId) {
        FileInfo fileInfo = fileInfoMapper.selectFileByFileId(fileId);
        String targetPath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE;
        //文件不存在或被移入回收站
        if(fileInfo == null || !fileInfo.getStatus().equals(FileStatusEnums.USING.getStatus()))
            return null;
        //返回视频索引
        String filePath = fileInfo.getFilePath();
        if(fileInfo.getFileType().equals(FileTypeEnums.VIDEO.getType()))
            //filePath: yyyy-MM/userId+fileMd5.xxx
            //targetPath: yyyy-MM/userId+fileMD5/index.m3u8
            targetPath += filePath.substring(0,filePath.indexOf(".")-1) + "/" + Constants.M3U8_NAME;
        //返回文件
        //filePath: yyyy-MM/userId+fileMd5.xxx
        //targetPath: yyyy-MM/userId+fileMd5.xxx
        targetPath += filePath;
        return new File(targetPath);
    }

    /*根据ts文件名(fileId_xxxx.ts获取ts文件)*/
    @Override
    public File getVideo(String userId, String fileName) {
        String fileId = fileName.substring(0,fileName.indexOf("_")-1);
        FileInfo fileInfo = fileInfoMapper.selectFileByFileId(Integer.parseInt(fileId));
        //文件不存在或被移入回收站
        if(fileInfo == null || !fileInfo.getStatus().equals(FileStatusEnums.USING.getStatus()))
            return null;
        String filePath = fileInfo.getFilePath();
        String targetPath = appConfig.getProjectFolder()+Constants.FILE_FOLDER_FILE;
        targetPath += filePath.substring(0,filePath.indexOf(".")-1) + "/" + fileName;
        return new File(targetPath);
    }


    /*重命名同名文件*/
    private String autoName(String userId,Integer dirId,String fileName){
        FileInfoQuery query = new FileInfoQuery();
        query.setUserId(userId);
        query.setPid(dirId);
        query.setStatus(FileStatusEnums.USING.getStatus());
        //TODO 只检查正常文件,则可能与回收站文件同名,恢复文件同样要重命名
        //同目录下存在同名文件,返回重命名
        if(fileInfoMapper.selectFileCount(query)>0){
            String randomNumber = StringTools.getRandomNumber(Constants.LENGTH_5);
            int index = fileName.indexOf(".");
            if(index == -1)
                fileName += randomNumber;
            fileName = fileName.substring(0,index-1)+"_"+randomNumber+fileName.substring(index);
        }
        return  fileName;
    }
}
