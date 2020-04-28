package xyz.kingsword.course.pojo.DO;

import lombok.Data;

/**
 * 表示一条书籍预定记录
 */
@Data
public class BookOrderDo {
    private String studentId;

    private String studentName;

    /**
     * 学生所属班级
     */
    private String className;

    private int bookId;

    private String semesterId;

    private String bookName;

    private double price;
}
