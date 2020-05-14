package xyz.kingsword.course.pojo.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Date;

@Data
public class ExportGradeBookParam {
    private int grade;
    private String semester;
    @ApiModelProperty(value = "是否是本科")
    private boolean rb;
}
