package xyz.kingsword.course.vo;

import lombok.Data;

import java.util.Map;

@Data
public class BookSettlementVo {
    private String className;
    private String name;
    private String userId;

    private Map<String, Double> semesterBill;
}
