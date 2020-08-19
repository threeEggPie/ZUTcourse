package xyz.kingsword.course.pojo.param;

import io.swagger.annotations.ApiModel;
import lombok.*;

@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@ApiModel(value = "SortCourseSearchParam", description = "查询参数，可任意组合")
public class SelectBookDeclareParam {
    private String courseName;
    private String semesterId;
    private Boolean declareStatus;
    private Integer type;
    private Integer nature;

    @Builder.Default
    private int pageNum = 1;

    @Builder.Default
    private int pageSize = 10;
}
