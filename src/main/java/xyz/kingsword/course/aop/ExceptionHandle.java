package xyz.kingsword.course.aop;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import xyz.kingsword.course.enmu.ErrorEnum;
import xyz.kingsword.course.exception.AuthException;
import xyz.kingsword.course.exception.BaseException;
import xyz.kingsword.course.exception.DataException;
import xyz.kingsword.course.exception.OperationException;
import xyz.kingsword.course.pojo.Result;

import java.sql.SQLException;

/**
 * 拦截异常，向前端返回错误信息
 */
@Slf4j
@ControllerAdvice
public class ExceptionHandle {

    @ExceptionHandler(value = AuthException.class)
    @ResponseBody
    public Result<ErrorEnum> exceptionGet(AuthException e) {
        log.error("【权限异常】{}", e.getErrorEnum());
        e.printStackTrace();
        return new Result<>(e.getErrorEnum());
    }

    @ExceptionHandler(value = SQLException.class)
    @ResponseBody
    public Result<ErrorEnum> exceptionGet(SQLException e) {
        log.error("【数据库异常】");
        e.printStackTrace();
        return new Result<>(ErrorEnum.ERROR);
    }

    @ExceptionHandler(value = DataException.class)
    @ResponseBody
    public Result<ErrorEnum> exceptionGet(DataException e) {
        e.printStackTrace();
        return new Result<>(e.getErrorEnum());
    }

    @ExceptionHandler(value = BaseException.class)
    @ResponseBody
    public Result<String> exceptionGet(BaseException e) {
        e.printStackTrace();
        return new Result<>(406, e.getMessage());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, InvalidFormatException.class})
    @ResponseBody
    public Result<ErrorEnum> exceptionGet(RuntimeException e) {
        log.error("参数异常");
        return new Result<>(ErrorEnum.ERROR_PARAMETER);
    }

    @ExceptionHandler(OperationException.class)
    @ResponseBody
    public Result<Object> exceptionGet(OperationException e) {
        log.error("操作异常");
        return new Result<>(e.getErrorEnum() == null ? e.getMessage() : e.getErrorEnum());
    }


    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result<ErrorEnum> exceptionGet(Exception e) {
        log.error("【系统异常】{}", e.getMessage());
        e.printStackTrace();
        return new Result<>(ErrorEnum.ERROR);
    }
}
