package xyz.kingsword.course.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import xyz.kingsword.course.VO.CourseVo;
import xyz.kingsword.course.dao.CourseGroupMapper;
import xyz.kingsword.course.dao.CourseMapper;
import xyz.kingsword.course.enmu.AssessmentEnum;
import xyz.kingsword.course.exception.DataException;
import xyz.kingsword.course.pojo.Course;
import xyz.kingsword.course.pojo.CourseGroup;
import xyz.kingsword.course.pojo.Teacher;
import xyz.kingsword.course.pojo.param.CourseSelectParam;
import xyz.kingsword.course.service.BookService;
import xyz.kingsword.course.service.CourseService;
import xyz.kingsword.course.service.TeacherService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CourseServiceImpl implements CourseService {

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private BookService bookService;
    @Resource
    private TeacherService teacherService;
    @Resource
    private CourseGroupMapper courseGroupMapper;

    @Override
    public void insert(Course course) {
        courseMapper.insert(course);
    }

    @Override
    public PageInfo<CourseVo> select(CourseSelectParam param) {
        PageInfo<Course> pageInfo = PageHelper.startPage(param.getPageNum(), param.getPageSize()).doSelectPageInfo(() -> courseMapper.select(param));
        List<Course> courseList = pageInfo.getList();
        PageInfo<CourseVo> courseVoPageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, courseVoPageInfo);
        List<CourseVo> courseVoList = renderCourseVo(courseList);
        courseVoPageInfo.setList(courseVoList);
        return courseVoPageInfo;
    }

    @Override
    public void resetBookManager(String courseId) {
        courseMapper.resetBookManager(courseId);
    }

    @Override
    public CourseVo findCourseById(String id) {
        Optional<Course> optional = courseMapper.getByPrimaryKey(id);
        CourseVo courseVo = null;
        if (optional.isPresent()) {
            courseVo = renderCourseVo(Collections.singletonList(optional.get())).get(0);
        }
        return courseVo;
    }

    @Override
    public int deleteCourse(List<String> list) {
        return courseMapper.deleteCourse(list);
    }

    @Override
    public int updateById(Course course) {
        return courseMapper.updateByPrimaryKey(course);
    }

    @Override
    public void setTeacherInCharge(String id, String teaId) {
        courseMapper.setTeacherInCharge(id, teaId);
    }

    private List<CourseVo> renderCourseVo(List<Course> courseList) {
        List<CourseVo> courseVoList = new ArrayList<>();
        if (courseList != null && !courseList.isEmpty()) {
            for (Course course : courseList) {
                CourseVo courseVo = new CourseVo();
                BeanUtils.copyProperties(course, courseVo);
                List<String> courseGroupList = courseGroupMapper.getNextSemesterCourseGroup(course.getId()).parallelStream().map(CourseGroup::getTeacherName).collect(Collectors.toList());
                courseVo.setType(course.getType());
                courseVo.setAssessmentWay(AssessmentEnum.getContent(course.getAssessmentWay()).getContent());
                courseVo.setBookList(bookService.getByIdList(course.getTextBook()));
                courseVo.setCourseGroup(courseGroupList);
                if (courseVo.getBookManager() != null) {
                    Teacher bookManager = Optional.ofNullable(teacherService.getTeacherById(courseVo.getBookManager())).orElseThrow(DataException::new);
                    courseVo.setBookManagerId(course.getBookManager());
                    courseVo.setBookManager(bookManager.getName());
                }
                courseVoList.add(courseVo);
            }
        }
        return courseVoList;
    }
}