package xyz.kingsword.course.pojo.DO;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 订书记录导出需要的课程信息
 *
 * @author
 */
@Data
public class BookExportCourseDo implements Serializable {
    private String courseName;


    private String courseId;

    /**
     * 课程性质：选修必修
     */
    private String nature;

    /**
     * 班级名，以空格分隔
     */
    private String className;

    private String teaId;

    /**
     * 教师名称
     */
    private String teacherName;


    private String semesterId;

    private String textBook;

    private List<Integer> textBookList;

    public void setTextBook(String textBook) {
        this.textBook = textBook;
        this.textBookList = JSONArray.parseArray(textBook, Integer.class);
    }

    private static final long serialVersionUID = 1L;
}