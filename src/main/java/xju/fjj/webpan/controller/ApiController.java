package xju.fjj.webpan.controller;

import com.wf.captcha.SpecCaptcha;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import xju.fjj.webpan.config.AppConfig;
import xju.fjj.webpan.entity.constants.Constants;
import xju.fjj.webpan.entity.dto.DownloadFileDto;
import xju.fjj.webpan.entity.enums.ResultCodeEnum;
import xju.fjj.webpan.entity.vo.ResponseVo;
import xju.fjj.webpan.exception.ServerException;
import xju.fjj.webpan.service.EmailCodeService;
import xju.fjj.webpan.service.FileInfoService;

import java.io.File;
import java.io.IOException;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description 实现多个控制器共用的api或无需登录调用的api
 * @date 2023/10/15 22:26
 */
@RestController("apiController")
@RequestMapping("/api")
public class ApiController extends BaseController {
    @Resource
    EmailCodeService emailCodeService;
    @Resource
    FileInfoService fileInfoService;
    @Resource
    AppConfig appConfig;

    /*获取人机验证码*/
    @GetMapping("/checkCode")
    public void checkCode(HttpServletResponse response,
                          HttpSession session) throws IOException {
        //生成验证码
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 38, 5);
        //避免浏览器缓存验证码图片
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        //保存验证码到session
        session.setAttribute(Constants.CHECK_CODE_KEY, specCaptcha.text());
        //上传图片
        response.setContentType("image/jpeg");
        specCaptcha.out(response.getOutputStream());
    }

    @PostMapping("/emailCode")
    public ResponseVo<?> emailCode(HttpSession session,
                                   @NotBlank @Email String email,
                                   @NotBlank String checkCode,
                                   @NotNull Integer type){
        if(!checkCheckCode(session,checkCode))
            throw new ServerException(ResultCodeEnum.CODE_600,"图像验证码不正确");
        emailCodeService.sendEmailCode(email,type);
        return success(null);
    }

    /*根据下载链接(code)下载文件*/
    @GetMapping("/download/{code}")
    public void download(HttpServletResponse response,
                         @NotNull @PathVariable("code") String code){
        DownloadFileDto fileDto =  fileInfoService.getDownloadFileDto(code);
        if(fileDto == null)
            throw new ServerException(ResultCodeEnum.CODE_600,"下载链接错误或已过期");
        String filePath = appConfig.getProjectFolder()+Constants.FILE_FOLDER_FILE+fileDto.getPath();
        String fileName = fileDto.getFileName();
        response.setContentType("application/x-msdownload;charset=UTF-8");
        response.setHeader("Content-Disposition","attachment;filename=\""+fileName+"\"");
        readFile(response,new File(filePath));
    }
}
