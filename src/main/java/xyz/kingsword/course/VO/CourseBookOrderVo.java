package xyz.kingsword.course.VO;

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
