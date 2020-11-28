package xyz.kingsword.course.service.impl;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import xyz.kingsword.course.dao.CourseMapper;
import xyz.kingsword.course.enmu.AssessmentEnum;
import xyz.kingsword.course.enmu.CourseNature;
import xyz.kingsword.course.enmu.CourseTypeEnum;
import xyz.kingsword.course.exception.DataException;
import xyz.kingsword.course.exception.OperationException;
import xyz.kingsword.course.pojo.Course;
import xyz.kingsword.course.pojo.Teacher;
import xyz.kingsword.course.pojo.param.CourseSelectParam;
import xyz.kingsword.course.service.BookService;
import xyz.kingsword.course.service.CourseService;
import xyz.kingsword.course.service.TeacherService;
import xyz.kingsword.course.util.ConditionUtil;
import xyz.kingsword.course.vo.CourseVo;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static xyz.kingsword.course.enmu.ErrorEnum.REPEATED_ID;

@Slf4j
@Service
public class CourseServiceImpl implements CourseService {

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private BookService bookService;
    @Resource
    private TeacherService teacherService;

    @Override
    public void insert(Course course) {
        int count = courseMapper.courseRepeated(course.getId());
        ConditionUtil.validateTrue(count == 0).orElseThrow(() -> new OperationException(REPEATED_ID));
        courseMapper.insert(course);
    }

    @Override
    public PageInfo<CourseVo> select(CourseSelectParam param) {
        PageInfo<Course> pageInfo = PageMethod.startPage(param.getPageNum(), param.getPageSize()).doSelectPageInfo(() -> courseMapper.select(param));
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
    @SneakyThrows(IOException.class)
    public void importData(InputStream inputStream) {
        Workbook workbook;
        try {
//            HSSFWorkbook:是操作Excel2003以前（包括2003）的版本，扩展名是.xls
//            XSSFWorkbook:是操作Excel2007以后（包括2007）的版本，扩展名是.xlsx
            workbook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            log.error("课程信息导入失败，excel无法打开");
            throw new OperationException("课程信息导入失败，excel无法打开");
        }
        Sheet sheet = workbook.getSheetAt(0);
        int rowRum = sheet.getLastRowNum();
        List<Course> courseList = new ArrayList<>(rowRum);
        int i = 0;
        try {
            for (i = 1; i < rowRum; i++) {
                Row row = sheet.getRow(i);
                Course course = new Course();
                course.setId(row.getCell(1).getStringCellValue());
                course.setName(row.getCell(2).getStringCellValue());
                String[] elements = (row.getCell(3).getStringCellValue()).split("/");
                course.setTimeWeek(Integer.parseInt(elements[0]));
                course.setWeekNum(Integer.parseInt(elements[1]));
                course.setTimeAll((int) row.getCell(4).getNumericCellValue());
                course.setCredit((int) row.getCell(5).getNumericCellValue());
                course.setType(CourseTypeEnum.get(row.getCell(6).getStringCellValue()).getCode());
                course.setNature(CourseNature.getContent(row.getCell(7).getStringCellValue()).getCode());
                course.setExaminationWay(row.getCell(8).getStringCellValue());
                course.setTimeTheory((int) row.getCell(9).getNumericCellValue());
                course.setTimeLab((int) row.getCell(10).getNumericCellValue());
                courseList.add(course);
            }
            courseMapper.importData(courseList);
        } catch (Exception e) {
            log.error("课程信息导入错误,错误下标{}", i);
            log.error(ExceptionUtil.stacktraceToString(e));
            throw new OperationException("课程信息导入错误,错误下标" + i);
        } finally {
            inputStream.close();
        }
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
                courseVo.setType(course.getType());
                courseVo.setAssessmentWay(AssessmentEnum.getContent(course.getAssessmentWay()).getContent());
                courseVo.setBookList(bookService.getTextBook(course.getId()));
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