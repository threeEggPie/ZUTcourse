package xyz.kingsword.course.exception;

import lombok.Getter;
import xyz.kingsword.course.enmu.ErrorEnum;

@Getter
public class OperationException extends BaseException {
    public OperationException(ErrorEnum errorEnum) {
        super(errorEnum);
    }

    public OperationException() {
        super(ErrorEnum.OPERATION_FORBIDDEN);
    }
}
