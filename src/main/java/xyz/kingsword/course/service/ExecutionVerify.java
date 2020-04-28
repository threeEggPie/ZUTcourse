package xyz.kingsword.course.service;

import xyz.kingsword.course.VO.VerifyResult;
import xyz.kingsword.course.pojo.ExecutionPlan;
import xyz.kingsword.course.pojo.TrainingProgram;

import java.util.List;

public interface ExecutionVerify {
    VerifyResult verify(List<ExecutionPlan> executionPlanList, List<TrainingProgram> trainingProgramList);
}
