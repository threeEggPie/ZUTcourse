package xyz.kingsword.course.service;

import com.github.pagehelper.PageInfo;
import xyz.kingsword.course.vo.CourseVo;
import xyz.kingsword.course.pojo.Course;
import xyz.kingsword.course.pojo.param.CourseSelectParam;

import java.io.InputStream;
import java.util.List;

public interface CourseService {

    void insert(Course course);

    CourseVo findCourseById(String id);

    int deleteCourse(List<String> list);

    int updateById(Course course);

    void setTeacherInCharge(String id, String teaId);

    PageInfo<CourseVo> select(CourseSelectParam param);

    void resetBookManager(String courseId);

    void importData(InputStream inputStream);

}
