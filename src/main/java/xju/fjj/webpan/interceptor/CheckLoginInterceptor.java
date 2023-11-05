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
 * @description: 检查是否登录的拦截器
 * @date 2023/10/19 21:37
 */
public class CheckLoginInterceptor implements HandlerInterceptor {
    public CheckLoginInterceptor() {

    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        //获取登录session
        SessionUserDto sessionUserDto = (SessionUserDto) session.getAttribute(Constants.SESSION_KEY);
        if(sessionUserDto == null)
            throw new ServerException(ResultCodeEnum.CODE_404,"请登录访问");
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
