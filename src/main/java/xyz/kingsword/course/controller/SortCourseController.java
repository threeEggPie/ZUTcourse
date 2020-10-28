package xyz.kingsword.course.controller;

import cn.hutool.core.exceptions.ValidateException;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.kingsword.course.annocations.Role;
import xyz.kingsword.course.enmu.RoleEnum;
import xyz.kingsword.course.exception.BaseException;
import xyz.kingsword.course.pojo.Result;
import xyz.kingsword.course.pojo.SortCourse;
import xyz.kingsword.course.pojo.User;
import xyz.kingsword.course.pojo.param.SortCourseSearchParam;
import xyz.kingsword.course.pojo.param.SortCourseUpdateParam;
import xyz.kingsword.course.service.SortCourseService;
import xyz.kingsword.course.util.ConditionUtil;
import xyz.kingsword.course.util.TimeUtil;
import xyz.kingsword.course.vo.SortCourseVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@Api(tags = "排课操作类")
public class SortCourseController {

    @Autowired
    private SortCourseService sortCourseService;

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ApiOperation(value = "排课列表搜索接口")
    @ApiImplicitParam(name = "searchParam", value = "参数自由组合", required = true)
    public Result<Object> search(@RequestBody SortCourseSearchParam sortCourseSearchParam) {
        PageInfo<SortCourseVo> pageInfo = sortCourseService.search(sortCourseSearchParam);
        return new Result<>(pageInfo);
    }

    @RequestMapping(value = "/courseHistory", method = RequestMethod.GET)
    @ApiOperation(value = "课程教授记录")
    @ApiImplicitParam(name = "courseId", required = true)
    public Result<Object> courseHistory(String courseId) {
        List<SortCourseVo> sortCourseVoList = sortCourseService.getCourseHistory(courseId);
        return new Result<>(sortCourseVoList);
    }


    @ApiOperation(value = "教师教授记录")
    @ApiImplicitParam(name = "teacherId", value = "教师id", paramType = "query", required = true, dataType = "String")
    @RequestMapping(value = "/teacherHistory", method = RequestMethod.GET)
    public Result<Object> teacherHistory(String teacherId) {
        List<SortCourseVo> sortCourseVoList = sortCourseService.getTeacherHistory(teacherId);
        return new Result<>(sortCourseVoList);
    }


    @Role({RoleEnum.ADMIN, RoleEnum.SPECIALTY_MANAGER})
    @ApiOperation(value = "批量删除排课数据")
    @RequestMapping(value = "/deleteSortCourseById", method = RequestMethod.PUT)
    public Result<Object> deleteCourseInfo(@RequestBody List<Integer> id) {
        sortCourseService.deleteSortCourseRecord(id);
        return Result.emptyResult();
    }


    @RequestMapping(value = "/setSortCourse", method = RequestMethod.PUT)
    @ApiOperation(value = "给课程设置老师，或者给老师设置课程")
    @ApiImplicitParam(value = "id为排课数据id必传，其他任给一个", required = true)
    public Result<Object> setSortCourse(@RequestBody SortCourseUpdateParam param, HttpSession session) {
        sortCourseService.setSortCourse(param,session);
        return Result.emptyResult();
    }

    @PostMapping(value = "/setClasses")
    @ApiOperation(value = "排课安排班级")
    public Result<Object> setClasses(@RequestBody List<String> classNameList, int sortId) {
        sortCourseService.setClasses(classNameList, sortId);
        return Result.emptyResult();
    }


    @RequestMapping(value = "/mergeCourseHead", method = RequestMethod.PUT)
    @ApiOperation(value = "课头合并")
    public Result<Object> merge(@RequestBody List<Integer> idList) {
        sortCourseService.mergeCourseHead(idList);
        return Result.emptyResult();
    }

    @RequestMapping(value = "/restoreCourseHead", method = RequestMethod.PUT)
    @ApiOperation(value = "课头重置")
    public Result<Object> restoreCourseHead(@RequestBody List<Integer> idList) {
        sortCourseService.restoreCourseHead(idList);
        return Result.emptyResult();
    }

    /**
     * 教学任务导出
     */
    @RequestMapping(value = "/sortCourseExport", method = RequestMethod.GET)
    @ApiOperation(value = "教学任务导出")
    public void export(HttpServletResponse response, String semesterId) throws IOException {
        Workbook workbook = sortCourseService.excelExport(semesterId);
        String fileName = TimeUtil.getSemesterName(semesterId) + "教学任务.xls";
        fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        response.setContentType("application/msexcel;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        workbook.write(response.getOutputStream());

        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    @RequestMapping(value = "/sortCourseImport", method = RequestMethod.POST)
    @ApiOperation(value = "排课数据导入")
    public Result<Object> sortCourseImport(MultipartFile file) throws IOException {
        file = Optional.ofNullable(file).orElseThrow(() -> new BaseException("文件上传错误"));
        ConditionUtil.validateTrue(!file.isEmpty()).orElseThrow(() -> new ValidateException("文件上传错误"));
        sortCourseService.excelImport(file.getInputStream());
        return Result.emptyResult();
    }
}
