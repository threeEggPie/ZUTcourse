package xyz.kingsword.course.controller;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import xyz.kingsword.course.enmu.ErrorEnum;
import xyz.kingsword.course.enmu.RoleEnum;
import xyz.kingsword.course.exception.AuthException;
import xyz.kingsword.course.exception.BaseException;
import xyz.kingsword.course.pojo.Result;
import xyz.kingsword.course.pojo.User;
import xyz.kingsword.course.pojo.param.StudentSelectParam;
import xyz.kingsword.course.service.StudentService;
import xyz.kingsword.course.service.TeacherService;
import xyz.kingsword.course.service.UserService;
import xyz.kingsword.course.util.ConditionUtil;
import xyz.kingsword.course.util.UserUtil;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Slf4j
@Api(tags = "用户操作相关类")
@RestController
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private StudentService studentService;
    @Resource
    private TeacherService teacherService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ApiOperation(value = "登录", notes = "通过账户密码进行登录")
    public Result<Object> login(@RequestBody User user, HttpSession session) {
        user = userService.login(user);
        setSession(user, session);
        return new Result<>();
    }

    @RequestMapping(value = "/userInfo", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户信息")
    public Result<Object> userInfo(HttpSession session) {
        User user = (User) session.getAttribute("user");
        Object o = userService.getUserInfo(user);
        return new Result<>(o);
    }


    @ApiOperation(value = "退出")
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public Result<Object> logout(HttpSession session, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies.length > 0) {
            for (Cookie cookie : cookies) {
                cookie.setMaxAge(1);
            }
        }
        session.invalidate();
        return new Result<>();
    }


    @ApiOperation(value = "重置密码")
    @ApiImplicitParam(name = "newPassword", value = "新密码，md5加密过", required = true)
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public Result<Object> resetPassword(String newPassword, HttpSession session) {
        User user = (User) session.getAttribute("user");
        int flag = userService.resetPassword(newPassword, user);
        ConditionUtil.validateTrue(flag == 1).orElseThrow(() -> new BaseException("旧密码错误"));
        return new Result<>();
    }

    @RequestMapping(value = "/loginOnRole", method = RequestMethod.POST)
    @ApiOperation(value = "按角色登录", notes = "通过账户密码进行登录")
    @ApiImplicitParam(name = "roleId", value = "角色Id", required = true)
    public Result<Object> loginOnRole(HttpServletRequest request, int roleId) {
        HttpSession session = request.getSession();
        User user = UserUtil.getUser();
        Optional.ofNullable(user).orElseThrow(() -> new AuthException(ErrorEnum.UN_LOGIN));
        List<Integer> roleList = JSONArray.parseArray(user.getRole()).toJavaList(Integer.class);
        ConditionUtil.validateTrue(roleList.contains(roleId)).orElseThrow(() -> new AuthException(ErrorEnum.NO_AUTH));
        session.invalidate();
        user.setCurrentRole(roleId);
        user.setCurrentRoleName(RoleEnum.valueOf(roleId).getContent());
        setSession(user, request.getSession());
        return new Result<>();
    }

    private void setSession(User user, HttpSession session) {
        session.setAttribute("user", user);
//        session.setAttribute("bookOrderList", bookOrderService.select(user.getUsername(), null, null));
        if (user.getCurrentRole() == RoleEnum.STUDENT.getCode()) {
            session.setAttribute("student", studentService.select(StudentSelectParam.builder().id(user.getUsername()).build()).getList().get(0));
        } else {
            session.setAttribute("teacher", teacherService.getById(user.getUsername()));
        }
    }
}
