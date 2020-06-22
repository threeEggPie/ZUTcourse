package xyz.kingsword.course.aop;

import cn.hutool.core.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import xyz.kingsword.course.annocations.Role;
import xyz.kingsword.course.enmu.ErrorEnum;
import xyz.kingsword.course.enmu.RoleEnum;
import xyz.kingsword.course.exception.AuthException;
import xyz.kingsword.course.pojo.User;
import xyz.kingsword.course.util.ConditionUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * 比较简单的权限验证方式，更灵活的可以考虑shiro
 */
@Slf4j
@Aspect
@Component
public class HttpAspect {

    @Pointcut("execution(* xyz.kingsword.course.controller..*.*(..))")
    public void log() {

    }

    @Before("log()")
    public void doBefore(JoinPoint joinPoint) {
//        获取方法签名
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        if (method.isAnnotationPresent(Role.class)) {
            Role role = method.getAnnotation(Role.class);
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                HttpSession session = request.getSession();
                User user = (User) session.getAttribute("user");
                Optional.ofNullable(user).orElseThrow(() -> new AuthException(ErrorEnum.UN_LOGIN));
                int roleId = user.getCurrentRole();
                boolean flag = ArrayUtil.contains(role.value(), RoleEnum.valueOf(roleId));
                ConditionUtil.validateTrue(flag).orElseThrow(AuthException::new);
            }
        }
        printRequest(joinPoint);
    }

    /**
     * 打印请求信息，测试专用
     */
    private void printRequest(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            log.debug("aop url = {}", request.getRequestURL());
            log.debug("aop method = {}", request.getMethod());
            log.debug("aop args = {}", joinPoint.getArgs());
        }
    }
}
