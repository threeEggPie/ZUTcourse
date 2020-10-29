package xyz.kingsword.course.controller;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import xyz.kingsword.course.annocations.Role;
import xyz.kingsword.course.enmu.RoleEnum;
import xyz.kingsword.course.pojo.Result;
import xyz.kingsword.course.pojo.Semester;
import xyz.kingsword.course.service.SemesterService;
import xyz.kingsword.course.util.TimeUtil;

@Api(tags = "学期相关类")
@RestController
@RequestMapping("/semester")
public class SemesterController {

    @Autowired
    private SemesterService semesterService;


    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ApiOperation("新增学期")
    @Role(RoleEnum.ADMIN)
    public Result<Object> addSemester(@RequestBody Semester semester) {
        semesterService.addSemester(semester);
        return Result.emptyResult();
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @ApiOperation("仅修改开始时间结束时间")
    @Role(RoleEnum.ADMIN)
    public Result<Object> updateSemester(@RequestBody Semester semester) {
        semesterService.updateById(semester);
        return Result.emptyResult();
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation("获取全部学期")
    public Result<Object> getAllSemester() {
        PageInfo<Semester> list = semesterService.getAllSemester(1, 10);
        return new Result<>(list);
    }

    @RequestMapping(value = "/getFuture", method = RequestMethod.GET)
    @ApiOperation("获取当前以及未来学期")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", paramType = "query", dataType = "int", required = true),
            @ApiImplicitParam(name = "pageSize", paramType = "query", dataType = "int", required = true)
    })
    public Result<Object> getFutureSemester(int pageNum, int pageSize) {
        PageInfo<Semester> list = semesterService.getFutureSemester(pageNum, pageSize);
        return new Result<>(list);
    }
    @RequestMapping(value = "/updateNow",method = RequestMethod.PUT)
    @ApiOperation("修改某为当前学期")
    public Result<Object> updateNow(@RequestParam(required = true) String semesterId){
        semesterService.updateNow(semesterId);
        return Result.emptyResult();
    }
}
