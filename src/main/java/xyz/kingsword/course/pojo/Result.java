package xyz.kingsword.course.pojo;

import lombok.Data;
import xyz.kingsword.course.enmu.ErrorEnum;

import java.sql.ResultSet;

@Data
public class Result<T> {
    private int code;
    private String msg;
    private T data;
    private static Result<Object> result = new Result<>();

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

    /**
     * 静态空result，避免频繁new对象
     * @return result
     */
    public static Result<Object> emptyResult() {
        return Result.result;
    }
}
