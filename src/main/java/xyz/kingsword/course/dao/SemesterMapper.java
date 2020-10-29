package xyz.kingsword.course.dao;

import org.apache.ibatis.annotations.Mapper;
import xyz.kingsword.course.pojo.Semester;

import java.util.List;

@Mapper
public interface SemesterMapper {
    int insert(Semester record);

    int updateById(Semester semester);

    /**
     * 根据id查学期
     *
     * @param id
     * @return
     */
    Semester findById(String id);

    List<Semester> selectAll();

    /**
     * 获取当前和未来学期
     */
    List<Semester> getFutureSemester();

    void updateNow(String semesterId);
}