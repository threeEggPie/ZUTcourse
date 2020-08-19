package xyz.kingsword.course.pojo.param;

import io.swagger.annotations.ApiModel;
import lombok.*;

@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@ApiModel(description = "查询参数，自由组合")
public class StudentSelectParam {
    private String name;

    private String id;

    private String className;

    private int degree;

    private int grade;

    @Builder.Default
    private int pageNum = 1;

    @Builder.Default
    private int pageSize = 10;
}
