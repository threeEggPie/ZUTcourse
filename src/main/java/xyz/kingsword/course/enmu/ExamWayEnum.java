package xyz.kingsword.course.enmu;

import lombok.Getter;

/**
 * 考试类型枚举类
 */
@Getter
public enum ExamWayEnum {
    EXAM(1,"考试"),
    TEST(2,"考查");

    private int code;
    private String content;

    ExamWayEnum(int code, String content) {
        this.code = code;
        this.content = content;
    }

    public static ExamWayEnum getContent(int code) {
        ExamWayEnum val = null;
        for (ExamWayEnum examWayEnum : ExamWayEnum.values()) {
            if (examWayEnum.getCode() == code) {
                val = examWayEnum;
                break;
            }
        }
        return val;
    }}
