package xyz.kingsword.course.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.*;
import xyz.kingsword.course.vo.BookOrderVo;
import xyz.kingsword.course.vo.CourseGroupOrderVo;
import xyz.kingsword.course.vo.StudentVo;
import xyz.kingsword.course.annocations.Role;
import xyz.kingsword.course.enmu.ErrorEnum;
import xyz.kingsword.course.enmu.RoleEnum;
import xyz.kingsword.course.exception.AuthException;
import xyz.kingsword.course.exception.ParameterException;
import xyz.kingsword.course.pojo.BookOrder;
import xyz.kingsword.course.pojo.Result;
import xyz.kingsword.course.pojo.User;
import xyz.kingsword.course.pojo.param.*;
import xyz.kingsword.course.service.BookOrderService;
import xyz.kingsword.course.util.ConditionUtil;
import xyz.kingsword.course.util.TimeUtil;
import xyz.kingsword.course.util.UserUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Slf4j
@Api(tags = "学生订书记录")
@RequestMapping("/bookOrder")
@RestController
public class BookOrderController {
    @Resource
    private BookOrderService bookOrderService;

    private static final String EXCEL_CONTENT_TYPE = "application/msexcel;charset=UTF-8";
    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final String SET_FILENAME = "attachment;filename=";


    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ApiOperation("新增订书记录")
    @Role
    public Result<Object> insert(@RequestBody List<BookOrder> bookOrderList) {
        User user = UserUtil.getUser();
        bookOrderList.parallelStream().forEach(v -> v.setUserId(user.getUsername()));
        List<Integer> idList = bookOrderService.insert(bookOrderList);
        if (user.getCurrentRole() != RoleEnum.STUDENT.getCode()) {
            bookOrderService.forTeacherIncrease(idList);
        }
        return new Result<>(idList);
    }

    @RequestMapping(value = "/insertByGrade", method = RequestMethod.POST)
    @ApiOperation("根据年级，订购必修教材")
    @Role
    public Result<Object> insertByGrade(@RequestBody List<Integer> gradeList) {
        String nextSemesterId = TimeUtil.getNextSemester().getId();
        bookOrderService.insertByGrade(gradeList, nextSemesterId);
        return Result.emptyResult();
    }

    @RequestMapping(value = "/cancelPurchase", method = RequestMethod.GET)
    @ApiOperation("取消订教材")
    @Role
    public Result<Object> cancelPurchase(int id) {
        bookOrderService.cancelPurchase(id);
        return Result.emptyResult();
    }

    @RequestMapping(value = "/courseGroupOrder", method = RequestMethod.GET)
    @ApiOperation("课程组教材订阅情况")
    @Role
    public Result<Object> courseGroupOrder(String courseId, String semesterId) {
        List<CourseGroupOrderVo> courseGroupOrderVoList = bookOrderService.courseGroupOrder(courseId, semesterId);
        return new Result<>(courseGroupOrderVoList);
    }

    @RequestMapping(value = "/getStudentOrder", method = RequestMethod.GET)
    @ApiOperation("获取学生订书记录")
    @Role({RoleEnum.STUDENT})
    public Result<Object> getStudentOrder(String semesterId) {
        StudentVo studentVo = Optional.ofNullable(UserUtil.getStudent()).orElseThrow(AuthException::new);
        List<BookOrderVo> bookOrderVoList = bookOrderService.select(BookOrderSelectParam.builder().semesterId(semesterId).userId(studentVo.getId()).build());
        BigDecimal sum = bookOrderVoList.parallelStream().map(v -> BigDecimal.valueOf(v.getPrice())).reduce(BigDecimal::add).orElse(new BigDecimal(0));
        Dict dict = Dict.create()
                .set("bookList", bookOrderVoList)
                .set("sum", sum.toString());
        return new Result<>(dict);
    }

    /**
     * 统计每个教材订购数量
     *
     * @param semesterId semesterId
     */
    @RequestMapping(value = "/exportBookOrderStatistics", method = RequestMethod.GET)
    public void exportBookOrderStatistics(String semesterId, HttpServletResponse response) throws IOException {
        Workbook workbook = bookOrderService.exportBookOrderStatistics(semesterId);
        String fileName = TimeUtil.getSemesterName(semesterId) + "教材征订统计表" + excelPostfix(workbook);
        fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        response.setContentType(EXCEL_CONTENT_TYPE);
        response.setHeader(CONTENT_DISPOSITION, SET_FILENAME + fileName);
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    /**
     * 班级教材订购信息导出
     *
     * @param semesterId 学期id
     */
    @RequestMapping(value = "/exportClassBookInfo", method = RequestMethod.GET)
    @ApiOperation("班级教材订购信息导出")
    @Role
    public void exportClassBookInfo(HttpServletResponse response, String className, String semesterId) throws IOException {
        Workbook workbook = bookOrderService.exportClassRecord(className, semesterId);
        String fileName = className + "-" + TimeUtil.getSemesterName(semesterId) + "教材征订计划表" + excelPostfix(workbook);
        fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        response.setContentType(EXCEL_CONTENT_TYPE);
        response.setHeader(CONTENT_DISPOSITION, SET_FILENAME + fileName);
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    /**
     * 多个班级教材订购信息导出
     *
     * @param param 班级列表及学期id
     */
    @RequestMapping(value = "/exportPluralClassBookInfo", method = RequestMethod.GET)
    @ApiOperation("多个班级教材订购信息导出")
    @Role
    public void exportPluralClassBookInfo(HttpServletResponse response, ExportPluralClassBookParam param) throws IOException {
        ConditionUtil.validateTrue(CollUtil.isNotEmpty(param.getClassNameList()) && StrUtil.isNotEmpty(param.getSemesterId()))
                .orElseThrow(() -> new ParameterException(ErrorEnum.ERROR_PARAMETER));
        byte[] bytes = bookOrderService.exportPluralClassBookInfo(param.getClassNameList(), param.getSemesterId());
        String fileName = TimeUtil.getSemesterName(param.getSemesterId()) + "-各班级教材征订计划表.zip";
        fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        response.setContentType(EXCEL_CONTENT_TYPE);
        response.setHeader(CONTENT_DISPOSITION, SET_FILENAME + fileName);
        OutputStream outputStream = response.getOutputStream();
        IoUtil.write(outputStream, true, bytes);
    }


    /**
     * 全部教材订购信息导出
     *
     * @param param 导出筛选条件
     */
    @RequestMapping(value = "/exportBookInfo", method = RequestMethod.GET)
    @ApiOperation("教材订购信息导出")
    @Role
    public void exportBookInfo(HttpServletResponse response, DeclareBookExportParam param) throws IOException {
        Workbook workbook = bookOrderService.exportAllStudentRecord(param);
        String fileName = TimeUtil.getSemesterName(param.getSemesterId()) + "教材征订计划表" + excelPostfix(workbook);
        fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        response.setContentType(EXCEL_CONTENT_TYPE);
        response.setHeader(CONTENT_DISPOSITION, SET_FILENAME + fileName);
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping(value = "/exportAllBookBill")
    @ApiOperation("导出教材结算表")
    public void exportGradeBookAccount(HttpServletResponse response, int grade, int degree) throws IOException {
        Workbook workbook = bookOrderService.exportBookSettlement(grade, degree);
        String fileName = TimeUtil.getGradeName(grade, degree) + "教材结算" + excelPostfix(workbook);
        fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        response.setContentType(EXCEL_CONTENT_TYPE);
        response.setHeader(CONTENT_DISPOSITION, SET_FILENAME + fileName);
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("/outBound")
    @ApiOperation("出库单导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "grade", required = true, value = "年级", defaultValue = "2017"),
            @ApiImplicitParam(name = "semesterId", required = true, value = "学期", defaultValue = "19202"),
            @ApiImplicitParam(name = "degree", required = true, value = "学历筛选 0全部，1本科 2专科")
    }
    )
    public void outBound(HttpServletResponse response, int grade, String semesterId, int degree) throws IOException {
        Workbook workbook = bookOrderService.exportOutBoundData(grade, semesterId, degree);
        String fileName = semesterId + TimeUtil.getGradeName(grade, degree) + "出库单" + excelPostfix(workbook);
        fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        response.setContentType(EXCEL_CONTENT_TYPE);
        response.setHeader(CONTENT_DISPOSITION, SET_FILENAME + fileName);
        workbook.write(response.getOutputStream());
        workbook.close();
    }


    @RequestMapping(value = "/setSemesterDiscount", method = RequestMethod.POST)
    @ApiOperation("设置学期书费折扣值")
    public Result<Object> setSemesterDiscount(String semester, double discount) {
        bookOrderService.setSemesterDiscount(semester, NumberUtil.round(discount, 2).doubleValue());
        return Result.emptyResult();
    }

    @RequestMapping(value = "/getSemesterDiscount", method = RequestMethod.GET)
    @ApiOperation("根据学期获取书费折扣值")
    public Result<Double> getSemesterDiscount(String semester) {
        Double discount = bookOrderService.getDiscountBySemester(semester);
        return new Result<>(discount);
    }

    @RequestMapping(value = "/getSemesterDiscountList", method = RequestMethod.GET)
    @ApiOperation("获取所有学期与折扣")
    public Result<Object> getSemesterDiscountList() {
        return new Result<>(bookOrderService.getDiscountBySemesterList());
    }

    private String excelPostfix(Workbook workbook) {
        if (workbook instanceof HSSFWorkbook) {
            return ".xls";
        }
        return ".xlsx";
    }
}
