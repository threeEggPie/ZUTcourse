package xyz.kingsword.course.vo;

import lombok.Data;

@Data
public class SemesterDiscountVo {
    private String semesterId;
    private String discount;
    private String semesterName;

    public SemesterDiscountVo(String semesterId, String discount, String semesterName) {
        this.semesterId = semesterId;
        this.discount = discount;
        this.semesterName = semesterName;
    }
}
