package com.taehun.board.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MailMessage {
    SEND_SUCCESS("인증번호가 전송되었습니다."),
    VERIFY_SUCCESS("인증번호가 확인되었습니다."),
    NOT_MATCHED_CODE("일치하지 않는 인증 코드입니다.");

    private final String message;
}
