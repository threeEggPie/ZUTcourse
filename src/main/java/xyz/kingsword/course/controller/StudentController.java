package xyz.kingsword.course.controller;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import xyz.kingsword.course.VO.StudentVo;
import xyz.kingsword.course.pojo.Result;
import xyz.kingsword.course.pojo.Student;
import xyz.kingsword.course.pojo.param.StudentSelectParam;
import xyz.kingsword.course.service.StudentService;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/student")
@Api(tags = "学生相关接口")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ApiOperation("查询")
    public Result select(@RequestBody StudentSelectParam param) {
        PageInfo<StudentVo> pageInfo = studentService.select(param);
        return new Result<>(pageInfo);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ApiOperation("删除")
    public Result deleteStudent(String id) {
        studentService.delete(id);
        return new Result();
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ApiOperation("新增")
    public Result insert(@RequestBody Student student) {
        studentService.insert(student);
        return new Result();
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @ApiOperation("更新")
    public Result update(@RequestBody Student student) {
        studentService.update(student);
        return new Result();
    }

    @RequestMapping(value = "/importData", method = RequestMethod.POST)
    @ApiOperation("批量导入")
    public Result importData(@NonNull MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        Workbook workbook = new XSSFWorkbook(inputStream);
        studentService.insert(workbook);
        return new Result();
    }
}
