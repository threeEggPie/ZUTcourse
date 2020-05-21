package xyz.kingsword.course.service.impl;

import cn.hutool.cache.Cache;
import cn.hutool.crypto.SecureUtil;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import xyz.kingsword.course.VO.TeacherVo;
import xyz.kingsword.course.dao.TeacherMapper;
import xyz.kingsword.course.pojo.Teacher;
import xyz.kingsword.course.pojo.param.TeacherSelectParam;
import xyz.kingsword.course.service.TeacherService;
import xyz.kingsword.course.util.UserUtil;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Slf4j
@Service
public class TeacherServiceImpl implements TeacherService {

    @Resource
    private TeacherMapper teacherMapper;

    @Resource(name = "teacher")
    private Cache<String, Teacher> cache;

    @Override
    public void insert(List<Teacher> teacherList) {
        teacherList.forEach(v -> v.setPassword(UserUtil.encrypt(SecureUtil.md5("123456"))));
        int flag = teacherMapper.insert(teacherList);
        if (flag != teacherList.size())
            log.error("数据库插入数据异常");
    }

    @Override
    public void insert(Teacher teacher) {
        insert(Collections.singletonList(teacher));
    }

    @Override
    public void insert(Workbook workbook) {
        List<Teacher> teacherList = new ArrayList<>();
        Sheet sheet = workbook.getSheetAt(0);
        for (int i = 0; i < sheet.getLastRowNum() + 1; i++) {
            Row row = sheet.getRow(i);
            Teacher teacher = new Teacher();
            teacher.setId(row.getCell(0).getStringCellValue());
            teacher.setName(row.getCell(1).getStringCellValue());
            teacherList.add(teacher);
        }
        insert(teacherList);
    }

    @Override
    public void delete(String id) {
        teacherMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void update(Teacher teacher) {
        teacherMapper.updateByPrimaryKey(teacher);
    }

    @Override
    public PageInfo<Teacher> select(TeacherSelectParam param) {
        return PageMethod.startPage(param.getPageNum(), param.getPageSize()).doSelectPageInfo(() -> teacherMapper.select(param));
    }

    @Override
    public TeacherVo getById(String id) {
        return teacherMapper.selectById(id);
    }

    @Override
    public Teacher getTeacherById(String id) {
        return cache.get(id, () -> teacherMapper.selectTeacherById(id));
    }

    @Override
    public List<Teacher> getByName(String name) {
        return select(TeacherSelectParam.builder().name(name).build()).getList();
    }
}
