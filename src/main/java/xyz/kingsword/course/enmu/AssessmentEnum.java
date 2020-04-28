package xyz.kingsword.course.enmu;

import lombok.Getter;

/**
 * 期末考核方式枚举类
 */
@Getter
public enum AssessmentEnum {
    CLOSE_BOOK_EXAM(1,"闭卷笔试"),
    ORAL_TEST(2,"口试"),
    COMPREHENSIVE_EXPERIMENTS(3,"综合实验"),
    OPEN_BOOK_EXAM(4,"开卷笔试"),
    PAPER(5,"论文"),
    OTHER(6,"其他");

    private int code;
    private String content;

    AssessmentEnum(int code, String content) {
        this.code = code;
        this.content = content;
    }

    public static AssessmentEnum getContent(int code) {
        AssessmentEnum val = OTHER;
        for (AssessmentEnum assessmentEnum : AssessmentEnum.values()) {
            if (assessmentEnum.getCode() == code) {
                val = assessmentEnum;
                break;
            }
        }
        return val;
    }
}
