package xju.fjj.webpan.service;

import xju.fjj.webpan.entity.dto.SessionUserDto;
import xju.fjj.webpan.entity.dto.UserSpaceDto;
import xju.fjj.webpan.entity.pojo.UserInfo;
import xju.fjj.webpan.entity.query.UserInfoQuery;
import xju.fjj.webpan.entity.vo.PagedResult;

import java.io.File;
import java.util.List;
/**
 * @description: 用户信息service
 * @author 新疆大学 冯俊杰
 * @date 2023.10.15
 * @version 1.0
 */
public interface UserInfoService {
    /*通过昵称和状态分页查询用户*/
    PagedResult<List<UserInfo>> findListByPage(UserInfoQuery query);

    /*更新UserInfo*/
    int updateUserInfo(UserInfo updateUserInfo);

    int register(String email, String nickName, String password, String emailCode);

    SessionUserDto login(String email, String password);

    void resetPassword(String email, String emailCode, String newPassword);

    UserSpaceDto getUseSpaceDto(String userId);

    File getAvatarFile(String userId);

    void logout(String userId);

    void updateUserSpace(String userId,Long totalSpace);
}
