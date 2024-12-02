package com.taehun.board.message;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class ResponseMessage<T> {
    private String code;
    private String message;
    private T data;

    // 성공 응답 생성
    public static <T> ResponseMessage<T> success(String code, String message, T data) {
        return new ResponseMessage<>(code, message, data);
    }

    // 실패 응답 생성
    public static <T> ResponseMessage<T> fail(String code, String message) {
        return new ResponseMessage<>(code, message, null);
    }
}
