package xyz.kingsword.course.vo;

import lombok.Data;
import xyz.kingsword.course.pojo.Book;
import xyz.kingsword.course.util.TimeUtil;

import java.util.Collections;
import java.util.List;

@Data
public class SortCourseVo {
    private Integer id;

    private String teacherId;

    private String teacherName;

    private String courseId;

    private String courseName;

    private int nature;

    private int type;

    private boolean flag;

    private int timeAll;

    /**
     * 学生总数
     */
    private Integer studentNum;

    private Integer classroomId;


    /**
     * 学期id
     */
    private String semesterId;

    private String semesterName;

    private String className;

    private int textBookNum = 0;

    private int referenceBookNum = 0;

    private List<Book> bookList;

    public List<Book> getReferenceBookList() {
        return referenceBookList == null ? Collections.emptyList() : referenceBookList;
    }

    private List<Book> referenceBookList;

    private List<String> courseGroup;

    private String bookManager;


    private static final long serialVersionUID = 1L;

    public void setSemesterId(String semesterId) {
        this.semesterId = semesterId;
        this.semesterName = TimeUtil.getSemesterName(semesterId);
    }
}
