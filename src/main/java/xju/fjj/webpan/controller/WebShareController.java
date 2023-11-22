package xju.fjj.webpan.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import xju.fjj.webpan.entity.constants.Constants;
import xju.fjj.webpan.entity.dto.SessionShareDto;
import xju.fjj.webpan.entity.dto.SessionUserDto;
import xju.fjj.webpan.entity.enums.FileStatusEnums;
import xju.fjj.webpan.entity.enums.ResultCodeEnum;
import xju.fjj.webpan.entity.query.FileInfoQuery;
import xju.fjj.webpan.entity.vo.ResponseVo;
import xju.fjj.webpan.entity.vo.ShareInfoVo;
import xju.fjj.webpan.exception.ServerException;
import xju.fjj.webpan.service.ShareInfoService;

import java.util.List;
import java.util.Map;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 分享文件浏览相关的controller
 * @date 2023/10/31 21:19
 */
@RestController
@RequestMapping("/webShare")
public class WebShareController extends BaseFileController{
    @Resource
    ShareInfoService shareInfoService;

    /*检验分享验证码是否正确,生成分享文件session*/
    @PostMapping("checkShareCode")
    public ResponseVo<?> checkShareCode(HttpSession session,
                                        @NotNull Integer shareId,
                                        @NotBlank String code){
        SessionShareDto shareDto = shareInfoService.checkShareCode(shareId,code);
        session.setAttribute(Constants.SESSION_SHARE_KEY_PREFIX+shareId,shareDto);
        return success(null);
    }

    /*获取分享人的用户信息和分享文件信息*/
    @PostMapping("/getShareLoginInfo")
    public ResponseVo<?> getShareLoginInfo(HttpSession session,
                                           @NotNull Integer shareId){
        SessionShareDto shareDto = getShareInfoFromSession(session, shareId);
        SessionUserDto userDto = getUserInfoFromSession(session);
        //没有对应分享文件session(未校验)
        if(shareDto == null)
            return success(null);
        //获取分享文件信息
        ShareInfoVo shareInfoVo = shareInfoService.getShareInfoVoFromId(shareId);
        //判断是否是当前用户分享的文件
        shareInfoVo.setCurrentUser(userDto != null && userDto.getUserId().equals(shareDto.getUserId()));
        return success(shareInfoVo);
    }

    /*分页显示分享文件夹下文件*/
    @PostMapping("/loadFileList")
    public ResponseVo<?> loadFileList(HttpSession session,
                                      @NotNull Integer pageNo,
                                      @NotNull Integer pageSize,
                                      @NotNull Integer shareId,
                                      @NotNull Integer dirId){
        SessionShareDto shareDto = getShareInfoFromSession(session,shareId);
        //没有对应分享文件session(未校验)
        if(shareDto == null)
            throw new ServerException(ResultCodeEnum.CODE_404,"分享码不正确");
        FileInfoQuery query = new FileInfoQuery();
        query.setPageNo(pageNo);
        query.setPageSize(pageSize);
        query.setUserId(shareDto.getUserId());
        query.setPid(dirId);
        query.setStatus(FileStatusEnums.USING.getStatus());
        query.setOrderBy("update_time desc");
        return success(fileInfoService.findListByPage(query));
    }

    /*获取一长串目录下的最末目录的id,也就是获取当前所属目录id*/
    @GetMapping("/getFolderInfo")
    public ResponseVo<Map<String, Object>> getFolderInfo(@NotBlank String path){
        return super.getFolderInfo(path);
    }

    /** 登录用户保存到网盘*/
    @PostMapping("/saveShare")
    @Transactional(rollbackFor = Exception.class)
    public ResponseVo<?> saveShare(HttpSession session,
                                   @NotNull Integer shareId,
                                   @NotNull List<Map<String,Integer>> documents,
                                   @NotNull Integer dirId){
        SessionUserDto userDto = getUserInfoFromSession(session);
        SessionShareDto shareDto = getShareInfoFromSession(session, shareId);
        //没有对应分享文件session(未校验)
        if(shareDto == null)
            throw new ServerException(ResultCodeEnum.CODE_404,"分享码不正确");
        if(shareDto.getUserId().equals(userDto.getUserId()))
            throw new ServerException(ResultCodeEnum.CODE_600,"自己分享的文件不能重复保存");
        fileInfoService.saveShare(shareDto.getUserId(),documents,userDto.getUserId(),dirId);
        return success(null);
    }

    /*预览获取视频索引或切片文件*/
    @GetMapping("/getVideoInfo/{shareId}/{fileId}")
    public void getVideoInfo(HttpServletResponse response,
                             HttpSession session,
                             @PathVariable("shareId") @NotNull Integer shareId,
                             @PathVariable("fileId") @NotBlank String fileId){
        SessionShareDto shareDto = getShareInfoFromSession(session, shareId);
        //没有对应分享文件session(未校验)
        if(shareDto == null)
            throw new ServerException(ResultCodeEnum.CODE_404,"分享码不正确");
        super.getVideoInfo(response, shareDto.getUserId(), fileId);
    }

    /*预览获取文件*/
    @GetMapping("/getFile/{shareId}/{fileId}")
    public void getFile(HttpServletResponse response,
                        HttpSession session,
                        @PathVariable("shareId") @NotNull Integer shareId,
                        @PathVariable("fileId") @NotNull Integer fileId){
        SessionShareDto shareDto = getShareInfoFromSession(session, shareId);
        //没有对应分享文件session(未校验)
        if(shareDto == null)
            throw new ServerException(ResultCodeEnum.CODE_404,"分享码不正确");
        super.getFile(response,shareDto.getUserId(),fileId);
    }

    /*正常用户创建文件下载链接*/
    @PostMapping("/createDownloadUrl/{fileId}")
    public ResponseVo<String> createDownloadUrl(HttpSession session,
                                                @NotNull Integer shareId,
                                                @PathVariable("fileId")Integer fileId){
        SessionShareDto shareDto = getShareInfoFromSession(session, shareId);
        //没有对应分享文件session(未校验)
        if(shareDto == null)
            throw new ServerException(ResultCodeEnum.CODE_404,"分享码不正确");
        return super.createDownLoadUrl(fileId, shareDto.getUserId(), false);
    }
}
