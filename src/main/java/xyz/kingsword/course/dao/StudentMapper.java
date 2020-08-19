package xyz.kingsword.course.dao;

import xyz.kingsword.course.vo.StudentVo;
import xyz.kingsword.course.pojo.DO.CurriculumDo;
import xyz.kingsword.course.pojo.Student;
import xyz.kingsword.course.pojo.param.StudentSelectParam;

import java.util.List;


public interface StudentMapper {
    int insert(List<Student> record);

    int update(Student record);

    int delete(String id);

    List<StudentVo> select(StudentSelectParam param);

    Student selectById(String id);

    /**
     * 获取学生课程表
     */
    List<CurriculumDo> curriculum(String studentId, String semesterId);

}