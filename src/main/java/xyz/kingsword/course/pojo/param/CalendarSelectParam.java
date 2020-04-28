package xyz.kingsword.course.pojo.param;

import io.swagger.annotations.ApiModel;
import lombok.*;

@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@ApiModel(description = "查询参数，自由组合")
public class CalendarSelectParam {
    private int id;

    private String teacherId;

    private String semesterId;

    private String courseId;

    private String researchRoom;

    @Builder.Default
    private int pageNum = 1;
    @Builder.Default
    private int pageSize = 10;
}
