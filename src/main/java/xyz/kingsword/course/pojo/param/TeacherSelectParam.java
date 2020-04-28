package xyz.kingsword.course.pojo.param;

import io.swagger.annotations.ApiModel;
import lombok.*;

@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@ApiModel(description = "查询参数，可任意组合")
public class TeacherSelectParam {
    private String name;

    private String id;

    private int roleId;

    @Builder.Default
    private int pageNum = 1;

    @Builder.Default
    private int pageSize = 10;
}
