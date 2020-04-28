package xyz.kingsword.course.pojo;

import lombok.*;

import java.io.Serializable;

/**
 * 执行计划
 *
 * @author wzh
 */
@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class TrainingProgram implements Serializable {
    /**
     * id
     */
    private Integer id;

    /**
     * 课程id
     */
    private String courseId;
    /**
     * 1通识教育必修课，2通识教育选修课，3专业必修课,4专业选修课,5学科必修课,6实践环节
     */
    private int type;

    /**
     * 课程名
     */
    private String courseName;

    /**
     * 学分
     */
    private float credit;

    /**
     * 是否核心课程
     */
    private Boolean core;

    /**
     * 考核方式（考试考查）
     */
    private String examinationWay;

    /**
     * 院考或系考
     */
    private String collegesOrDepartments;

    /**
     * 总学时
     */
    private float timeAll;

    /**
     * 理论学时
     */
    @Builder.Default
    private float timeTheory = 0F;

    /**
     * 实验学时
     */
    @Builder.Default
    private float timeLab = 0f;

    /**
     * 上机学时
     */
    @Builder.Default
    private float timeComputer = 0f;

    /**
     * 其他学时
     */
    @Builder.Default
    private float timeOther = 0f;

    /**
     * 起始学期
     */
    private Integer startSemester;

    /**
     * 培养方案按入学时间分，如：2017级本科培养方案，2018级本科培养方案
     * eg:2017 2018
     */
    private int grade;

    private String specialityId;

    private String semesterId;

    private int status;

    private static final long serialVersionUID = 1L;

}