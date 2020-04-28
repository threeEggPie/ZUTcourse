package xyz.kingsword.course.dao;

import org.apache.ibatis.annotations.Mapper;
import xyz.kingsword.course.pojo.TrainingProgram;
import xyz.kingsword.course.pojo.param.TrainingProgramSearchParam;

import java.util.List;

@Mapper
public interface TrainingProgramMapper {

    int delete(List<Integer> idList);

    int insert(TrainingProgram record);

    int insertList(List<TrainingProgram> recordList);

    int update(TrainingProgram record);


    List<TrainingProgram> select(TrainingProgramSearchParam param);

}