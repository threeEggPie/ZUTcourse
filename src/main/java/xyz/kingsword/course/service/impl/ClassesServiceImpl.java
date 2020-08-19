package xyz.kingsword.course.service.impl;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.kingsword.course.dao.ClassesMapper;
import xyz.kingsword.course.pojo.Classes;
import xyz.kingsword.course.pojo.param.ClassesSelectParam;
import xyz.kingsword.course.service.ClassesService;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ClassesServiceImpl implements ClassesService {

    @Autowired
    private ClassesMapper classesMapper;

    @PostConstruct
    public void init() {
//        classesMapper.selectAll().forEach(v -> classesMapper.updateStudentNum(v.getClassname()));
    }

    @Override
    public void insert(List<Classes> classesList) {
        int flag = classesMapper.insertList(classesList);
        log.debug("新增班级,{}", flag);
    }

    @Override
    public void update(Classes classes) {
        int flag = classesMapper.updateByPrimaryKey(classes);
        log.debug("更新班级,{}", flag);
    }

    @Override
    public PageInfo<Classes> select(ClassesSelectParam param) {
        return PageMethod.startPage(param.getPageNum(), param.getPageSize()).doSelectPageInfo(() -> classesMapper.select(param));
    }

    @Override
    public List<Classes> getByName(List<String> nameList) {
        return classesMapper.findByName(nameList);
    }

    /**
     * @return [RB软工17级卓越班, 39]
     */
    @Override
    public Map<String, Integer> getClassStudentNum() {
        List<Classes> classesList = classesMapper.selectAll();
        return classesList.stream().collect(Collectors.toMap(Classes::getClassname, Classes::getStudentNum));
    }

}
