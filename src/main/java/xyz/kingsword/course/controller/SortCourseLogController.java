package xyz.kingsword.course.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import xyz.kingsword.course.pojo.Result;
import xyz.kingsword.course.pojo.SortCourseLog;
import xyz.kingsword.course.service.SortCourseLogService;

import java.util.List;

@Slf4j
@RestController
@Api(tags = "排课操作日志类")
public class SortCourseLogController {
    @Autowired
    SortCourseLogService sortCourseLogService;

    @RequestMapping(value = "/log",method = RequestMethod.POST)
    public Result<Object> addLog(int sortCourseLogId){

        return null;
    }
    @RequestMapping(value = "/log",method = RequestMethod.GET)
    @ApiOperation("获取某个课程的排课历史")
    @ApiImplicitParam(name = "sortCourseId", value = "排课id" ,required = true)
    public Result<Object> getLog(Integer sortCourseId){
        List<SortCourseLog> logs=sortCourseLogService.getLogs(sortCourseId);
        return new Result<>(logs);
    }
}
