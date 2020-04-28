package xyz.kingsword.course.pojo;

import lombok.Data;

/**
 * 对应数据库view course_group_view
 */
@Data
public class TeacherGroup {
    private int id;
    private String semesterId;
    private String semesterName;
    private Integer sortId;
    private String courseId;
    private String teaId;
    private String teacherName;
    private String courseName;
    private Integer calendarId;
    private String className;
}
