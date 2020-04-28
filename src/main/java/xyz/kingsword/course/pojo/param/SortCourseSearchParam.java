package xyz.kingsword.course.pojo.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;


@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@ApiModel(value = "SortCourseSearchParam", description = "查询参数，可任意组合")
public class SortCourseSearchParam {
    private String teaId;

    private String couId;

    private String courseName;

    private Integer classroomId;
    /**
     * 学期id
     */
    private String semesterId;

    @Builder.Default
    private int pageNum = 1;

    @Builder.Default
    private int pageSize = 10;

    /**
     * 排课标志，已分配为1，未分配-1，全部为0
     */
    @ApiModelProperty(value = "排课标志，已分配为1，未分配-1，全部为0", allowableValues = "range[-1,0,1]", required = true)
    @Builder.Default
    private int sortCourseFlag = 0;
}
