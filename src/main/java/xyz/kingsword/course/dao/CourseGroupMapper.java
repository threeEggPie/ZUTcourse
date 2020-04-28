package xyz.kingsword.course.dao;

import xyz.kingsword.course.pojo.CourseGroup;
import xyz.kingsword.course.pojo.param.CourseGroupSelectParam;

import java.util.List;

/**
 * 对应视图course_group_view 可查询每一学期的各课程组
 */
public interface CourseGroupMapper {
    /**
     * 多条件查询，不需要可传空
     */
    List<CourseGroup> select(CourseGroupSelectParam courseGroupSelectParam);

    /**
     * 根据teaId去重
     */
    List<CourseGroup> selectDistinct(CourseGroupSelectParam courseGroupSelectParam);

    List<CourseGroup> getNextSemesterCourseGroup(String courseId);

    List<CourseGroup> geyByClasses(String className);
}
