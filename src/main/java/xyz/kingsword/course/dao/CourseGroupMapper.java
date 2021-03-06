package xyz.kingsword.course.dao;

import org.apache.ibatis.annotations.Param;
import xyz.kingsword.course.pojo.CourseGroup;
import xyz.kingsword.course.pojo.param.CourseGroupSelectParam;
import xyz.kingsword.course.pojo.param.SelectBookDeclareParam;

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
    List<CourseGroup> selectTeaIdDistinct(CourseGroupSelectParam courseGroupSelectParam);

    /**
     * 根据课程Id去重
     */
    List<CourseGroup> selectBookDeclareStatus(SelectBookDeclareParam selectBookDeclareParam);

    List<CourseGroup> getSemesterCourseGroup(@Param("courseId") String courseId, @Param("semesterId") String semesterId);

    List<CourseGroup> getCourseGroupTeacher(@Param("collection") List<String> courseList, @Param("semesterId") String semesterId);
}
