package xyz.kingsword.course.enmu;

import lombok.Getter;

@Getter
public enum CourseNature {
    NOT_REQUIRED(1,"选修"),
    REQUIRED(2,"必修");

    private int code;
    private String content;

    CourseNature(int code, String content) {
        this.code = code;
        this.content = content;
    }

    public static CourseNature getContent(int code) {
        CourseNature val = null;
        for (CourseNature courseNature : CourseNature.values()) {
            if (courseNature.getCode() == code) {
                val = courseNature;
                break;
            }
        }
        return val;
    }
}
