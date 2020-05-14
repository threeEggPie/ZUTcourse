package xyz.kingsword.course.dao;

import org.apache.ibatis.annotations.Param;
import xyz.kingsword.course.pojo.Course;
import xyz.kingsword.course.pojo.Teacher;
import xyz.kingsword.course.pojo.param.CourseSelectParam;

import java.util.List;
import java.util.Optional;

public interface CourseMapper {
    Teacher getBookManager(String courseId);

    void setBookManager(String courseId, String teacherId);

    void setTextBook(String textBookStr, String courseId);

    int deleteCourse(List<String> list);

    Optional<Course> getByPrimaryKey(String id);

    List<Course> getByIdList(List<String> idList);

    int insert(Course record);

    int importData(List<Course> courseList);

    int updateByPrimaryKey(Course record);

    int setTeacherInCharge(@Param("courseId") String courseId, @Param("teaId") String teaId);

    void addCourseBook(@Param("bookId") int bookId, @Param("courseId") String courseId);

    int selectTeacherGroupCount(@Param("semesterId") String semesterId, @Param("courseId") String courseId);

    List<Course> getCourseByInCharge(String teaId);

    List<Course> select(CourseSelectParam param);

    void resetBookManager(String courseId);

    /**
     * 根据班级和学期获取该班级的所有课程
     * @param className
     * @return
     */
    List<Course> selectCourseByClassName(@Param("className") String className , @Param("semester") String semester);
}