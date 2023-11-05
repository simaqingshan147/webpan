package xju.fjj.webpan.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xju.fjj.webpan.entity.constants.Constants;
import xju.fjj.webpan.entity.enums.ResultCodeEnum;
import xju.fjj.webpan.entity.pojo.ShareInfo;
import xju.fjj.webpan.entity.query.ShareInfoQuery;
import xju.fjj.webpan.entity.vo.FileShareVo;
import xju.fjj.webpan.entity.vo.PagedResult;
import xju.fjj.webpan.entity.vo.ResponseVo;
import xju.fjj.webpan.exception.ServerException;
import xju.fjj.webpan.service.ShareInfoService;
import xju.fjj.webpan.utils.StringTools;

import java.util.Date;
import java.util.List;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 用户实现和管理文件分享的controller
 * @date 2023/10/31 22:21
 */
@RestController
@RequestMapping("/share")
public class FileShareController extends BaseController{
    @Resource
    ShareInfoService shareInfoService;

    /*分页查询分享文件或目录*/
    @PostMapping("/loadShareList")
    public ResponseVo<?> loadShareList(HttpSession session,
                                       @Validated ShareInfoQuery query){
        String userId = getUserInfoFromSession(session).getUserId();
        query.setOrderBy("share_time desc");
        query.setUserId(userId);
        PagedResult<List<FileShareVo>> result = shareInfoService.findListByPage(query);
       return success(result);
    }

    /*分享文件*/
    @PostMapping("/shareFile")
    public ResponseVo<?> shareFile(HttpSession session,
                                   @NotNull Integer id,
                                   @NotNull @Size(max = 1) Integer isFolder,
                                   Integer validType,
                                   String code){
        String userId = getUserInfoFromSession(session).getUserId();
        ShareInfo shareInfo = new ShareInfo();
        if(code == null||code.length() != Constants.LENGTH_5)
            code = StringTools.getRandomNumber(Constants.LENGTH_5);
        shareInfo.setDocumentId(id);
        shareInfo.setIsFolder(isFolder);
        shareInfo.setValidType(validType);
        shareInfo.setUserId(userId);
        shareInfo.setShareTime(new Date());
        shareInfo.setShowCount(0);
        shareInfo.setCode(code);
        shareInfoService.saveShareInfo(shareInfo);
        //自增id未返回
        if (shareInfo.getShareId() == null)
            throw new ServerException(ResultCodeEnum.CODE_500,"分享信息保存失败");
        return success(shareInfo);
    }

    /*(批量)取消分享*/
    @PostMapping("/cancelShare")
    public ResponseVo<?> cancelShare(HttpSession session,
                                     @NotNull List<Integer> shareIds){
        String userId = getUserInfoFromSession(session).getUserId();
        if(!shareIds.isEmpty())
            shareInfoService.deleteBatch(userId,shareIds);
        return success(null);
    }
}
