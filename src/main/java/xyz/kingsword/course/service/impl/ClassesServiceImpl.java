package xyz.kingsword.course.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.kingsword.course.dao.ClassesMapper;
import xyz.kingsword.course.pojo.Classes;
import xyz.kingsword.course.pojo.param.ClassesSelectParam;
import xyz.kingsword.course.service.BookOrderService;
import xyz.kingsword.course.service.ClassesService;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class ClassesServiceImpl implements ClassesService {

    @Autowired
    private ClassesMapper classesMapper;
    @Resource
    private BookOrderService bookOrderService;

    @Override
    @Transactional
    public void insert(List<Classes> classesList) {
        int flag = classesMapper.insertList(classesList);
        log.debug("新增班级,{}", flag);
    }

    @Override
    public void update(Classes Classes) {
        int flag = classesMapper.updateByPrimaryKey(Classes);
        log.debug("更新班级,{}", flag);
    }

    @Override
    public PageInfo<Classes> select(ClassesSelectParam param) {
        return PageHelper.startPage(param.getPageNum(), param.getPageSize()).doSelectPageInfo(() -> classesMapper.select(param));
    }

    @Override
    public List<Classes> getByName(List<String> nameList) {
        return classesMapper.findByName(nameList);
    }

}
