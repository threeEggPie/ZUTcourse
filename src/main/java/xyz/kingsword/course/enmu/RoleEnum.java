package xyz.kingsword.course.enmu;

import lombok.Getter;

@Getter
public enum RoleEnum {
    ADMIN(0, "管理员"),
    TEACHER(1, "教师"),
    ACADEMIC_MANAGER(2, "教学部"),
    STUDENT(3, "学生"),
    SPECIALTY_MANAGER(4, "专业负责人"),
    OFFICE_MANAGER(5, "教研室主任"),
    COURSE_MANAGER(6, "课程负责人");


    private int code;
    private String content;

    RoleEnum(int code, String content) {
        this.code = code;
        this.content = content;
    }

    public static RoleEnum valueOf(int code) {
        RoleEnum val = null;
        for (RoleEnum roleEnum : RoleEnum.values()) {
            if (roleEnum.getCode() == code) {
                val = roleEnum;
                break;
            }
        }
        return val;
    }

    @Override
    public String toString() {
        return "RoleEnum{" +
                "code=" + code +
                ", content='" + content + '\'' +
                '}';
    }
}
