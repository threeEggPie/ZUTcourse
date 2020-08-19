package xyz.kingsword.course.vo;

import lombok.Data;

/**
 * 统计每本书要了多少
 */
@Data
public class BookOrderStatistics {
    private int bookId;

    private String name;

    private String author;

    private String publish;

    private String courseId;

    private String courseName;

    private int count;
}
