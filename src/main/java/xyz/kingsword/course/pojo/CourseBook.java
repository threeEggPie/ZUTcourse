package xyz.kingsword.course.pojo;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.util.List;

/**
 * 导出书籍订购信息需要用的课程信息
 */
@Data
public class CourseBook {
    private String courseId;

    private String courseName;

    private String nature;

    private String className;

    private String textBook;

    private List<Integer> textBookIdList;

    public void setTextBook(String textBook) {
        this.textBook = textBook;
        this.textBookIdList = JSON.parseArray(textBook, Integer.class);
    }
}
