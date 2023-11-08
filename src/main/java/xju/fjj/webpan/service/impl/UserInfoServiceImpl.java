package xju.fjj.webpan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;
import xju.fjj.webpan.component.RedisComponent;
import xju.fjj.webpan.config.AppConfig;
import xju.fjj.webpan.entity.constants.Constants;
import xju.fjj.webpan.entity.dto.SessionUserDto;
import xju.fjj.webpan.entity.dto.UserSpaceDto;
import xju.fjj.webpan.entity.enums.FileStatusEnums;
import xju.fjj.webpan.entity.enums.ResultCodeEnum;
import xju.fjj.webpan.entity.enums.UserStatusEnum;
import xju.fjj.webpan.entity.pojo.UserInfo;
import xju.fjj.webpan.entity.query.UserInfoQuery;
import xju.fjj.webpan.entity.vo.PagedResult;
import xju.fjj.webpan.exception.ServerException;
import xju.fjj.webpan.mapper.FileInfoMapper;
import xju.fjj.webpan.mapper.UserInfoMapper;
import xju.fjj.webpan.service.EmailCodeService;
import xju.fjj.webpan.service.UserInfoService;
import xju.fjj.webpan.utils.StringTools;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 用户信息service的实现类
 * @date 2023/10/15 0:49
 */
@Service
public class UserInfoServiceImpl implements UserInfoService {
    @Resource
    AppConfig appConfig;
    @Resource
    UserInfoMapper userInfoMapper;
    @Resource
    FileInfoMapper fileInfoMapper;
    @Resource
    RedisComponent redisComponent;
    @Resource
    EmailCodeService emailCodeService;
    @Override
    public PagedResult<List<UserInfo>> findListByPage(UserInfoQuery query) {
        //设置分页
        PageHelper.startPage(query.getPageNo(),query.getPageSize());
        List<UserInfo> users = userInfoMapper.selectAllByPage(query.getNickName(), query.getStatus());
        //获取分页信息
        PageInfo<UserInfo> pageInfo = new PageInfo<>(users);
        return PagedResult.convert(pageInfo);
    }

    @Override
    public int updateUserInfo(UserInfo updateUserInfo) {
        return userInfoMapper.update(updateUserInfo);
    }

    @Override
    public void updateUserSpace(String userId,Long totalSpace) {
        //更新redis,同时更新db
        UserSpaceDto useSpaceDto = redisComponent.getUseSpaceDto(userId);
        useSpaceDto.setTotalSpace(totalSpace);
        redisComponent.saveUserSpaceDto(userId,useSpaceDto);
    }

    @Override
    public int register(String email, String nickName, String password, String emailCode) {
        //检测邮箱是否已注册
        UserInfo checkEmail =  userInfoMapper.selectByEmail(email);
        if(checkEmail != null)
            throw  new ServerException(ResultCodeEnum.CODE_601,"邮箱账户已存在");
        //检测邮箱验证码
        boolean checked = emailCodeService.checkEmailCode(email, emailCode, Constants.EMAIL_CODE_TYPE_REGISTER);
        if(!checked)
            throw new ServerException(ResultCodeEnum.CODE_600,"邮箱验证码错误");

        //注册
        String randomId = StringTools.getRandomNumber(Constants.LENGTH_15);
        UserInfo newUser = new UserInfo();
        newUser.setUserId(randomId);
        newUser.setNickName(nickName);
        newUser.setEmail(email);
        //密码在前端加密
        newUser.setPassword(password);
        newUser.setJoinTime(new Date());
        newUser.setStatus(UserStatusEnum.ENABLE.getStatus());
        newUser.setUseSpace(0L);
        newUser.setTotalSpace(appConfig.getInitialTotalSpace()*Constants.MB);
        return userInfoMapper.insert(newUser);
    }

    @Override
    public SessionUserDto login(String email, String password) {
        UserInfo user =  userInfoMapper.selectByEmail(email);
        if(user == null)
            throw new ServerException(ResultCodeEnum.CODE_600,"账号不存在");
        else if (!password.equals(user.getPassword())) {
            throw new ServerException(ResultCodeEnum.CODE_600,"密码错误");
        } else if (user.getStatus().equals(UserStatusEnum.DISABLE.getStatus())) {
            throw new ServerException(ResultCodeEnum.CODE_600,"账号已封禁");
        }
        //更新最后登录时间
        user.setLastLoginTime(new Date());
        userInfoMapper.update(user);
        //返回session
        SessionUserDto sessionUserDto = new SessionUserDto();
        sessionUserDto.setUserId(user.getUserId());
        sessionUserDto.setNickName(user.getNickName());
        sessionUserDto.setEmail(user.getEmail());
        sessionUserDto.setAdmin(ArrayUtils.contains(appConfig.getAdminEmails().split(","), email));
        //用户空间存储到redis
        UserSpaceDto userSpaceDto = new UserSpaceDto();
        //查询用户使用空间
        userSpaceDto.setUseSpace(user.getUseSpace());
        userSpaceDto.setTotalSpace(user.getTotalSpace());
        redisComponent.saveUserSpaceDto(user.getUserId(),userSpaceDto);
        return sessionUserDto;
    }

    @Override
    public void resetPassword(String email, String emailCode, String newPassword) {
        UserInfo userInfo = userInfoMapper.selectByEmail(email);
        if(userInfo == null){
            throw new ServerException(ResultCodeEnum.CODE_600,"账号或邮箱错误");
        }
        //检测邮箱验证码
        boolean checked = emailCodeService.checkEmailCode(email, emailCode, Constants.EMAIL_CODE_TYPE_RESET);
        if(!checked)
            throw new ServerException(ResultCodeEnum.CODE_600,"邮箱验证码错误");
        //重置密码
        userInfo.setPassword(newPassword);
        userInfoMapper.update(userInfo);
    }

    @Override
    public UserSpaceDto getUseSpaceDto(String userId) {
        return redisComponent.getUseSpaceDto(userId);
    }

    @Override
    public File getAvatarFile(String userId) {
        //拼接头像文件夹的绝对路径
        String avatarFolder = appConfig.getProjectFolder() + Constants.FILE_FOLDER_AVATAR;
        File folder = new File(avatarFolder);
        if(!folder.exists()){
            folder.mkdirs();
            return null;
        }
        //拼接用户对应头像文件的绝对路径
        String avatar = avatarFolder + userId + Constants.AVATAR_SUFFIX;
        File avatarFile = new File(avatar);
        if(!avatarFile.exists())
            avatarFile = new File(avatarFolder+Constants.AVATAR_DEFAULT_NAME);
        return avatarFile;
    }

    @Override
    public void logout(String userId) {
        //将最新的useSpace更新到用户表中
        Long useSpace =  fileInfoMapper.selectUseSpace(userId, FileStatusEnums.USING.getStatus());
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setUseSpace(useSpace);
        userInfoMapper.update(userInfo);
        //删除redis缓存
        redisComponent.removeUserSpaceDto(userId);
        //TODO logout可能需要删除更多redis缓存，或者进一步处理
    }
}
