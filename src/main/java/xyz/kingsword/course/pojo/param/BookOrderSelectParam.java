package xyz.kingsword.course.pojo.param;

import io.swagger.annotations.ApiModel;
import lombok.*;

@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@ApiModel(description = "查询参数，自由组合")
public class BookOrderSelectParam {
    private String userId;
    private String semesterId;
    private String className;
    private int bookId;

    private int grade;
    /**
     * 学历筛选 0全部，1本科 2专科
     */
    private int degree;
}
