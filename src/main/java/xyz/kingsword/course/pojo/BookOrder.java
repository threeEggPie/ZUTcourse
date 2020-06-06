package xyz.kingsword.course.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * book_order
 *
 * @author
 */
@Data
@ApiModel()
public class BookOrder implements Serializable {
    @ApiModelProperty(hidden = true)
    private Integer id;

    @ApiModelProperty(hidden = true)
    private String userId;

    @ApiModelProperty(required = true)
    private String courseId;

    @ApiModelProperty(required = true)
    private Integer bookId;

    @ApiModelProperty(required = true)
    private String semesterId;

    private double discount;

    /**
     * -1删除0正常
     */
    @ApiModelProperty(hidden = true)
    private Integer status;

    public BookOrder() {
    }
}