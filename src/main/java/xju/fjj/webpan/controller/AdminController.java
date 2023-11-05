package xju.fjj.webpan.controller;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xju.fjj.webpan.entity.pojo.UserInfo;
import xju.fjj.webpan.entity.query.UserInfoQuery;
import xju.fjj.webpan.entity.vo.ResponseVo;
import xju.fjj.webpan.service.UserInfoService;

@RestController("userInfoController")
@RequestMapping("/admin")
public class AdminController extends BaseController{
    @Resource
    UserInfoService userInfoService;

    /*
    通过昵称和状态分页查询用户
      变量名 是否必填 类型 描述
      pageNo 是 int 页码
      pageSize 否 int 分页大小
      nickName 否 String 昵称(模糊搜索)
      status 否 String 状态
    */
    @PostMapping("/loadUserList")
    public ResponseVo<?> loadUserList(@Validated UserInfoQuery query){
        return success(userInfoService.findListByPage(query));
    }

    /*
    通过用户id修改用户状态
      变量名 是否必填 类型 描述
      userId 是 String 用户id
      status 是 int 状态
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
      变量名 是否必填 类型 描述
      userId 是 String 用户id
      useSpace 是 int 已使用空间
    */
    @PostMapping("/updateUseSpace")
    @Transactional(rollbackFor = {Exception.class})
    public ResponseVo<?> updateUseSpace(@NotBlank String userId,
                                        @NotBlank Long useSpace){
        UserInfo updateUserInfo = new UserInfo();
        updateUserInfo.setUserId(userId);
        updateUserInfo.setUseSpace(useSpace);
        userInfoService.updateUserInfo(updateUserInfo);
        return success(null);
    }
}
