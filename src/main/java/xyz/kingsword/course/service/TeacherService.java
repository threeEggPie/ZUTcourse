package xyz.kingsword.course.service;

import com.github.pagehelper.PageInfo;
import org.apache.poi.ss.usermodel.Workbook;
import xyz.kingsword.course.vo.TeacherVo;
import xyz.kingsword.course.pojo.Teacher;
import xyz.kingsword.course.pojo.param.TeacherSelectParam;

import java.util.List;

public interface TeacherService {

    void insert(List<Teacher> teacherList);

    void insert(Teacher teacher);

    void insert(Workbook workbook);

    void delete(String id);

    void update(Teacher teacher);

    PageInfo<Teacher> select(TeacherSelectParam param);

    TeacherVo getById(String id);

    Teacher getTeacherById(String id);

    List<Teacher> getByName(String name);

}
