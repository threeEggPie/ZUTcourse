package xyz.kingsword.course.service;

import com.github.pagehelper.PageInfo;
import org.apache.poi.ss.usermodel.Workbook;
import xyz.kingsword.course.pojo.BookOrder;
import xyz.kingsword.course.pojo.param.BookOrderSelectParam;
import xyz.kingsword.course.pojo.param.DeclareBookExportParam;
import xyz.kingsword.course.vo.BookOrderVo;
import xyz.kingsword.course.vo.CourseGroupOrderVo;
import xyz.kingsword.course.vo.SemesterDiscountVo;

import java.util.Collection;
import java.util.List;

public interface BookOrderService {
    List<Integer> insert(List<BookOrder> bookOrderList);

    void forTeacherIncrease(Collection<Integer> bookIdList);

    void cancelPurchase(int id);

    void deleteByBook(List<Integer> bookIdList);

    int selectByBookIdSemester(List<Integer> bookIdList, String semesterId);

    void insertByGrade(Collection<Integer> gradeList, String semesterId);

    List<BookOrderVo> select(BookOrderSelectParam param);

    List<CourseGroupOrderVo> courseGroupOrder(String courseId, String semesterId);

    Workbook exportAllStudentRecord(DeclareBookExportParam param);

    Workbook exportClassRecord(String className, String semesterId);

    byte[] exportPluralClassBookInfo(List<String> className, String semesterId);

    Workbook exportBookOrderStatistics(String semesterId);

    /**
     * 出库单导出
     *
     * @param grade      2017
     * @param semesterId 19002
     * @param degree     1
     * @return workbook
     */
    Workbook exportOutBoundData(int grade, String semesterId, int degree);

    Workbook exportBookSettlement(int grade, int degree);

    void setSemesterDiscount(String semester, Double discount);

    Double getDiscountBySemester(String semester);

    List<SemesterDiscountVo> getDiscountBySemesterList();
}
