package xyz.kingsword.course.service;

import com.github.pagehelper.PageInfo;
import xyz.kingsword.course.pojo.ExecutionPlan;
import xyz.kingsword.course.pojo.param.ExecutionPlanSearchParam;

import java.io.InputStream;
import java.util.List;

public interface ExecutionPlanService {
    List<ExecutionPlan> importData(InputStream inputStream);

    void insert(List<ExecutionPlan> executionPlanList);

    void insert(ExecutionPlan executionPlan);

    void update(ExecutionPlan executionPlan);

    void delete(List<Integer> idList);

    PageInfo<ExecutionPlan> select(ExecutionPlanSearchParam param);
}
