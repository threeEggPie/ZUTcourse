package xyz.kingsword.course.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xyz.kingsword.course.vo.VerifyField;
import xyz.kingsword.course.vo.VerifyResult;
import xyz.kingsword.course.enmu.ErrorEnum;
import xyz.kingsword.course.exception.BaseException;
import xyz.kingsword.course.exception.DataException;
import xyz.kingsword.course.pojo.ExecutionPlan;
import xyz.kingsword.course.pojo.TrainingProgram;
import xyz.kingsword.course.service.ExecutionVerify;
import xyz.kingsword.course.util.ConditionUtil;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExecutionVerifyImpl implements ExecutionVerify {
    /**
     * 验证培养方案与执行计划的不同
     *
     * @param executionPlanList   executionPlanList
     * @param trainingProgramList trainingProgramList
     */
    @Override
    public VerifyResult verify(List<ExecutionPlan> executionPlanList, List<TrainingProgram> trainingProgramList) {
        ConditionUtil.validateTrue(!trainingProgramList.isEmpty()).orElseThrow(() -> new DataException(ErrorEnum.TRAINING_PROGRAM_ERROR));
        ConditionUtil.validateTrue(!executionPlanList.isEmpty()).orElseThrow(() -> new DataException(ErrorEnum.EXECUTION_PLAN_ERROR));
//        预处理
        preprocess(trainingProgramList, executionPlanList);
//        按课程号排序
        trainingProgramList = trainingProgramList.parallelStream().sorted(Comparator.comparing(TrainingProgram::getCourseId)).collect(Collectors.toList());
        executionPlanList = executionPlanList.parallelStream().sorted(Comparator.comparing(ExecutionPlan::getCourseId)).collect(Collectors.toList());

        ConditionUtil.validateTrue(trainingProgramList.size() == executionPlanList.size()).orElseThrow(() -> new BaseException(ErrorEnum.ERROR));
        int len = executionPlanList.size();
        List<Map<String, List<VerifyField>>> trainingProgramDifferenceList = new ArrayList<>(len);
        List<Map<String, List<VerifyField>>> executionPlanDifferenceList = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            Map<String, Map<String, List<VerifyField>>> map = verifySingleCourse(executionPlanList.get(i), trainingProgramList.get(i));
            trainingProgramDifferenceList.add(map.get("trainingProgram"));
            executionPlanDifferenceList.add(map.get("executionPlan"));
        }
        return VerifyResult.builder().executionResult(executionPlanDifferenceList).trainingProgramResult(trainingProgramDifferenceList).build();
    }

    /**
     * 预处理，解决执行计划和培养方案课程数量，种类不同的问题
     * 处理后的两个list课程数量及种类相同，并按课程号从小到大排序
     *
     * @param trainingProgramList 培养方案list
     * @param executionPlanList   执行计划list
     */
    private void preprocess(List<TrainingProgram> trainingProgramList, List<ExecutionPlan> executionPlanList) {
//        将课程号和课程id转换为set，便于集合做差
        Set<String> trainingProgramSet = trainingProgramList.parallelStream().map(TrainingProgram::getCourseId).collect(Collectors.toSet());
        Set<String> executionPlanSet = executionPlanList.parallelStream().map(ExecutionPlan::getCourseId).collect(Collectors.toSet());
//        trainingProgramSet剩余的是培养方案里面有，但执行计划里面没有的课程id
        trainingProgramSet.removeAll(executionPlanSet);
//        executionPlanSet剩余的是执行计划里面有，但培养方案里面没有的课程id
        executionPlanSet.removeAll(trainingProgramSet);
        executionPlanSet.forEach(v -> trainingProgramList.add(TrainingProgram.builder().courseId(v).build()));
        trainingProgramSet.forEach(v -> executionPlanList.add(ExecutionPlan.builder().courseId(v).build()));
    }

    /**
     * @param e 执行计划
     * @param t 培养方案
     * @return 同课程各属性验证情况
     */
    private Map<String, Map<String, List<VerifyField>>> verifySingleCourse(ExecutionPlan e, TrainingProgram t) {
        log.debug("执行计划,{}", e);
        log.debug("培养方案,{}", t);
//        对是否能进行比较进行验证
        boolean sameCourse = StrUtil.equals(t.getCourseId(), e.getCourseId());
        ConditionUtil.validateTrue(sameCourse).orElseThrow(() -> new DataException(ErrorEnum.VERIFY_ERROR));


//      开始比较
        boolean courseNameFlag = Objects.equals(t.getCourseName(), e.getCourseName());
        boolean creditFlag = Objects.equals(t.getCredit(), e.getCredit());
        boolean examinationWayFlag = Objects.equals(t.getExaminationWay(), e.getExaminationWay());
        boolean timeAllFlag = Objects.equals(t.getTimeAll(), e.getTimeAll());
        boolean timeTheoryFlag = Objects.equals(t.getTimeTheory(), e.getTimeTheory());
        boolean timeLabFlag = Objects.equals(t.getTimeLab(), e.getTimeLab());
        boolean timeComputerFlag = Objects.equals(t.getTimeComputer(), e.getTimeComputer());
        boolean timeOtherFlag = Objects.equals(t.getTimeOther(), e.getTimeOther());
        boolean startSemesterFlag = Objects.equals(t.getStartSemester(), e.getStartSemester());


//      构建结果
        List<VerifyField> tVerifyFieldList = new ArrayList<>(12);
        tVerifyFieldList.add(new VerifyField("courseName", t.getCourseName(), courseNameFlag));
        tVerifyFieldList.add(new VerifyField("credit", String.valueOf(t.getCredit()), creditFlag));
        tVerifyFieldList.add(new VerifyField("examinationWay", t.getExaminationWay(), examinationWayFlag));
        tVerifyFieldList.add(new VerifyField("timeAll", String.valueOf(t.getTimeAll()), timeAllFlag));
        tVerifyFieldList.add(new VerifyField("timeTheory", String.valueOf(t.getTimeAll()), timeTheoryFlag));
        tVerifyFieldList.add(new VerifyField("timeLab", String.valueOf(t.getTimeLab()), timeLabFlag));
        tVerifyFieldList.add(new VerifyField("timeComputer", String.valueOf(t.getTimeComputer()), timeComputerFlag));
        tVerifyFieldList.add(new VerifyField("timeOther", String.valueOf(t.getTimeOther()), timeOtherFlag));
        tVerifyFieldList.add(new VerifyField("startSemester", String.valueOf(t.getStartSemester()), startSemesterFlag));

        List<VerifyField> eVerifyFieldList = new ArrayList<>(12);
        eVerifyFieldList.add(new VerifyField("courseName", e.getCourseName(), courseNameFlag));
        eVerifyFieldList.add(new VerifyField("credit", String.valueOf(e.getCredit()), creditFlag));
        eVerifyFieldList.add(new VerifyField("examinationWay", e.getExaminationWay(), examinationWayFlag));
        eVerifyFieldList.add(new VerifyField("timeAll", String.valueOf(e.getTimeAll()), timeAllFlag));
        eVerifyFieldList.add(new VerifyField("timeTheory", String.valueOf(e.getTimeAll()), timeTheoryFlag));
        eVerifyFieldList.add(new VerifyField("timeLab", String.valueOf(e.getTimeLab()), timeLabFlag));
        eVerifyFieldList.add(new VerifyField("timeComputer", String.valueOf(e.getTimeComputer()), timeComputerFlag));
        eVerifyFieldList.add(new VerifyField("timeOther", String.valueOf(e.getTimeOther()), timeOtherFlag));
        eVerifyFieldList.add(new VerifyField("startSemester", String.valueOf(e.getStartSemester()), startSemesterFlag));

        Map<String, Map<String, List<VerifyField>>> map = new HashMap<>();
        map.put("trainingProgram", MapUtil.builder(t.getCourseId(), tVerifyFieldList).build());
        map.put("executionPlan", MapUtil.builder(e.getCourseId(), eVerifyFieldList).build());

        return map;
    }
}
