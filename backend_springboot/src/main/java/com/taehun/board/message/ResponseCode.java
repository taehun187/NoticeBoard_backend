package com.taehun.board.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {
    REQUEST_SUCCESS("1", "요청이 성공적으로 완료되었습니다."),
    REQUEST_FAIL("-1", "요청 처리에 실패하였습니다."),
    LOGIN_SUCCESS("2", "로그인에 성공하였습니다."),
    LOGIN_FAIL("-2", "로그인에 실패하였습니다."),
    INVALID_TOKEN("-3", "유효하지 않은 토큰입니다."),
    SERVER_ERROR("-4", "서버에서 알 수 없는 오류가 발생하였습니다."); // 추가된 부분

    private final String code;
    private final String message;
}
