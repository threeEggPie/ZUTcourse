package xyz.kingsword.course.pojo;


import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@ApiModel(value = "SortCourseLog",description = "排课日志实体")
public class SortCourseLog {

    private Integer id;

    /**
     * 排课id
     */
    private Integer sortCourseId;
    /**
     * 操作者id
     */
    private String operatorId;
    /**
     * 操作者姓名
     */
    private String operatorName;
    /**
     * 操作后代课老师姓名
     */
    private String teacherName;
    /**
     * 操作时间
     */
    private Date operateTime;
}
