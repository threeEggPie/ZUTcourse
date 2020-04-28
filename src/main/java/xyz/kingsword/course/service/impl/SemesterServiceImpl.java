package xyz.kingsword.course.service.impl;

import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.kingsword.course.dao.SemesterMapper;
import xyz.kingsword.course.pojo.Semester;
import xyz.kingsword.course.service.SemesterService;
import xyz.kingsword.course.util.TimeUtil;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SemesterServiceImpl implements SemesterService {

    @Resource
    private SemesterMapper semesterMapper;

    private List<Semester> semesterList;

    @PostConstruct
    public void init() {
        semesterList = semesterMapper.selectAll();
    }


    @Override
    @Transactional
    public void addSemester(Semester semester) {
        semester.setName(TimeUtil.getSemesterName(semester.getId()));
        semesterMapper.insert(semester);
        semesterList.add(semester);
    }


    @Override
    @Transactional
    public void updateById(Semester semester) {
        semesterMapper.updateById(semester);
        semesterList.removeIf(v -> v.getId().equals(semester.getId()));
        semesterList.add(semester);
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
        List<Semester> semesterList = this.semesterList.parallelStream().filter(v -> v.getStatus() > -1).sorted(Comparator.comparing(Semester::getId)).collect(Collectors.toList());
        int size = semesterList.size();
        int navigatePages = size % pageSize > 0 ? size / pageSize + 1 : size / pageSize;
        return PageInfo.of(semesterList, navigatePages);
    }
}
