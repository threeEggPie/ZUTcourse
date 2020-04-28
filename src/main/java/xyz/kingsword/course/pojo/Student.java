package xyz.kingsword.course.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.kingsword.course.enmu.RoleEnum;

@Data
@NoArgsConstructor
public class Student {
    private String id;

    @ApiModelProperty(hidden = true)
    private String password;

    private String name;

    @ApiModelProperty(value = "班级不存在时会报错")
    private String className;

    @ApiModelProperty(hidden = true)
    private String gender;

    private int grade;

    @ApiModelProperty(hidden = true)
    private final String role = "[" + RoleEnum.STUDENT.getCode() + "]";

    private int status = 0;

    public Student(String id, String className) {
        this.id = id;
        this.className = className;
    }
}