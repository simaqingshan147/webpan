package xju.fjj.webpan.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xju.fjj.webpan.entity.enums.FileStatusEnums;
import xju.fjj.webpan.entity.vo.Document;
import xju.fjj.webpan.entity.vo.ResponseVo;
import xju.fjj.webpan.service.FileInfoService;

import java.util.List;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 回收站相关controller
 * @date 2023/10/27 17:51
 */
@RestController("recycleController")
@RequestMapping("/recycle")
public class RecycleController extends BaseController{
    @Resource
    FileInfoService fileInfoService;

    /*获取回收站的文件和目录列表*/
    @PostMapping("/loadRecycleList")
    public ResponseVo<?> loadRecycleList(HttpSession session){
        String userId = getUserInfoFromSession(session).getUserId();
        List<Document> results = fileInfoService.findRecycleList(userId);
        return success(results);
    }

    /*批量恢复文件夹和文件*/
    @PostMapping("/recoverFile")
    @Transactional(rollbackFor = Exception.class)
    public ResponseVo<?> recoverFile(HttpSession session,
                                     @NotNull List<Integer> dirIds,
                                     @NotNull List<Integer> fileIds){
        String userId = getUserInfoFromSession(session).getUserId();
        fileInfoService.changeFilesOrDirs(userId,dirIds,fileIds,FileStatusEnums.USING.getStatus());
        return success(null);
    }

    /*批量删除文件夹和文件*/
    @PostMapping("delFile")
    @Transactional(rollbackFor = Exception.class)
    public ResponseVo<?> delFile(HttpSession session,
                                 @NotNull List<Integer> dirIds,
                                 @NotNull List<Integer> fileIds){
        String userId = getUserInfoFromSession(session).getUserId();
        fileInfoService.changeFilesOrDirs(userId,dirIds,fileIds,FileStatusEnums.DELETE.getStatus());
        //清理标记删除文件
        fileInfoService.autoRemove(userId);
        return success(null);
    }
}
