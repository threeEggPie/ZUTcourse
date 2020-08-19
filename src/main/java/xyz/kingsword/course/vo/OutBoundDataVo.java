package xyz.kingsword.course.vo;

import lombok.*;

@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class OutBoundDataVo {
    private String className;
    private String bookName;
    private double price;
    private int number;
}
