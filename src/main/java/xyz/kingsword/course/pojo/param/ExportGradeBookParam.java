package xyz.kingsword.course.pojo.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 余建青
 */
@Data
public class ExportGradeBookParam {
    private int grade;
    private String semester;
    @ApiModelProperty(value = "是否是本科")
    private boolean rb;
}
