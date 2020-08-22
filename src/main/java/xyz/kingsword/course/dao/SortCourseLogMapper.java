package xyz.kingsword.course.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.kingsword.course.pojo.SortCourseLog;
import xyz.kingsword.course.pojo.Teacher;
import xyz.kingsword.course.pojo.User;
import xyz.kingsword.course.pojo.param.SortCourseUpdateParam;

import java.util.List;

@Mapper
public interface SortCourseLogMapper {
    void insert(@Param("param") SortCourseUpdateParam sortCourseUpdateParam, @Param("user") User user);

    List<SortCourseLog> selectLogBySortCourseId(Integer sortCourseId);
}
