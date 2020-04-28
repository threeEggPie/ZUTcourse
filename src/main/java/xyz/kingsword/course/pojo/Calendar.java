package xyz.kingsword.course.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wzh
 */
@Data
@ApiModel()
public class Calendar implements Serializable {
    @ApiModelProperty("新建时不传")
    private Integer id;

    @ApiModelProperty(required = true)
    private String teaId;
    /**
     * 课程id
     */
    @ApiModelProperty(required = true)
    private String courseId;

    /**
     * 排课id
     */
    @ApiModelProperty(required = true, value = "排课id")
    private Integer sortId;


    /**
     * 0正常1课程组长审核通过2教研室主任审核通过
     */
    @ApiModelProperty(required = true, value = " 0未审核1课程组长审核通过2教研室主任审核通过", allowableValues = "range[0, 2]")
    private Integer status;


    /**
     * 学期id
     */
    @ApiModelProperty(required = true)
    private String semesterId;

    private String teachingContent;

    private static final long serialVersionUID = 1L;
}

