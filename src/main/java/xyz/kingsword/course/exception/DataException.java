package xyz.kingsword.course.exception;


import lombok.Getter;
import xyz.kingsword.course.enmu.ErrorEnum;


@Getter
public class DataException extends BaseException {

    public DataException(ErrorEnum errorEnum) {
        super(errorEnum);
    }

    public DataException(String message) {
        super(message);
    }

    public DataException() {
        super(ErrorEnum.DATA_ERROR);
    }
}
