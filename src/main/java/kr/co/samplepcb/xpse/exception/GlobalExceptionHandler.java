package kr.co.samplepcb.xpse.exception;

import coolib.common.CCResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public CCResult handleBusinessException(BusinessException e) {
        return CCResult.exceptionSimpleMsg(e);
    }
}
