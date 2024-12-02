package com.taehun.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 인증번호 전송 요청에 필요한 데이터 전송 객체 (DTO)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationDto {
    private String email; // 이메일 주소
    private String code;  // 인증번호
}
