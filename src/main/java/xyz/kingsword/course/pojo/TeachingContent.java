package xyz.kingsword.course.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;

/**
 * 本类是对每个教学日历的授课内容的描述，以json文本存数据库
 */
@Data
public class TeachingContent implements Comparable<TeachingContent> {
    /**
     * 自增
     */
    private int id;
    /**
     * 授课顺序
     */
    private int index;
    /**
     * 授课日期
     */
    private Date date;
    /**
     * 第几周的课
     */
    private int weekNum;
    /**
     * 授课内容
     */
    @JsonIgnore
    private String teachingContent = "";
    /**
     * 学时数，默认为2
     */
    private int studyTime;
    /**
     * 作业或实验安排
     */
    private String homeworkOrLab = "";

    public TeachingContent() {
        studyTime = 2;
    }

    /**
     * 按日期排序
     */
    @Override
    public int compareTo(TeachingContent o) {
        return date.compareTo(o.getDate());
    }
}
