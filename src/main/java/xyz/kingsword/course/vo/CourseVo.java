package xyz.kingsword.course.vo;

import lombok.Data;
import xyz.kingsword.course.pojo.Book;

import java.util.Collections;
import java.util.List;

@Data
public class CourseVo {
    //    课程基本信息
    private String id;

    private String name;

    private String teacherInCharge;

    /**
     * see:{@link xyz.kingsword.course.enmu.CourseTypeEnum}
     */
    private int type;

    /**
     * 是否核心课程
     */
    private String core;

    /**
     * 1选修2必修
     */
    private int nature;

    private double credit;

    /**
     * 院考系考
     */
    private String collegesOrDepartments;
    /**
     * 考试类型 考查还是考试
     */
    private String examinationWay;


    private List<Book> bookList;

    public List<Book> getReferenceBookList() {
        return referenceBookList == null ? Collections.emptyList() : referenceBookList;
    }

    private List<Book> referenceBookList;

    private String researchRoom;

    /**
     * 辅导时间地点
     */
    private String coach;
    /**
     * 辅导教师
     */
    private String coachTeacher;

    private List<String> courseGroup;

    private String bookManager;

    private String bookManagerId;

//****************************************
//    学时组成
//****************************************

    /**
     * 周学时
     */
    private int timeWeek;
    /**
     * 上课周数
     */
    private int weekNum;

    /**
     * 理教周数
     */
    private int theoryWeek;

    /**
     * 理论学时
     */
    private int timeTheory;

    /**
     * 实践学时
     */
    private int timePractical;

    /**
     * 上机学时
     */
    private int timeComputer;

    /**
     * 实验学时
     */
    private int timeLab;

    /**
     * 习题课学时
     */
    private int timeHomework;

    /**
     * 总学时
     */
    private int timeAll;


    /**
     * 期末考核方式
     * see:{@link xyz.kingsword.course.enmu.AssessmentEnum}
     */
    private String assessmentWay;


//*****************************************
//      成绩组成
//*****************************************


    private double homeworkProportion;

    private double testProportion;

    private double labProportion;

    private double attendanceProportion;

    private double performanceProportion;

    /**
     * 小论文或综合作业
     */
    private double paperProportion;

    private double otherProportion;

    private double examProportion;

    public void setTimeAll(int timeAll) {
        this.timeAll = timeTheory + timePractical + timeComputer + timeLab + timeHomework;
    }

}

