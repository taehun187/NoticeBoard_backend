package com.taehun.board.handler;

import com.taehun.board.message.ResponseCode;
import com.taehun.board.message.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 유효성 검사 실패 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseMessage<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        String errorMessage = bindingResult.getAllErrors()
                .get(0) // 첫 번째 에러 가져오기
                .getDefaultMessage();

        return ResponseEntity.badRequest().body(
                ResponseMessage.fail(ResponseCode.REQUEST_FAIL.getCode(), errorMessage)
        );
    }


    // 일반 RuntimeException 처리
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseMessage<Void>> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseMessage.fail(ResponseCode.REQUEST_FAIL.getCode(), e.getMessage())
        );
    }

    // 기타 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseMessage<Void>> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ResponseMessage.fail(ResponseCode.SERVER_ERROR.getCode(), "서버에서 알 수 없는 오류가 발생했습니다.")
        );
    }
}
