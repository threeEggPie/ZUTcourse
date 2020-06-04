package xyz.kingsword.course.service.impl;


import lombok.NonNull;
import org.springframework.stereotype.Service;
import xyz.kingsword.course.vo.*;
import xyz.kingsword.course.dao.CourseGroupMapper;
import xyz.kingsword.course.dao.UserMapper;
import xyz.kingsword.course.enmu.ErrorEnum;
import xyz.kingsword.course.enmu.RoleEnum;
import xyz.kingsword.course.exception.AuthException;
import xyz.kingsword.course.pojo.*;
import xyz.kingsword.course.pojo.param.BookOrderSelectParam;
import xyz.kingsword.course.pojo.param.CourseGroupSelectParam;
import xyz.kingsword.course.service.BookOrderService;
import xyz.kingsword.course.service.BookService;
import xyz.kingsword.course.service.UserService;
import xyz.kingsword.course.util.ConditionUtil;
import xyz.kingsword.course.util.TimeUtil;
import xyz.kingsword.course.util.UserUtil;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private BookService bookService;
    @Resource
    private BookOrderService bookOrderService;
    @Resource
    private CourseGroupMapper courseGroupMapper;

    /**
     * 通过用户名进行登录
     */
    @Override
    public User login(User user) {
        User userDb = userMapper.login(user.getUsername());
        ConditionUtil.notNull(userDb).orElseThrow(() -> new AuthException(ErrorEnum.ERROR_LOGIN));
        boolean flag = UserUtil.validPassword(user.getPassword(), userDb.getPassword());
        ConditionUtil.validateTrue(flag).orElseThrow(() -> new AuthException(ErrorEnum.ERROR_LOGIN));
        return userDb;
    }


    @Override
    public int resetPassword(@NonNull String password, @NonNull User user) {
        password = UserUtil.encrypt(password);
        return isStudent(user) ? userMapper.resetPasswordStudent(user.getUsername(), password) : userMapper.resetPasswordTeacher(user.getUsername(), password);
    }

    @Override
    public Object getUserInfo(User user) {
        return isStudent(user) ? getStudent(user) : getTeacher(user);
    }

    private StudentVo getStudent(User user) {
        String semesterId = TimeUtil.getNextSemester().getId();
        StudentVo studentVo = UserUtil.getStudent();
        List<CourseGroup> courseGroupList = courseGroupMapper.select(CourseGroupSelectParam.builder().className(studentVo.getClassName()).semesterId(semesterId).build());
        List<CourseBookOrderVo> courseBookOrderVoList = getCourseList(courseGroupList, user.getUsername());
        studentVo.setSemesterId(semesterId);
        studentVo.setCourseList(courseBookOrderVoList);
        return studentVo;
    }

    private TeacherVo getTeacher(User user) {
        TeacherVo teacherVo = UserUtil.getTeacher();
        String semesterId = TimeUtil.getNextSemester().getId();
        List<CourseGroup> courseGroupList = courseGroupMapper.select(CourseGroupSelectParam.builder().teaId(user.getUsername()).semesterId(semesterId).build());
        List<CourseBookOrderVo> courseBookOrderVoList = getCourseList(courseGroupList, user.getUsername());
        teacherVo.setCurrentRole(user.getCurrentRole());
        teacherVo.setSemesterId(semesterId);
        teacherVo.setCourseList(courseBookOrderVoList);
        return teacherVo;
    }

    /**
     * 构建导出个人信息时附带下一学期的订书信息
     *
     * @param courseGroupList 课程组view
     * @param username        username
     */
    private List<CourseBookOrderVo> getCourseList(List<CourseGroup> courseGroupList, String username) {
        List<CourseBookOrderVo> courseBookOrderVoList = new ArrayList<>(courseGroupList.size());
        if (!courseGroupList.isEmpty()) {
            String semesterId = courseGroupList.get(0).getSemesterId();
            Set<Integer> bookIdSet = courseGroupList.parallelStream().flatMap(v -> v.getTextBook().stream()).collect(Collectors.toSet());
            Map<Integer, Book> bookMap = bookService.getMap(bookIdSet);
            BookOrderSelectParam param = BookOrderSelectParam.builder().userId(username).semesterId(semesterId).build();
            Map<Integer, BookOrderVo> bookIdToOrder = bookOrderService.select(param).parallelStream().collect(Collectors.toMap(BookOrderVo::getBookId, v -> v));
            for (CourseGroup courseGroup : courseGroupList) {
                List<BookOrderFlag> bookOrderFlagList = new ArrayList<>();
                List<Integer> bookIdList = courseGroup.getTextBook();
                if (bookIdList != null && !bookIdList.isEmpty()) {
                    for (Integer bookId : courseGroup.getTextBook()) {
                        Book book = bookMap.get(bookId);
                        BookOrderVo bookOrderVo = bookIdToOrder.get(bookId);
                        BookOrderFlag bookOrderFlag = BookOrderFlag.builder()
                                .info(book).flag(bookOrderVo != null)
                                .orderId(bookOrderVo != null ? bookOrderVo.getOrderId() : null)
                                .build();
                        bookOrderFlagList.add(bookOrderFlag);
                    }
                }
                CourseBookOrderVo courseBookOrderVo = CourseBookOrderVo.builder()
                        .courseId(courseGroup.getCouId())
                        .courseName(courseGroup.getCourseName())
                        .textBook(bookOrderFlagList).build();
                courseBookOrderVoList.add(courseBookOrderVo);
            }
        }

        return courseBookOrderVoList;
    }


    private boolean isStudent(User user) {
        Optional.ofNullable(user).orElseThrow(AuthException::new);
        return user.getCurrentRole() == RoleEnum.STUDENT.getCode();
    }
}
