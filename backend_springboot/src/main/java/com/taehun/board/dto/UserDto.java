package com.taehun.board.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    @NotBlank(message = "사용자 이름은 필수입니다.")
    @Size(min = 3, max = 20, message = "사용자 이름은 3자 이상, 20자 이하로 입력해야 합니다.")
    private String username;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 주소를 입력하세요.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수입니다.")
    private String checkPassword;

    private String profileImageUrl;

    private String verificationCode;
}

