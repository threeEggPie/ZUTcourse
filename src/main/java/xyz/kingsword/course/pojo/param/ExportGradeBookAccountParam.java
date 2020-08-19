package xyz.kingsword.course.pojo.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExportGradeBookAccountParam {
    private int grade;
    @ApiModelProperty(value = "是否是本科")
    private boolean rb;

    public ExportGradeBookAccountParam(int grade, boolean rb) {
        this.grade = grade;
        this.rb = rb;
    }

    public ExportGradeBookAccountParam() {
    }
}
