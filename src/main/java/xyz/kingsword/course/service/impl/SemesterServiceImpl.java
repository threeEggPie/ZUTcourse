package xyz.kingsword.course.service.impl;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.springframework.stereotype.Service;
import xyz.kingsword.course.dao.SemesterMapper;
import xyz.kingsword.course.pojo.Semester;
import xyz.kingsword.course.service.SemesterService;
import xyz.kingsword.course.util.TimeUtil;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SemesterServiceImpl implements SemesterService {

    @Resource
    private SemesterMapper semesterMapper;

    private static List<Semester> semesterList;

    @PostConstruct
    public void init() {
        semesterList = semesterMapper.selectAll();
    }

    public static List<Semester> getSemesterList() {
        return SemesterServiceImpl.semesterList;
    }


    @Override
    public void addSemester(Semester semester) {
        semester.setName(TimeUtil.getSemesterName(semester.getId()));
        if (semesterMapper.insert(semester) == 1) {
            semesterList.add(semester);
        }
    }


    @Override
    public void updateById(Semester semester) {
        if (semesterMapper.updateById(semester) == 1) {
            semesterList.removeIf(v -> v.getId().equals(semester.getId()));
            semesterList.add(semester);
        }
    }

    @Override
    public PageInfo<Semester> getAllSemester(Integer pageNumber, Integer pageSize) {
        int size = semesterList.size();
        pageSize = pageSize == 0 ? size : pageSize;
        int navigatePages = size % pageSize > 0 ? size / pageSize + 1 : size / pageSize;
        return PageInfo.of(semesterList, navigatePages);
    }

    @Override
    public PageInfo<Semester> getFutureSemester(Integer pageNumber, Integer pageSize) {
        List<Semester> semesterList = SemesterServiceImpl.semesterList.parallelStream().filter(v -> v.getStatus() > -1).sorted(Comparator.comparing(Semester::getId)).collect(Collectors.toList());
        int size = semesterList.size();
        int navigatePages = size % pageSize > 0 ? size / pageSize + 1 : size / pageSize;
        return PageInfo.of(semesterList, navigatePages);
    }

    @Override
    public void updateNow(String semesterId) {
        semesterMapper.updateNow(semesterId);
        semesterList = semesterMapper.selectAll();
    }





}
