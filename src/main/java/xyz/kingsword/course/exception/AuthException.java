package xyz.kingsword.course.exception;

import lombok.Getter;
import xyz.kingsword.course.enmu.ErrorEnum;

@Getter
public class AuthException extends BaseException {

    public AuthException(ErrorEnum errorEnum) {

        super(errorEnum);
    }

    public AuthException() {
        super(ErrorEnum.NO_AUTH);
    }
}
