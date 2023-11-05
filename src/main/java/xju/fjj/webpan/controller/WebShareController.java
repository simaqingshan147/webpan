package xju.fjj.webpan.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xju.fjj.webpan.entity.constants.Constants;
import xju.fjj.webpan.entity.dto.SessionShareDto;
import xju.fjj.webpan.entity.dto.SessionUserDto;
import xju.fjj.webpan.entity.enums.FileStatusEnums;
import xju.fjj.webpan.entity.enums.ResultCodeEnum;
import xju.fjj.webpan.entity.query.FileInfoQuery;
import xju.fjj.webpan.entity.vo.FileShareVo;
import xju.fjj.webpan.entity.vo.ResponseVo;
import xju.fjj.webpan.entity.vo.ShareInfoVo;
import xju.fjj.webpan.exception.ServerException;
import xju.fjj.webpan.service.FileInfoService;
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
public class WebShareController extends BaseController{
    @Resource
    ShareInfoService shareInfoService;
    @Resource
    FileInfoService fileInfoService;

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
        FileInfoQuery query = new FileInfoQuery();
        query.setPageNo(pageNo);
        query.setPageSize(pageSize);
        query.setUserId(shareDto.getUserId());
        query.setPid(dirId);
        query.setStatus(FileStatusEnums.USING.getStatus());
        query.setOrderBy("update_time desc");
        return success(fileInfoService.findListByPage(query));
    }

    @PostMapping("/saveShare")
    public ResponseVo<?> saveShare(HttpSession session,
                                   @NotNull Integer shareId,
                                   @NotNull List<Map<String,Integer>> documents,
                                   @NotNull Integer dirId){
        SessionUserDto userDto = getUserInfoFromSession(session);
        SessionShareDto shareDto = getShareInfoFromSession(session, shareId);
        if(shareDto.getUserId().equals(userDto.getUserId()))
            throw new ServerException(ResultCodeEnum.CODE_600,"自己分享的文件不能重复保存");
        fileInfoService.saveShare(shareDto.getUserId(),documents,userDto.getUserId(),dirId);
        return success(null);
    }
}
