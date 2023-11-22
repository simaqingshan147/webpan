package xju.fjj.webpan.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xju.fjj.webpan.entity.constants.Constants;
import xju.fjj.webpan.entity.dto.SessionUserDto;
import xju.fjj.webpan.entity.enums.FileStatusEnums;
import xju.fjj.webpan.entity.pojo.UserInfo;
import xju.fjj.webpan.entity.query.FileInfoQuery;
import xju.fjj.webpan.entity.query.UserInfoQuery;
import xju.fjj.webpan.entity.vo.ResponseVo;
import xju.fjj.webpan.service.UserInfoService;

import java.util.List;

@RestController("userInfoController")
@RequestMapping("/admin")
public class AdminController extends BaseFileController{
    @Resource
    UserInfoService userInfoService;

    /*
    通过昵称和状态分页查询用户
    */
    @PostMapping("/loadUserList")
    public ResponseVo<?> loadUserList(@Validated UserInfoQuery query){
        return success(userInfoService.findListByPage(query));
    }

    /*
    通过用户id修改用户状态
    */
    @PostMapping("/updateStatus")
    @Transactional(rollbackFor = {Exception.class})
    public ResponseVo<?> updateStatus(@NotBlank String userId,
                                      @NotBlank Integer status){
        UserInfo updateUserInfo = new UserInfo();
        updateUserInfo.setUserId(userId);
        updateUserInfo.setStatus(status);
        userInfoService.updateUserInfo(updateUserInfo);
        return success(null);
    }

    /*
    通过用户id修改用户空间
    */
    @PostMapping("/updateUseSpace")
    @Transactional(rollbackFor = {Exception.class})
    public ResponseVo<?> updateUseSpace(@NotBlank String userId,
                                        @NotBlank Long totalSpace){
        totalSpace = totalSpace * Constants.MB;
        userInfoService.updateUserSpace(userId,totalSpace);
        return success(null);
    }

    /*获取所有文件*/
    @PostMapping("loadFileList")
    public ResponseVo<?> loadFileList(@NotNull @Min(1) Integer pageNo,
                                      @NotNull @Min(1) Integer pageSize,
                                      String fileNameFuzzy){
        FileInfoQuery query = new FileInfoQuery();
        query.setPageNo(pageNo);
        query.setPageSize(pageSize);
        query.setName(fileNameFuzzy);
        query.setStatus(FileStatusEnums.USING.getStatus());
        query.setOrderBy("update_time desc");
        return success(fileInfoService.getFileByPage(query));
    }

    /*获取文件封面*/
    @GetMapping("/getImage/{imageFolder}/{imageName}")
    public void getImage(HttpServletResponse response,
                         @PathVariable(value = "imageFolder",required = false) String imageFolder,
                         @PathVariable(value = "imageName",required = false) String imageName,
                         @RequestParam(value = "isFolder",defaultValue = "false")Boolean isFolder){
        super.getImage(response,imageFolder,imageName,isFolder);
    }

    /*预览获取视频索引或切片文件*/
    @PostMapping("/getVideoInfo/{fileId}")
    public void getVideoInfo(HttpServletResponse response,
                             HttpSession session,
                             @PathVariable("fileId") @NotBlank String fileId){
        SessionUserDto userDto = getUserInfoFromSession(session);
        super.getVideoInfo(response, userDto.getUserId(), fileId);
    }

    /*预览获取文件*/
    @PostMapping("/getFile/{fileId}")
    public void getFile(HttpServletResponse response,
                        HttpSession session,
                        @PathVariable("fileId") @NotNull Integer fileId){
        SessionUserDto userDto = getUserInfoFromSession(session);
        super.getFile(response,userDto.getUserId(),fileId);
    }

    /*正常用户创建文件下载链接*/
    @PostMapping("/createDownloadUrl/{fileId}")
    public ResponseVo<String> createDownloadUrl(HttpSession session,
                                                @PathVariable("fileId")Integer fileId){
        String userId = getUserInfoFromSession(session).getUserId();
        return super.createDownLoadUrl(fileId,userId,true);
    }

    @PostMapping("/delFile")
    @Transactional(rollbackFor = Exception.class)
    public ResponseVo<?> delFile(List<Integer> fileIds){
        fileInfoService.deleteFileBatch(fileIds);
        return success(null);
    }
}
