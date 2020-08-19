package xyz.kingsword.course.pojo;


import cn.hutool.core.util.NumberUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

/**
 * @author wzh
 */
@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@ApiModel("教材实体")
public class Book implements Serializable {
    @ApiModelProperty(hidden = true, notes = "不可更新")
    private Integer id;

    @ApiModelProperty(required = true, notes = "不可更新")
    private String isbn;

    @ApiModelProperty(required = true)
    private String name;

    @ApiModelProperty(required = true)
    private String author;

    @ApiModelProperty(required = true)
    private String publish;

    @ApiModelProperty(required = true)
    @Builder.Default
    private double price = 0;

    private String note;


    /**
     * -2删除-1审核不通过0正常
     */
    @ApiModelProperty(hidden = true)
    private Integer status;

    @ApiModelProperty(required = true)
    private String pubDate;

    @ApiModelProperty(notes = "推荐教师填写该字段")
    private String award;

    private String edition;

    /**
     * 书籍图片url
     */
    @ApiModelProperty(required = true)
    private String imgUrl;

    private String courseId;

    /**
     * 为老师留几本书
     */
    @Builder.Default
    @ApiModelProperty(notes = "为老师留几本书")
    private Integer forTeacher = 0;

    private static final long serialVersionUID = 1L;

    public String getAuthor() {
        return author == null ? "" : author;
    }

    public String getPublish() {
        return publish == null ? "" : publish;
    }

    public String getPubDate() {
        return pubDate == null ? "" : pubDate;
    }

    public String getAward() {
        return award == null ? "" : award;
    }

    public String getEdition() {
        return edition == null ? "" : edition;
    }

    public void setPrice(double price) {
        this.price = NumberUtil.round(price, 2).doubleValue();
    }
}