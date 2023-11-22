package xju.fjj.webpan.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xju.fjj.webpan.entity.constants.Constants;
import xju.fjj.webpan.entity.dto.SessionUserDto;
import xju.fjj.webpan.entity.dto.UserSpaceDto;
import xju.fjj.webpan.entity.enums.ResultCodeEnum;
import xju.fjj.webpan.entity.vo.ResponseVo;
import xju.fjj.webpan.exception.ServerException;
import xju.fjj.webpan.service.UserInfoService;

import java.io.File;
import java.io.IOException;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 用户服务controller
 * @date 2023/10/16 1:22
 */
@RestController("accountController")
@RequestMapping("/user")
public class AccountController extends BaseFileController{
    @Resource
    UserInfoService userInfoService;

    @PostMapping("/register")
    @Transactional(rollbackFor = {Exception.class})
    public ResponseVo<?> register(HttpSession session,
                                  @NotBlank @Email String email,
                                  @NotBlank String checkCode,
                                  @NotBlank @Length(max = 20) String nickName,
                                  @NotBlank String password,
                                  @NotBlank String emailCode){
        if(!checkCheckCode(session,checkCode))
            throw new ServerException(ResultCodeEnum.CODE_600,"图像验证码不正确");
        //验证邮箱验证码,完成注册
        userInfoService.register(email,nickName,password,emailCode);
        return success(null);
    }

    @PostMapping("/login")
    @Transactional(rollbackFor = {Exception.class})
    public ResponseVo<?> login(HttpSession session,
                               @NotBlank @Email String email,
                               @NotBlank String checkCode,
                               @NotBlank String password){
        if(!checkCheckCode(session,checkCode))
            throw new ServerException(ResultCodeEnum.CODE_600,"图像验证码不正确");
        //登录,通过session存储用户信息
        SessionUserDto sessionUserDto = userInfoService.login(email,password);
        session.setAttribute(Constants.SESSION_KEY,sessionUserDto);
        return success(null);
    }

    /*获取用户头像*/
    @GetMapping("/getAvatar/{userId}")
    public void getAvatar(@PathVariable("userId") @NotBlank String userId,
                          HttpServletResponse response) {
        File avatarFile = userInfoService.getAvatarFile(userId);
        if (avatarFile == null)
            throw new ServerException(ResultCodeEnum.CODE_500, "头像文件不存在");
        //上传文件
        response.setContentType("image/jpg");
        readFile(response, avatarFile);
    }

    @PostMapping("/logout")
    public ResponseVo<?> logout(HttpSession session){
        //清空session
        String userId = getUserInfoFromSession(session).getUserId();
        userInfoService.logout(userId);
        return success(null);
    }

    @PostMapping("/resetPwd")
    @Transactional(rollbackFor = {Exception.class})
    public ResponseVo<?> resetPassword(HttpSession session,
                                        @NotBlank String emailCode,
                                        @NotBlank String newPassword){
        SessionUserDto sessionUserDto = getUserInfoFromSession(session);
        userInfoService.resetPassword(sessionUserDto.getEmail(),emailCode,newPassword);
        return success(null);
    }

    @PostMapping("/getUseSpace")
    public ResponseVo<?> getUseSpace(HttpSession session){
        SessionUserDto sessionUserDto = getUserInfoFromSession(session);
        UserSpaceDto userSpaceDto = userInfoService.getUseSpaceDto(sessionUserDto.getUserId());
        return success(userSpaceDto);
    }

    @PostMapping("/updateAvatar")
    @Transactional(rollbackFor = {Exception.class})
    public ResponseVo<?> updateAvatar(HttpSession session,
                                      @RequestParam("avatar") MultipartFile avatar){
        SessionUserDto sessionUserDto = getUserInfoFromSession(session);
        //更新avatar文件
        File avatarFile = userInfoService.getAvatarFile(sessionUserDto.getUserId());
        try {
            avatar.transferTo(avatarFile);
        } catch (IOException e) {
            throw new ServerException(ResultCodeEnum.CODE_500,"上传头像失败");
        }
        return success(null);
    }
}
