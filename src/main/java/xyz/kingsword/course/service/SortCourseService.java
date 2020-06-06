package xyz.kingsword.course.service;

import com.github.pagehelper.PageInfo;
import org.apache.poi.ss.usermodel.Workbook;
import xyz.kingsword.course.vo.SortCourseVo;
import xyz.kingsword.course.pojo.SortCourse;
import xyz.kingsword.course.pojo.param.SortCourseSearchParam;
import xyz.kingsword.course.pojo.param.SortCourseUpdateParam;

import java.io.InputStream;
import java.util.List;

public interface SortCourseService {

    void insertSortCourseList(List<SortCourse> sortCourseList);

    void setSortCourse(SortCourseUpdateParam sortCourseUpdateParam);

    void setClasses(List<String> classNameList, int sortId);


    void deleteSortCourseRecord(List<Integer> id);

    List<SortCourseVo> getCourseHistory(String courseId);

    List<SortCourseVo> getTeacherHistory(String courseId);

    PageInfo<SortCourseVo> search(SortCourseSearchParam param);

    void mergeCourseHead(List<Integer> id);

    void restoreCourseHead(List<Integer> id);

    List<SortCourse> excelImport(InputStream inputStream);

    Workbook excelExport(String semesterId);
}
