package xyz.kingsword.course.vo;

import lombok.*;

import java.util.List;
import java.util.Map;


@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class VerifyResult {
    /**
     * 以课程号进行分类，展示一个课程的培养计划各项属性的是否相同
     */
    private List<Map<String, List<VerifyField>>> trainingProgramResult;
    private List<Map<String, List<VerifyField>>> executionResult;
}


