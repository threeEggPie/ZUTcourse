package xyz.kingsword.course.vo;

import lombok.*;

import java.util.List;

@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CourseBookOrderVo {
    private String courseName;

    private String courseId;

    private List<BookOrderFlag> textBook;
}
