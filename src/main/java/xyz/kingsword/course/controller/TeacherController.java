package xyz.kingsword.course.controller;


import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.kingsword.course.pojo.Result;
import xyz.kingsword.course.pojo.Teacher;
import xyz.kingsword.course.pojo.param.TeacherSelectParam;
import xyz.kingsword.course.service.TeacherService;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/teacher")
@Api(tags = "教师管理")
public class TeacherController {

    @Resource
    private TeacherService teacherService;


    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ApiOperation("查询")
    public Result select(@RequestBody TeacherSelectParam param) {
        PageInfo<Teacher> teacherPageInfo = teacherService.select(param);
        return new Result<>(teacherPageInfo);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ApiOperation("删除")
    public Result deleteTeacher(String id) {
        teacherService.delete(id);
        return new Result();
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ApiOperation("新增")
    public Result addTeacher(@RequestBody Teacher teacher) {
        teacherService.insert(teacher);
        return new Result();
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @ApiOperation("更新")
    public Result update(@RequestBody Teacher teacher) {
        teacherService.update(teacher);
        return new Result();
    }


    @RequestMapping(value = "/importData", method = RequestMethod.POST)
    @ApiOperation("批量导入")
    public Result importData(@NonNull MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        Workbook workbook = new HSSFWorkbook(inputStream);
        teacherService.insert(workbook);
        return new Result();
    }
}
