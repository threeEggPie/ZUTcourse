package xyz.kingsword.course.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import xyz.kingsword.course.util.SpringContextUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@Configuration
public class WebConfigurer implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry interceptorRegistry) {
        String profile = SpringContextUtil.getActiveProfile();
        interceptorRegistry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                HttpSession session = request.getSession();
//                if (session.getAttribute("user") == null && profile.equals("prod")) {
//                    throw new AuthException(ErrorEnum.UN_LOGIN);
//                }
                return true;
            }
        }).excludePathPatterns("/login", "/logout");
    }
}
