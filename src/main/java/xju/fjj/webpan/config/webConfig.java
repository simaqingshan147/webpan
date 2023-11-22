package xju.fjj.webpan.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import xju.fjj.webpan.interceptor.CheckAdminInterceptor;
import xju.fjj.webpan.interceptor.CheckLoginInterceptor;

/**
 * @author 新疆大学 冯俊杰
 * @version 1.0
 * @description: 配置拦截器的配置类
 * @date 2023/10/19 22:16
 */
@Configuration
public class webConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        WebMvcConfigurer.super.addInterceptors(registry);
        CheckLoginInterceptor loginInterceptor = new CheckLoginInterceptor();
        CheckAdminInterceptor adminInterceptor = new CheckAdminInterceptor();
        //添加拦截器
        registry.addInterceptor(loginInterceptor)
                .order(0)
                .addPathPatterns("/user/**")
                .addPathPatterns("/admin/**")
                .addPathPatterns("/file/**")
                .addPathPatterns("/recycle/**")
                .excludePathPatterns("/user/register")
                .excludePathPatterns("/user/login")
                .excludePathPatterns("/user/resetPwd");
        registry.addInterceptor(adminInterceptor)
                .order(1)
                .addPathPatterns("/admin/**");
    }
}
