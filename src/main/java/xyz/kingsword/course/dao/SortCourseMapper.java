package xyz.kingsword.course.dao;

import org.apache.ibatis.annotations.Param;
import xyz.kingsword.course.VO.SortCourseVo;
import xyz.kingsword.course.pojo.Course;
import xyz.kingsword.course.pojo.SortCourse;
import xyz.kingsword.course.pojo.param.SortCourseSearchParam;
import xyz.kingsword.course.pojo.param.SortCourseUpdateParam;

import java.util.List;

public interface SortCourseMapper {
    int deleteSortCourseRecord(List<Integer> idList);

    int insert(List<SortCourse> sortCourseList);

    SortCourse selectByPrimaryKey(Integer id);

    int mergeCourseHead(List<Integer> idList);

    int restoreCourseHead(List<Integer> idList);

    int setTeacher(@Param("id") Integer id, @Param("teaId") String teaId);


    /**
     * 多条件查询
     *
     * @param param param {@link SortCourseSearchParam}
     * @return list
     */
//    List<SortCourseVo> searchVo(SearchParam param);

    List<SortCourseVo> search(SortCourseSearchParam param);

    List<SortCourse> getById(List<Integer> idList);

    /**
     * 获取排课历史信息
     */
    List<SortCourseVo> getTeacherHistory(@Param("teacherId") String teacherId, @Param("semesterId") String semesterId);

    List<SortCourseVo> getCourseHistory(@Param("courseId") String courseId, @Param("semesterId") String semesterId);

    int setSortCourse(SortCourseUpdateParam param);


}