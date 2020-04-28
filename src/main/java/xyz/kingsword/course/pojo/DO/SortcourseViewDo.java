package xyz.kingsword.course.pojo.DO;

import lombok.Data;

import java.io.Serializable;

/**
 * @author
 */
@Data
public class SortcourseViewDo implements Serializable {
    private Integer sortCourseId;

    private String teaId;

    /**
     * 教师名称
     */
    private String teacherName;

    private String couId;

    private String courseName;

    /**
     * 课程性质：选修必修
     */
    private String nature;

    /**
     * 教材
     */
    private String textBook;

    /**
     * 学生总数
     */
    private Integer studentNum;

    /**
     * 学期id
     */
    private String semesterId;

    /**
     * 班级名，以空格分隔
     */
    private String className;


}