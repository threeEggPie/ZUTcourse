package xyz.kingsword.course.controller;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.kingsword.course.annocations.Role;
import xyz.kingsword.course.enmu.RoleEnum;
import xyz.kingsword.course.pojo.Course;
import xyz.kingsword.course.pojo.Result;
import xyz.kingsword.course.pojo.param.CourseSelectParam;
import xyz.kingsword.course.service.CourseService;
import xyz.kingsword.course.vo.CourseVo;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/course")
@Api(tags = "课程操作类")
public class CourseController {

    @Resource
    private CourseService courseService;

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ApiOperation("新增课程")
    public Result<Object> insert(@RequestBody Course course) {
        courseService.insert(course);
        return Result.emptyResult();
    }

    @RequestMapping(value = "/setTeacherInCharge", method = RequestMethod.PUT)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "teacherId", required = true),
            @ApiImplicitParam(name = "courseId", required = true)}
    )
    @ApiOperation("设置课程负责人")
    public Result<Object> setTeacherInCharge(@NonNull String teacherId, @NonNull String courseId) {
        courseService.setTeacherInCharge(courseId, teacherId);
        return Result.emptyResult();
    }


    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation("更新课程，参数设置见新增课程")
    public Result<Object> update(@RequestBody Course course) {
        courseService.updateById(course);
        return Result.emptyResult();
    }

    @GetMapping("/courseInfo")
    @ApiOperation("获取课程信息")
    @ApiImplicitParam(name = "courseId", required = true)
    public Result<CourseVo> courseInfo(String courseId) {
        CourseVo courseVo = courseService.findCourseById(courseId);
        return new Result<>(courseVo);
    }

    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ApiOperation("多条件查询，自由组合")
    public Result<Object> select(@RequestBody CourseSelectParam param) {
        PageInfo<CourseVo> pageInfo = courseService.select(param);
        return new Result<>(pageInfo);
    }

    @RequestMapping(value = "/resetBookManager", method = RequestMethod.GET)
    @ApiOperation("清空教材管理权限")
    @Role(RoleEnum.TEACHER)
    public Result<Object> resetBookManager(String courseId) {
        courseService.resetBookManager(courseId);
        return Result.emptyResult();
    }

    @PutMapping("/import")
    @ApiOperation("文件上传")
    @Role
    @SneakyThrows(IOException.class)
    public Result<Object> importData(MultipartFile file) {
        courseService.importData(file.getInputStream());
        return Result.emptyResult();
    }

}
