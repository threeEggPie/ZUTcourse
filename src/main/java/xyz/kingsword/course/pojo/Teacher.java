package xyz.kingsword.course.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel
public class Teacher implements Serializable {
    @ApiModelProperty(required = true, value = "姓名全拼小写，或工号皆可")
    private String id;

    @ApiModelProperty(hidden = true)
    private String password;

    @ApiModelProperty(required = true)
    private String name;

    private String researchRoom;

    private String phone;

    private String email;

    private String departmentSchool;

    private String teachingTitle;

    private String education;

    private String gender;

    @ApiModelProperty(hidden = true)
    private String role;

    /**
     * 管理的课程id，存json
     */
    @ApiModelProperty(hidden = true)
    private String courseInCharge;

    /**
     * 管理的专业id，存json
     */
    @ApiModelProperty(hidden = true)
    private String specialtyInCharge;
    /**
     * 0正常-1删除
     */
    @ApiModelProperty(hidden = true)
    private int status = 0;

    @ApiModelProperty(hidden = true)
    private int currentRole;

    private final String className = "教师组";

}