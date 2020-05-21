package xyz.kingsword.course.service;

import org.apache.poi.ss.usermodel.Workbook;
import xyz.kingsword.course.VO.BookOrderVo;
import xyz.kingsword.course.VO.CourseGroupOrderVo;
import xyz.kingsword.course.pojo.BookOrder;
import xyz.kingsword.course.pojo.param.BookOrderSelectParam;
import xyz.kingsword.course.pojo.param.DeclareBookExportParam;
import xyz.kingsword.course.pojo.param.ExportGradeBookAccountParam;
import xyz.kingsword.course.pojo.param.ExportGradeBookParam;

import java.util.Collection;
import java.util.List;

public interface BookOrderService {
    List<Integer> insert(List<BookOrder> bookOrderList);

    void forTeacherIncrease(Collection<Integer> bookIdList);

    void cancelPurchase(int id);

    void insertByGrade(Collection<Integer> gradeList, String semesterId);

    List<BookOrderVo> select(BookOrderSelectParam param);

    List<CourseGroupOrderVo> courseGroupOrder(String courseId, String semesterId);

    Workbook exportAllStudentRecord(DeclareBookExportParam param);

    Workbook exportSingleRecord(String studentId);

    Workbook exportClassRecord(String className, String semesterId);

    byte[] exportPluralClassBookInfo(List<String> className, String semesterId);

    Workbook exportBookOrderStatistics(String semesterId);


    Workbook exportGradeOrder(ExportGradeBookParam param);

    /**
     * 导出年级订购教程学生结算
     * @param param 参数类
     * @return excel表格
     */
    Workbook getGradeBookAccount(ExportGradeBookAccountParam param);

    void setSemesterDiscount(String semester, Double discount);

    Double getDiscountBySemester(String semester);
}
