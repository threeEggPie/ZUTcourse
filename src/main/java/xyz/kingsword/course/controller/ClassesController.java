package xyz.kingsword.course.controller;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.kingsword.course.pojo.Classes;
import xyz.kingsword.course.pojo.Result;
import xyz.kingsword.course.pojo.param.ClassesSelectParam;
import xyz.kingsword.course.service.ClassesService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/classes")
@Api(tags = "班级接口")
public class ClassesController {

    @Autowired
    private ClassesService classesService;


    @PostMapping("/insert")
    @ApiOperation("新增")
    public Result insert(List<Classes> classesList) {
        classesService.insert(classesList);
        return new Result();
    }

    @PostMapping("/update")
    @ApiOperation("修改")
    public Result<Object> update(Classes Classes) {
        classesService.update(Classes);
        return Result.emptyResult();
    }

    @PostMapping("/select")
    @ApiOperation("条件分页查询")
    public Result<Object> select(@RequestBody ClassesSelectParam param) {
        PageInfo<Classes> pageInfo = classesService.select(param);
        return new Result<>(pageInfo);
    }

    @PostMapping("/selectAll")
    @ApiOperation("查询全部，按年级分类")
    public Result<Object> selectAll() {
        List<Classes> classesList = classesService.select(ClassesSelectParam.builder().pageSize(0).build()).getList();
        Map<Integer, List<Classes>> map = classesList.parallelStream().collect(Collectors.groupingBy(Classes::getGrade));
        return new Result<>(map);
    }

    @GetMapping("/selectGrades")
    @ApiOperation("获取在校年级")
    public Result<List<Integer>> selectGrades() {
        return new Result<>(Arrays.asList(2016, 2017, 2018, 2019, 2020));
    }
}
