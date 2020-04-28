package xyz.kingsword.course.pojo;

import lombok.Data;
import xyz.kingsword.course.enmu.ErrorEnum;

@Data
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Result(T data) {
        this.code = 200;
        this.data = data;
    }

    public Result(ErrorEnum errorEnum) {
        code = errorEnum.getCode();
        msg = errorEnum.getMsg();
    }

    public Result() {
        code = 200;
    }
}
