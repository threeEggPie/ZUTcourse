package xyz.kingsword.course.service;

import com.github.pagehelper.PageInfo;
import xyz.kingsword.course.pojo.TrainingProgram;
import xyz.kingsword.course.pojo.param.TrainingProgramSearchParam;

import java.io.InputStream;
import java.util.List;


public interface TrainingProgramService {
    void insert(TrainingProgram record);

    void insert(List<TrainingProgram> record);

    void update(TrainingProgram record);


    void delete(List<Integer> idList);

    PageInfo<TrainingProgram> select(TrainingProgramSearchParam param);


    List<TrainingProgram> importData(InputStream inputStream);
}
