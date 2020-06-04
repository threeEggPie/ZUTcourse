package xyz.kingsword.course.vo;

import lombok.Data;
import xyz.kingsword.course.pojo.Book;
import xyz.kingsword.course.util.TimeUtil;

import java.util.List;

@Data
public class SortCourseVo {
    private Integer id;

    private String teacherId;

    private String teacherName;

    private String courseId;

    private String courseName;

    private String nature;

    /**
     * 学生总数
     */
    private Integer studentNum;

    private Integer classroomId;

//    private String classroomName;

    /**
     * 学期id
     */
    private String semesterId;

    private String semesterName;

    private String className;

    private int textBookNum = 0;

    private int referenceBookNum = 0;

    private String textBookString;
    private String referenceBookString;

    private List<Book> bookList;
    private List<Book> referenceBookList;

    private List<String> courseGroup;

    private String bookManager;


    private static final long serialVersionUID = 1L;

    public void setSemesterId(String semesterId) {
        this.semesterId = semesterId;
        this.semesterName = TimeUtil.getSemesterName(semesterId);
    }
}
