package xyz.kingsword.course.pojo.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@ApiModel(description = "查询参数，可任意组合")
public class DeclareBookExportParam {
    @ApiModelProperty(required = true)
    private String semesterId;

    @ApiModelProperty(value = "是否报了教材")
    private Boolean declareStatus;

    private Integer nature;

    private Integer type;
}
