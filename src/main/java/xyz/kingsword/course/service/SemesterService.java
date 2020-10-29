package xyz.kingsword.course.service;

import com.github.pagehelper.PageInfo;
import xyz.kingsword.course.pojo.Semester;

public interface SemesterService {

    void addSemester(Semester semester);

    void updateById(Semester semester);

    PageInfo<Semester> getAllSemester(Integer pageNumber, Integer pageSize);


    /**
     * 获取当前和未来所有学期
     */
    PageInfo<Semester> getFutureSemester(Integer pageNumber, Integer pageSize);


    void updateNow(String semesterId);
}