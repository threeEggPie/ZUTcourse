package xyz.kingsword.course.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@ApiModel(description = "执行计划实体")
public class ExecutionPlan {
    private Integer id;

    @ApiModelProperty(required = true)
    private String courseId;

    @ApiModelProperty(required = true)
    private String courseName;

    @ApiModelProperty(required = true)
    private float credit;

    @ApiModelProperty(required = true)
    private float timeAll;

    @ApiModelProperty(required = true)
    private float timeTheory;

    private float timeLab;

    private float timeComputer;

    private float timeOther;
    /**
     * 周学时
     */
    @ApiModelProperty(required = true)
    private float timeWeek;

    @ApiModelProperty(required = true)
    private int startSemester;

    /**
     * 考核方式(考试、考查)
     */
    @ApiModelProperty(required = true)
    private String examinationWay;


    /**
     * 入学时间
     * eg:2017 2018
     */
    @ApiModelProperty(required = true)
    private int grade;

    /**
     * 专业方向id
     */
    @ApiModelProperty(required = true)
    private String specialityId;

    /**
     * 执行计划应用学期
     */
    @ApiModelProperty(required = true)
    private String semesterId;

    @ApiModelProperty(required = true, value = "课程类型", example = "1通识教育必修课，2通识教育选修课，3专业必修课,4专业选修课,5学科必修课,6实践环节", dataType = "integer")
    private int type;

    @ApiModelProperty(required = true, value = "课程性质")
    private String nature;

    private int status;

    public float getTimeAll() {
        this.timeAll = timeTheory + timeLab + timeComputer + timeOther + timeWeek;
        return timeAll;
    }
}
