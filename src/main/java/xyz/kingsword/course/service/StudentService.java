package xyz.kingsword.course.service;

import com.github.pagehelper.PageInfo;
import org.apache.poi.ss.usermodel.Workbook;
import xyz.kingsword.course.vo.StudentVo;
import xyz.kingsword.course.pojo.Student;
import xyz.kingsword.course.pojo.param.StudentSelectParam;

import java.util.List;

public interface StudentService {

    void insert(Student student);

    void insert(List<Student> studentList);

    void insert(Workbook workbook);

    void delete(String id);

    void update(Student student);

    PageInfo<StudentVo> select(StudentSelectParam param);
}
