package xju.fjj.webpan.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;
import xju.fjj.webpan.entity.constants.Constants;
import xju.fjj.webpan.entity.dto.SessionUserDto;
import xju.fjj.webpan.entity.enums.ResultCodeEnum;
import xju.fjj.webpan.exception.ServerException;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 检查是否为管理员的拦截器，在检查登录之后执行
 * @date 2023/10/19 22:14
 */
public class CheckAdminInterceptor implements HandlerInterceptor {

    public CheckAdminInterceptor() {
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        //获取登录session
        SessionUserDto sessionUserDto = (SessionUserDto) session.getAttribute(Constants.SESSION_KEY);
        if(!sessionUserDto.isAdmin())
            throw new ServerException(ResultCodeEnum.CODE_404,"您没有管理员权限");
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
