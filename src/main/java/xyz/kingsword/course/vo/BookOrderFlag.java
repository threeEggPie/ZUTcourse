package xyz.kingsword.course.vo;

import lombok.*;
import xyz.kingsword.course.pojo.Book;

@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class BookOrderFlag {
    private boolean flag;

    private Integer orderId;

    private Book info;
}
