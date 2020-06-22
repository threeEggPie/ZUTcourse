package xyz.kingsword.course.dao;

import xyz.kingsword.course.pojo.Classes;
import xyz.kingsword.course.pojo.Course;
import xyz.kingsword.course.pojo.param.ClassesSelectParam;

import java.util.Collection;
import java.util.List;


public interface ClassesMapper {
    int insert(Classes record);

    int insertList(Collection<Classes> collection);

    Classes selectByPrimaryKey(String name);

    List<Classes> selectAll();

    List<Classes> findByName(List<String> nameList);

    int updateByPrimaryKey(Classes record);

    List<Course> getCurriculum(String className, String semesterId);

    List<Classes> select(ClassesSelectParam param);

    void updateStudentNum(String className);
}