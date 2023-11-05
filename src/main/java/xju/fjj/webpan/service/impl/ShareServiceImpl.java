package xju.fjj.webpan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import xju.fjj.webpan.entity.dto.SessionShareDto;
import xju.fjj.webpan.entity.enums.ResultCodeEnum;
import xju.fjj.webpan.entity.pojo.DirInfo;
import xju.fjj.webpan.entity.pojo.FileInfo;
import xju.fjj.webpan.entity.pojo.ShareInfo;
import xju.fjj.webpan.entity.pojo.UserInfo;
import xju.fjj.webpan.entity.query.FileInfoQuery;
import xju.fjj.webpan.entity.query.ShareInfoQuery;
import xju.fjj.webpan.entity.vo.FileShareVo;
import xju.fjj.webpan.entity.vo.PagedResult;
import xju.fjj.webpan.entity.vo.ShareInfoVo;
import xju.fjj.webpan.exception.ServerException;
import xju.fjj.webpan.mapper.DirInfoMapper;
import xju.fjj.webpan.mapper.FileInfoMapper;
import xju.fjj.webpan.mapper.ShareInfoMapper;
import xju.fjj.webpan.mapper.UserInfoMapper;
import xju.fjj.webpan.service.ShareInfoService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 分享服务相关
 * @date 2023/10/31 22:08
 */
@Service
public class ShareServiceImpl implements ShareInfoService {
    @Resource
    FileInfoMapper fileInfoMapper;
    @Resource
    DirInfoMapper dirInfoMapper;
    @Resource
    ShareInfoMapper shareInfoMapper;
    @Resource
    UserInfoMapper userInfoMapper;

    /*分页查询分享文件的文件信息和分享信息*/
    @Override
    public PagedResult<List<FileShareVo>> findListByPage(ShareInfoQuery query) {
        PageHelper.startPage(query.getPageNo(),query.getPageSize());
        List<ShareInfo> shareInfos = shareInfoMapper.selectShareInfoByPage(query);
        if(shareInfos != null && !shareInfos.isEmpty()){
            PageInfo<ShareInfo> pageInfo = new PageInfo<>(shareInfos);
            List<Integer> fileIds = new ArrayList<>();
            List<ShareInfo> files = new ArrayList<>();
            List<Integer> dirIds = new ArrayList<>();
            List<ShareInfo> dirs = new ArrayList<>();
            List<FileShareVo> data = new ArrayList<>();
            //收集共享文件或文件夹id
            for(ShareInfo shareInfo : shareInfos){
                if(shareInfo.getIsFolder() == 1){
                    dirIds.add(shareInfo.getDocumentId());
                    dirs.add(shareInfo);
                }
                else{
                    fileIds.add(shareInfo.getDocumentId());
                    files.add(shareInfo);
                }
            }
            //查询文件信息,匹配并封装结果
            FileInfoQuery fileInfoQuery = new FileInfoQuery();
            fileInfoQuery.setUserId(query.getUserId());
            if(!fileIds.isEmpty()) {
                List<FileInfo> fileInfos = fileInfoMapper.selectFilesByIds(fileInfoQuery, fileIds);
                if(fileInfos != null && !fileInfos.isEmpty()){
                    for(FileInfo fileInfo:fileInfos) {
                        Optional<ShareInfo> first = files.stream().filter(file -> file.getDocumentId().equals(fileInfo.getFileId())).findFirst();
                        FileShareVo fileShareVo = FileShareVo.convertFromFileInfo(first.get(), fileInfo);
                        data.add(fileShareVo);
                    }
                }
            }
            //查询目录信息,匹配并封装结果
            if(!dirIds.isEmpty()){
                List<DirInfo> dirInfos = dirInfoMapper.selectDirsByIds(fileInfoQuery, dirIds);
                if(dirInfos != null && !dirInfos.isEmpty()){
                    for(DirInfo dirInfo:dirInfos){
                        Optional<ShareInfo> first = dirs.stream().filter(dir -> dir.getDocumentId().equals(dirInfo.getDirId())).findFirst();
                        FileShareVo fileShareVo = FileShareVo.convertFromDirInfo(first.get(), dirInfo);
                        data.add(fileShareVo);
                    }
                }
            }
            //封装分页对象
            PagedResult<List<FileShareVo>> result = new PagedResult<>();
            result.setList(data);
            result.setPageNo(pageInfo.getPageNum());
            result.setPageSize(pageInfo.getPageSize());
            result.setPageTotal(pageInfo.getPages());
            result.setTotalCount((int) pageInfo.getTotal());
            return result;
        }
        return null;
    }

    /*修改或新增分享信息*/
    @Override
    public void saveShareInfo(ShareInfo shareInfo) {
        if(shareInfo.getShareId() == null)
            shareInfoMapper.insert(shareInfo);
        else{
            shareInfoMapper.update(shareInfo);
        }
    }

    /*批量删除*/
    @Override
    public void deleteBatch(String userId, List<Integer> shareIds) {
        int count = shareInfoMapper.deleteShareInfoBatch(userId,shareIds);
        if(shareIds.size() != count)
            throw new ServerException(ResultCodeEnum.CODE_600,"删除分享失败");
    }

    /*通过shareId获取分享文件信息*/
    @Override
    public FileShareVo getFileShareVoById(Integer shareId) {
        ShareInfo shareInfo = shareInfoMapper.selectShareInfoById(shareId);
        if(shareInfo == null)
            return null;
        //文件
        if(shareInfo.getIsFolder() == 0){
            FileInfo fileInfo = fileInfoMapper.selectFileByFileId(shareInfo.getDocumentId());
            return FileShareVo.convertFromFileInfo(shareInfo,fileInfo);
        } else if (shareInfo.getIsFolder() == 1) {
            DirInfo dirInfo = dirInfoMapper.selectDirByDirId(shareInfo.getDocumentId());
            return FileShareVo.convertFromDirInfo(shareInfo,dirInfo);
        }
        return null;
    }

    /*校验分享码,生成分享文件session*/
    @Override
    public SessionShareDto checkShareCode(Integer shareId, String code) {
        ShareInfo shareInfo = shareInfoMapper.selectShareInfoById(shareId);
        if(shareInfo == null)
            throw new ServerException(ResultCodeEnum.CODE_600,"分享文件不存在");
        else if (!shareInfo.getCode().equals(code))
            throw new ServerException(ResultCodeEnum.CODE_600,"分享码不正确");
        //更新浏览次数
        shareInfo.setShowCount(shareInfo.getShowCount()+1);
        shareInfoMapper.update(shareInfo);

        SessionShareDto shareDto = new SessionShareDto();
        shareDto.setShareId(shareId);
        shareDto.setUserId(shareInfo.getUserId());
        shareDto.setId(shareInfo.getDocumentId());
        return shareDto;
    }

    /*通过shareId获取分享文件和所属用户信息*/
    @Override
    public ShareInfoVo getShareInfoVoFromId(Integer shareId) {
        //获取分享文件信息
        FileShareVo fileShareVo = getFileShareVoById(shareId);
        Date now = new Date();
        //分享文件不存在或者已过期
        if(fileShareVo == null)
            throw new ServerException(ResultCodeEnum.CODE_404,"分享文件不存在");
        else if (fileShareVo.getExpireTime() != null && now.after(fileShareVo.getExpireTime()))
            throw new ServerException(ResultCodeEnum.CODE_404,"分享文件已过期");
        //获取用户信息
        UserInfo userInfo = userInfoMapper.selectByUserId(fileShareVo.getUserId());
        return ShareInfoVo.covert(userInfo,fileShareVo);
    }
}
