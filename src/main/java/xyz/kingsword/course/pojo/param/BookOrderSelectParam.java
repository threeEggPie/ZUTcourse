package xyz.kingsword.course.pojo.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@ApiModel(description = "订单查询参数，自由组合")
public class BookOrderSelectParam {
    private String userId;
    private String semesterId;
    private String className;
    private int bookId;
    //书名或者isbn
    @ApiModelProperty(value = "书名或isbn号")
    private String nameOrIsbn;

    private int grade;
    /**
     * 学历筛选 0全部，1本科 2专科
     */
    private int degree;

    @Builder.Default
    private int pageNum=1;
    @Builder.Default
    private int pageSize=10;
}
