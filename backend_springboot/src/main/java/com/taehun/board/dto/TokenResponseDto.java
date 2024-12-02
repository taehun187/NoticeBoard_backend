package com.taehun.board.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResponseDto {
    private String token;  // Access Token
    private String refreshToken; // Refresh Token
    private String username;
    private String email;

    // token만 초기화하는 생성자 추가
    public TokenResponseDto(String token) {
        this.token = token;
    }
}
