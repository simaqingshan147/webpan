package xju.fjj.webpan.task;

import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import xju.fjj.webpan.service.FileInfoService;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 自动清理标记为删除的目录和文件,
 *                自动将过期回收站文件删除
 * @date 2023/11/22 23:33
 */
@Component
public class FileCleanTask {
    @Resource
    private FileInfoService fileInfoService;

    //每30分钟执行一次
    @Scheduled(fixedDelay = 1000*60*30)
    @Transactional(rollbackFor = Exception.class)
    public void execute(){
        fileInfoService.removeExpireDirsAndFiles();
    }
}
