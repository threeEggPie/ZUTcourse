package xyz.kingsword.course.VO;

import lombok.Data;
import xyz.kingsword.course.enmu.RoleEnum;

import java.util.List;
import java.util.Map;

@Data
public class StudentVo {

    private String id;

    private String name;

    private String className;

    private String semesterId;

    private final String role = "[" + RoleEnum.STUDENT.getCode() + "]";

    private final int currentRole = RoleEnum.STUDENT.getCode();

    private List<CourseBookOrderVo> courseList;


    public StudentVo() {
    }

}
