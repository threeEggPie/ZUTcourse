package xyz.kingsword.course.exception;

import xyz.kingsword.course.enmu.ErrorEnum;

public class ParameterException extends BaseException {
    public ParameterException(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
