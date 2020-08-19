package xyz.kingsword.course.pojo.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@ApiModel(description = "查询参数，可任意组合")
public class CourseSelectParam {
    private String courseId;

    private String courseName;

    @ApiModelProperty(value = "课程性质 1选修2必修", allowableValues = "range[1,2]")
    private Integer nature;

    @ApiModelProperty(value = "课程类别", allowableValues = "range[1,8]")
    private Integer type;

    private String researchRoom;

    private String teacherInCharge;

    private Boolean declareStatus;

    @Builder.Default
    private int pageNum = 1;
    @Builder.Default
    private int pageSize = 10;
}
