package xyz.kingsword.course.vo;

import lombok.Getter;

/**
 * 每个参数是否相同
 */
@Getter
public class VerifyField {
    private final String name;
    private final boolean same;
    private final String value;

    public VerifyField(String name, String value, boolean same) {
        this.name = name;
        this.same = same;
        this.value = value;
    }
}
