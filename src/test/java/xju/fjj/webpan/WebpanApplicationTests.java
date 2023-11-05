package xju.fjj.webpan;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import xju.fjj.webpan.entity.constants.Constants;
import xju.fjj.webpan.entity.dto.SessionUserDto;
import xju.fjj.webpan.entity.dto.UserSpaceDto;
import xju.fjj.webpan.entity.pojo.UserInfo;
import xju.fjj.webpan.entity.query.UserInfoQuery;
import xju.fjj.webpan.entity.vo.PagedResult;
import xju.fjj.webpan.service.EmailCodeService;
import xju.fjj.webpan.service.UserInfoService;

import java.io.File;
import java.util.List;

@SpringBootTest
class WebpanApplicationTests {
    @Resource
    UserInfoService userInfoService;
    @Resource
    EmailCodeService emailCodeService;

    /*UserInfoService测试 结果：完成*/
    @Test
    void TestSelectUserInfo() {
        UserInfoQuery query = new UserInfoQuery();
        query.setPageNo(1);
        query.setPageSize(15);
        query.setNickNameFuzzy("高");
        query.setStatus(0);
        PagedResult<List<UserInfo>> result = userInfoService.findListByPage(query);
        System.out.println("页码: "+result.getPageNo());
        System.out.println("每页行数: "+result.getPageSize());
        System.out.println("总行数: "+result.getTotalCount());
        System.out.println("总页数: "+result.getPageTotal());
        result.getList().forEach(System.out::println);
    }

    @Test
    void TestUpdateUserInfo(){
        UserInfo user = new UserInfo();
        user.setUserId("anqi331");
        user.setStatus(0);
        int i = userInfoService.updateUserInfo(user);
        System.out.println( i == 1 ? "更新成功" : "更新失败");
    }

    @Test
    void TestGetAvatarFile(){
        File avatarFile = userInfoService.getAvatarFile("123456");
        System.out.println(avatarFile.getAbsolutePath());
        System.out.println(avatarFile.getName());

    }

    @Test
    void testRegister(){
        userInfoService.register("fengjunjief@gmail.com","冯俊杰","1111","12345");
    }

    @Test
    void testLogin(){
        SessionUserDto login = userInfoService.login("fengjunjief@gmail.com", "1111");
        System.out.println(login);
    }

    @Test
    void testGetUseSpaceDto(){
        UserSpaceDto useSpaceDto = userInfoService.getUseSpaceDto("314113829821084");
        System.out.println(useSpaceDto);
    }

    /*EmailCodeService测试,结果：完成*/
    @Test
    void TestSendEmailCode(){
        emailCodeService.sendEmailCode("fengjunjief@gmail.com", Constants.EMAIL_CODE_TYPE_REGISTER);
    }

    @Test
    void TestCheckEmailCode(){
        emailCodeService.checkEmailCode("fengjunjief@gmail.com", "95059",Constants.EMAIL_CODE_TYPE_REGISTER);
    }
}
