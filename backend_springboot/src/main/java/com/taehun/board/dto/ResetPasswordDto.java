package com.taehun.board.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordDto {

    @NotBlank(message = "현재 비밀번호를 입력해야 합니다.")
    private String currentPassword; // 기존 비밀번호

    @NotBlank(message = "새 비밀번호를 입력해야 합니다.")
    private String newPassword; // 새 비밀번호

    @NotBlank(message = "확인 비밀번호를 입력해야 합니다.")
    private String confirmPassword; // 확인 비밀번호
}
