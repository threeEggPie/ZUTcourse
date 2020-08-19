package xyz.kingsword.course.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@ApiModel(value = "Course", description = "课程实体类")
@NoArgsConstructor
public class Course {
    //************************************
//    课程基本信息
//****************************************
    @ApiModelProperty(required = true, value = "课程id")
    private String id;

    @ApiModelProperty(required = true, value = "课程名")
    private String name;

    @ApiModelProperty(required = true, value = "课程管理员id")
    private String teacherInCharge;

    /**
     * see:{@link xyz.kingsword.course.enmu.CourseTypeEnum}
     */
    @ApiModelProperty(required = true, value = "课程类型",
            example = "1通识教育必修课，2通识教育选修课，3专业必修课,4专业选修课,5学科必修课,6实践环节",
            allowableValues = "range[1,6]")
    private int type;

    /**
     * 是否核心课程
     */
    @ApiModelProperty(required = true, value = "是否核心课程")
    private boolean core;

    /**
     * 选修 必修
     */
    @ApiModelProperty(required = true, allowableValues = "1选修,2必修")
    private int nature;

    @ApiModelProperty(required = true, value = "学分")
    private double credit;

    /**
     * 院考系考
     */
    @ApiModelProperty(required = true, allowableValues = "院考,系考")
    private String collegesOrDepartments;

    /**
     * 考试类型 考查还是考试
     */
    @ApiModelProperty(required = true, allowableValues = "考查,考试")
    private String examinationWay;

    @ApiModelProperty(hidden = true)
    private String textBook;

    @ApiModelProperty(hidden = true)
    private String referenceBook;

    @ApiModelProperty(hidden = true)
    private String bookManager;

    @ApiModelProperty(hidden = true)
    private List<Book> textBookList;

    @ApiModelProperty(hidden = true)
    private List<Book> referenceBookList;

    @ApiModelProperty(required = true, value = "教研室名称")
    private String researchRoom;

    /**
     * 辅导时间地点
     */
    @ApiModelProperty(value = "辅导时间地点")
    private String coach;
    /**
     * 辅导教师
     */
    @ApiModelProperty(value = "辅导教师")
    private String coachTeacher;

//****************************************
//    学时组成
//****************************************

    /**
     * 周学时
     */
    @ApiModelProperty(required = true, value = "周学时")
    private int timeWeek;
    /**
     * 上课周数
     */
    @ApiModelProperty(required = true, value = "上课周数")
    private int weekNum;

    /**
     * 理教周数
     */
    @ApiModelProperty(required = true, value = "理教周数")
    private int theoryWeek;

    /**
     * 理论学时
     */
    @ApiModelProperty(required = true, value = "理论学时")
    private int timeTheory;

    /**
     * 实践学时
     */
    @ApiModelProperty(value = "实践学时")
    private int timePractical = 0;

    /**
     * 上机学时
     */
    @ApiModelProperty(value = "上机学时")
    private int timeComputer = 0;

    /**
     * 实验学时
     */
    @ApiModelProperty(value = "实验学时")
    private int timeLab = 0;

    /**
     * 习题课学时
     */
    @ApiModelProperty(value = "习题课学时")
    private int timeHomework = 0;

    /**
     * 总学时
     */
    @ApiModelProperty(value = "总学时")
    private int timeAll;


    /**
     * 期末考核方式
     * see:{@link xyz.kingsword.course.enmu.AssessmentEnum}
     */
    @ApiModelProperty(required = true, value = "期末考核方式", example = "1闭卷笔试，2口试，3综合实验,4开卷笔试,5论文,6其他", allowableValues = "range[1,6]")
    private int assessmentWay;


//*****************************************
//      成绩组成
//*****************************************


    @ApiModelProperty(value = "作业占比")
    private double homeworkProportion = 0;

    @ApiModelProperty(value = "阶段性测验占比")
    private double testProportion = 0;

    @ApiModelProperty(value = "实验占比")
    private double labProportion = 0;

    @ApiModelProperty(value = "出勤占比")
    private double attendanceProportion = 0;

    @ApiModelProperty(value = "课堂表现占比")
    private double performanceProportion = 0;

    /**
     * 小论文或综合作业
     */
    @ApiModelProperty(value = "小论文或综合作业占比")
    private double paperProportion = 0;

    @ApiModelProperty(value = "其他项目占比")
    private double otherProportion = 0;

    @ApiModelProperty(required = true, value = "期末考试占比")
    private double examProportion = 0;

}