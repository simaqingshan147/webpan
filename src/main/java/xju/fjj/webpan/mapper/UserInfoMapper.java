package xju.fjj.webpan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xju.fjj.webpan.entity.pojo.UserInfo;

import java.util.List;

@Mapper
public interface UserInfoMapper{
    List<UserInfo> selectAllByPage(@Param("nickName") String nickName,
                                   @Param("status") Integer status);

    int update(UserInfo updateUserInfo);

    UserInfo selectByEmail(String email);

    int insert(UserInfo newUser);

    UserInfo selectByUserId(String userId);

    Long selectTotalSpace(String userId);
}
