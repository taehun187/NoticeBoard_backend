package com.taehun.board.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountMessage {
    REGISTER_SUCCESS("회원가입이 완료되었습니다."),
    LOGIN_SUCCESS("로그인에 성공하였습니다."),
    LOGOUT_SUCCESS("로그아웃이 완료되었습니다."),
    DUPLICATE_ID("중복된 아이디가 존재합니다."),
    DUPLICATE_EMAIL("중복된 이메일이 존재합니다.");

    private final String message;
}
