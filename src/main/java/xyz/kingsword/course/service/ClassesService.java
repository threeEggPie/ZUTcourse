package xyz.kingsword.course.service;


import com.github.pagehelper.PageInfo;
import xyz.kingsword.course.pojo.Classes;
import xyz.kingsword.course.pojo.param.ClassesSelectParam;

import java.util.List;
import java.util.Map;

public interface ClassesService {

    void insert(List<Classes> classesList);

    void update(Classes Classes);

    PageInfo<Classes> select(ClassesSelectParam param);

    List<Classes> getByName(List<String> nameList);

    Map<String, Integer> getClassStudentNum();

}
