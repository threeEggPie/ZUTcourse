package xyz.kingsword.course.dao;

import xyz.kingsword.course.VO.TeacherVo;
import xyz.kingsword.course.pojo.Teacher;
import xyz.kingsword.course.pojo.param.TeacherSelectParam;

import java.util.List;

public interface TeacherMapper {
    int insert(List<Teacher> teacherList);

    int deleteByPrimaryKey(String teaId);

    int insert(Teacher record);

    int updateByPrimaryKey(Teacher record);

    List<Teacher> select(TeacherSelectParam param);

    TeacherVo selectById(String id);

    Teacher selectTeacherById(String id);

    List<Teacher> getByResearchRoom(List<String> researchRoom);
}