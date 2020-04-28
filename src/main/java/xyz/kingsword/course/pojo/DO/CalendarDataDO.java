package xyz.kingsword.course.pojo.DO;

import lombok.Data;

/**
 * DO 对应数据库 calendar_view
 * {@link xyz.kingsword.course.service.calendarExport.CalendarData}
 */
@Data
public class CalendarDataDO {
    /**
     * 对应教学日历id
     */
    private int id;

    /**
     * 教师名称
     */

    private String teaName;
    /**
     * 职称
     */
    private String title;
    /**
     * 教师id
     */
    private String teaId;

    /**
     * 所在学院
     */
    private String college;

    /**
     * 教研室
     */
    private String researchRoom;

    /**
     * 课程id
     */
    private String courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 教授班级
     */
    private String className;

    /**
     * 排课id
     */
    private int sortId;

    /**
     * 理教周数
     */
    private int weekOfTheory;
    /**
     * 讲课时数
     */
    public int timeOfTheory;

    /**
     * 上机或实验学时
     */
    private int timeOfComputer;
    /**
     * 习题课学时
     */
    private int timeOfHomework;
    /**
     * 总学时
     */
    private int timeOfAll;
    /**
     * 学分
     */
    private int credit;
    /**
     * 学生人数
     */
    private int studentNum;

    /**
     * 课程属性 1选修2必修
     */
    private int courseProperty;

    /**
     * 考试类型 考试考查
     */
    private String testType;


    /**
     * 期末考核方式 只有一个打勾，其他的空字符串
     */
    private int examinationForm;

    private int examProportion;

    private int homeworkProportion;

    private int labProportion;

    private int testProportion;
    /**
     * 小论文或综合作业
     */
    private int paperProportion;

    /**
     * 出勤占比
     */
    private int attendanceProportion;

    /**
     * 课堂表现占比
     */
    private int performanceProportion;

    /**
     * 其他方式占比
     */
    private int otherProportion;

    /**
     * 辅导答疑时间地点
     */
    private String coach;
    /**
     * 辅导教师
     */
    private String coachTeacher;

    private String textBook;
    private String referenceBook;

    /**
     * 授课内容
     */
    private String teachingContent;

    private String semesterId;
}
