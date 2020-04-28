package xyz.kingsword.course.pojo.param;

import io.swagger.annotations.ApiModel;
import lombok.*;

@ApiModel(description = "培养方案查询参数，可全空")
@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class TrainingProgramSearchParam {
    private Integer id;

    private String semesterId;

    private Integer grade;

    private String specialityId;

    private String courseName;

    @Builder.Default
    private int pageNum = 1;

    @Builder.Default
    private int pageSize = 10;
}
