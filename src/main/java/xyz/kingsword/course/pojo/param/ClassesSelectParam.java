package xyz.kingsword.course.pojo.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@ApiModel(description = "查询参数，自由组合")
public class ClassesSelectParam {
    @ApiModelProperty("模糊查询")
    private String className;

    private int grade;

    private int speciality;

    @Builder.Default
    private int pageNum = 1;

    @Builder.Default
    private int pageSize = 10;

}
