package xyz.kingsword.course.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * SortCourse
 *
 * @author wzh
 */
@Data
public class SortCourse implements Serializable {
    private Integer id;

    private String teaId;

    private String couId;

    /**
     * 学生总数
     */
    private Integer studentNum;

    private Integer classroomId;

    /**
     * 学期id
     */
    private String semesterId;

    /**
     * -2删除-1被合并课头0正常显示
     */
    private Integer status;


    private String className;

    /**
     * 被合并的课头id
     */
    private String mergedId;

    private int flag;

    private static final long serialVersionUID = 1L;


}