package xyz.kingsword.course.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.kingsword.course.VO.StudentVo;
import xyz.kingsword.course.dao.StudentMapper;
import xyz.kingsword.course.pojo.Classes;
import xyz.kingsword.course.pojo.Student;
import xyz.kingsword.course.pojo.param.StudentSelectParam;
import xyz.kingsword.course.service.ClassesService;
import xyz.kingsword.course.service.StudentService;
import xyz.kingsword.course.util.UserUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private ClassesService classesService;

    @Override
    public void insert(Student student) {
        insert(Collections.singletonList(student));
    }

    /**
     * 需要验证班级是否存在
     */
    @Override
    public void insert(List<Student> studentList) {
        studentList.parallelStream().forEach(v -> v.setPassword(UserUtil.encrypt(SecureUtil.md5("123456"))));
        studentMapper.insert(studentList);
    }

    @Override
    public void insert(Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(0);
        List<Student> studentList = new ArrayList<>(sheet.getLastRowNum() + 1);
        for (int i = 1; i < sheet.getLastRowNum() + 1; i++) {
            Row row = sheet.getRow(i);
            String username = row.getCell(0).getStringCellValue();
            String name = row.getCell(1).getStringCellValue();
            String className = row.getCell(2).getStringCellValue();
            int grade = (int) row.getCell(3).getNumericCellValue();
            Student student = new Student();
            student.setId(username);
            student.setName(name);
            student.setClassName(className);
            student.setGrade(grade);
            studentList.add(student);
        }
        Row row = sheet.getRow(1);
        insert(studentList);
        Classes classes = new Classes();
        classes.setClassname(row.getCell(2).getStringCellValue());
        classes.setGrade((int) row.getCell(3).getNumericCellValue());
        classes.setStudentNum(studentList.size());
        classesService.insert(Collections.singletonList(classes));
    }

    @Override
    public void delete(String id) {
        studentMapper.delete(id);
    }

    @Override
    public void update(Student student) {
        studentMapper.update(student);
    }

    @Override
    public PageInfo<StudentVo> select(StudentSelectParam param) {
        return PageHelper.startPage(param.getPageNum(), param.getPageSize()).doSelectPageInfo(() -> studentMapper.select(param));
    }

}
