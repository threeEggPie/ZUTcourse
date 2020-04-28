package xyz.kingsword.course.pojo.DO;

import lombok.*;

import java.util.Map;

/**
 * 教材征订表导出封装班级、教材之间的关系
 */
@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class BookExportClassDo {
    private int bookId;
    /**
     * k:班级
     * v:数量
     */
    private Map<String, Integer> map;
}
