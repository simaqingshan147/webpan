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
import xju.fjj.webpan.entity.enums.FileStatusEnums;
import xju.fjj.webpan.entity.enums.ResultCodeEnum;
import xju.fjj.webpan.entity.pojo.DirInfo;
import xju.fjj.webpan.entity.pojo.FileInfo;
import xju.fjj.webpan.entity.vo.ResponseVo;
import xju.fjj.webpan.exception.ServerException;
import xju.fjj.webpan.service.EmailCodeService;
import xju.fjj.webpan.service.FileInfoService;
import xju.fjj.webpan.service.UserInfoService;
import xju.fjj.webpan.utils.StringTools;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    UserInfoService userInfoService;
    @Resource
    EmailCodeService emailCodeService;
    @Resource
    FileInfoService fileInfoService;
    @Resource
    AppConfig appConfig;

    /*
    根据userId获取头像url
        参数名	是否必填	参数类型	描述说明
        userId    是    String     用户Id
     */
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

    @GetMapping("/emailCode")
    public ResponseVo<?> emailCode(HttpSession session,
                                   @NotBlank @Email String email,
                                   @NotBlank String checkCode,
                                   @NotNull Integer type){
        if(!checkCheckCode(session,checkCode))
            throw new ServerException(ResultCodeEnum.CODE_600,"图像验证码不正确");
        emailCodeService.sendEmailCode(email,type);
        return success(null);
    }

    /*获取文件封面*/
    @GetMapping("/getImage/{imageFolder}/{imageName}")
    public void getImage(HttpServletResponse response,
                         @PathVariable("imageFolder") String imageFolder,
                         @PathVariable("imageName") String imageName,
                         @RequestParam(value = "isFolder",defaultValue = "false")Boolean isFolder){
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

    /*获取一长串目录下的最末目录的id,也就是获取当前所属目录id*/
    @GetMapping("/getFolderInfo")
    public ResponseVo<Map<String, Object>> getFolderInfo(@NotBlank String path){
        String[] dirs = path.split("/");
        //最末目录的id,因为目录id是unique的，所以无需递归查询
        String dir = dirs[dirs.length - 1];
        DirInfo dirInfo = fileInfoService.getDirByDirId(Integer.parseInt(dir));
        Map<String,Object> idAndName = new HashMap<>();
        idAndName.put("dirId",dirInfo.getDirId());
        idAndName.put("dirName",dirInfo.getDirName());
        return success(idAndName);
    }

    /*检测下载权限,创建下载链接并返回*/
    @GetMapping("/createDownLoadUrl/{fileId}")
    public ResponseVo<String> createDownLoadUrl(@NotNull @PathVariable("fileId") Integer fileId){
        FileInfo fileInfo =  fileInfoService.getFileByFileId(fileId);
        //文件不存在
        if(fileInfo == null || !fileInfo.getStatus().equals(FileStatusEnums.USING.getStatus()))
            throw new ServerException(ResultCodeEnum.CODE_600,"下载文件不存在!");
        //TODO 创建下载链接可能需要检测权限,可能是因为分享让未登录用户可以下载?
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
