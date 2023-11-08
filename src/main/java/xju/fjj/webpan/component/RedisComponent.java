package xju.fjj.webpan.component;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import xju.fjj.webpan.entity.constants.Constants;
import xju.fjj.webpan.entity.dto.DownloadFileDto;
import xju.fjj.webpan.entity.dto.UserSpaceDto;
import xju.fjj.webpan.entity.enums.FileStatusEnums;
import xju.fjj.webpan.entity.pojo.UserInfo;
import xju.fjj.webpan.mapper.FileInfoMapper;
import xju.fjj.webpan.mapper.UserInfoMapper;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: redis高级功能的工具类
 * @date 2023/10/18 22:10
 */
@Component("redisComponent")
public class RedisComponent {
    @Resource
    private RedisUtils<Object> redisUtils;
    @Resource
    FileInfoMapper fileInfoMapper;
    @Resource
    UserInfoMapper userInfoMapper;


    public void saveUserSpaceDto(String userId, UserSpaceDto userSpaceDto) {
        //保存到redis
        String key = Constants.REDIS_USER_SPACE_PREFIX + userId;
        redisUtils.set(key,userSpaceDto,Constants.REDIS_ONE_DAY);
        //当用户登录时,可能会重复保存,但是在这里更新db更安全,免得忘了导致数据不一致
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        if(userSpaceDto.getUseSpace() != null)
            userInfo.setUseSpace(userSpaceDto.getUseSpace());
        if(userSpaceDto.getTotalSpace() != null)
            userInfo.setTotalSpace(userSpaceDto.getTotalSpace());
        userInfoMapper.update(userInfo);
    }

    public UserSpaceDto getUseSpaceDto(String userId) {
        UserSpaceDto userSpaceDto = (UserSpaceDto) redisUtils.get(Constants.REDIS_USER_SPACE_PREFIX + userId);
        //如果空间信息已过期
        if(userSpaceDto == null){
            userSpaceDto = new UserSpaceDto();
            Long useSpace = fileInfoMapper.selectUseSpace(userId, FileStatusEnums.USING.getStatus());
            Long totalSpace = userInfoMapper.selectTotalSpace(userId);
            userSpaceDto.setUseSpace(useSpace);
            userSpaceDto.setTotalSpace(useSpace);
            //重新保存
            saveUserSpaceDto(userId,userSpaceDto);
        }
        return userSpaceDto;
    }

    public void removeUserSpaceDto(String userId) {
        redisUtils.delete(Constants.REDIS_USER_SPACE_PREFIX + userId);
    }

    /*redis保存待下载文件信息*/
    public void saveDownloadFileDto(String code, DownloadFileDto fileDto) {
        //限定五分钟后下载过期
        redisUtils.set(Constants.REDIS_DOWNLOAD_FILE_PREFIX+code,fileDto,Constants.REDIS_FIVE_MIN);
    }

    /*redis保存待下载文件信息*/
    public DownloadFileDto getDownloadFileDto(String code) {
        //五分钟能多次下载
        return (DownloadFileDto) redisUtils.get(Constants.REDIS_DOWNLOAD_FILE_PREFIX+code);
    }

    public Long getFileTempSize(String userId, String fileMd5) {
        String key = Constants.REDIS_TEMP_FILE_SIZE_PREFIX+userId+fileMd5;
        Object obj = redisUtils.get(key);
        if(obj == null)
            return 0L;
        else if (obj instanceof Integer)
            return ((Integer)obj).longValue();
        else if(obj instanceof Long)
            return (Long) obj;
        return 0L;
    }

    public void updateFileTempSize(String userId, String fileMd5,Long size){
        String key = Constants.REDIS_TEMP_FILE_SIZE_PREFIX+userId+fileMd5;
        Long oldSize = getFileTempSize(userId,fileMd5);
        redisUtils.set(key,oldSize+size,Constants.REDIS_ONE_HOUR);
    }
}
