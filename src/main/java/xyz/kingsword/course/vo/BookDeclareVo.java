package xyz.kingsword.course.vo;

import lombok.Data;
import xyz.kingsword.course.pojo.Book;
import xyz.kingsword.course.util.TimeUtil;

import java.util.List;

@Data
public class BookDeclareVo {
    private String courseId;

    private String courseName;

    private int nature;

    /**
     * 学期id
     */
    private String semesterId;

    private String semesterName;

    private List<Book> bookList;

    private List<String> courseGroup;

    private static final long serialVersionUID = 1L;

    public void setSemesterId(String semesterId) {
        this.semesterId = semesterId;
        this.semesterName = TimeUtil.getSemesterName(semesterId);
    }
}
