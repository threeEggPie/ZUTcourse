package xyz.kingsword.course.controller;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.kingsword.course.vo.VerifyResult;
import xyz.kingsword.course.pojo.ExecutionPlan;
import xyz.kingsword.course.pojo.Result;
import xyz.kingsword.course.pojo.TrainingProgram;
import xyz.kingsword.course.pojo.param.ExecutionPlanSearchParam;
import xyz.kingsword.course.pojo.param.TrainingProgramSearchParam;
import xyz.kingsword.course.service.ExecutionPlanService;
import xyz.kingsword.course.service.ExecutionVerify;
import xyz.kingsword.course.service.TrainingProgramService;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/executePlan")
@Api(tags = "执行计划控制类")
public class ExecutePlanController {
    @Resource
    private ExecutionPlanService executionPlanService;
    @Resource
    private ExecutionVerify executionVerify;
    @Resource
    private TrainingProgramService trainingProgramService;

    /**
     * 通过上传执行计划检测与培养方案的不同
     */
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    @ApiOperation("插入")
    public Result importData(MultipartFile file, int grade, String semesterId, String specialityId) throws IOException {
        List<ExecutionPlan> executionPlanList = executionPlanService.importData(file.getInputStream());
        executionPlanList.parallelStream().forEach(v -> {
            v.setGrade(grade);
            v.setSemesterId(semesterId);
            v.setSpecialityId(specialityId);
        });
        executionPlanService.insert(executionPlanList);
        return new Result();
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ApiOperation("新增")
    public Result insert(@RequestBody ExecutionPlan executionPlan) {
        executionPlanService.insert(executionPlan);
        return new Result();
    }


    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ApiOperation("多条件查询,参数自由组合")
    public Result select(@RequestBody ExecutionPlanSearchParam param) {
        PageInfo<ExecutionPlan> pageInfo = executionPlanService.select(param);
        return new Result<>(pageInfo);
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @ApiOperation("更新")
    public Result update(@RequestBody ExecutionPlan executionPlan) {
        executionPlanService.update(executionPlan);
        return new Result();
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ApiOperation("删除")
    public Result delete(int id) {
        executionPlanService.delete(Collections.singletonList(id));
        return new Result();
    }

    @RequestMapping(value = "/verify", method = RequestMethod.GET)
    @ApiOperation(value = "认证，三个参数必填")
    public Result verify(int grade, String semesterId, String specialityId) {
        ExecutionPlanSearchParam executionPlanSearchParam = ExecutionPlanSearchParam.builder().grade(grade).semesterId(semesterId).specialityId(specialityId).build();
        TrainingProgramSearchParam trainingProgramSearchParam = TrainingProgramSearchParam.builder().grade(grade).semesterId(semesterId).specialityId(specialityId).build();
        List<ExecutionPlan> executionPlanList = executionPlanService.select(executionPlanSearchParam).getList();
        List<TrainingProgram> trainingProgramList = trainingProgramService.select(trainingProgramSearchParam).getList();
        VerifyResult verifyResult = executionVerify.verify(executionPlanList, trainingProgramList);
        return new Result<>(verifyResult);
    }
}
