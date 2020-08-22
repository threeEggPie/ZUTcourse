package xyz.kingsword.course.service.impl;

import cn.hutool.cache.Cache;
import org.springframework.stereotype.Service;
import xyz.kingsword.course.dao.SortCourseLogMapper;
import xyz.kingsword.course.pojo.SortCourseLog;
import xyz.kingsword.course.pojo.Teacher;
import xyz.kingsword.course.pojo.User;
import xyz.kingsword.course.pojo.param.SortCourseUpdateParam;
import xyz.kingsword.course.service.SortCourseLogService;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SortCourseLogServiceImpl implements SortCourseLogService {

    @Resource
    Cache<String, Teacher> teacherCache;
    @Resource
    SortCourseLogMapper logMapper;

    @Override
    public void addLog(SortCourseUpdateParam param, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Teacher teacher = teacherCache.get(param.getTeacherId());
        param.setTeacherName(teacher.getName());
        logMapper.insert(param,user);
    }

    @Override
    public List<SortCourseLog> getLogs(Integer sortCourseId) {
        List<SortCourseLog> logs=logMapper.selectLogBySortCourseId(sortCourseId);
        return logs;
    }
}
