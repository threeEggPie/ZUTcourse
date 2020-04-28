package xyz.kingsword.course.pojo.param;

import lombok.Data;

import java.util.List;

/**
 * 多个班级教材申报情况导出参数
 */
@Data
public class ExportPluralClassBookParam {
    private List<String> classNameList;

    private String semesterId;
}
