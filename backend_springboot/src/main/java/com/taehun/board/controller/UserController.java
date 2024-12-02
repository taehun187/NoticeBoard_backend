package com.taehun.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taehun.board.dto.*;
import com.taehun.board.entity.User;
import com.taehun.board.message.AccountMessage;
import com.taehun.board.message.ResponseCode;
import com.taehun.board.message.ResponseMessage;
import com.taehun.board.repository.UserRepository;
import com.taehun.board.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping(value = "/registers", consumes = "multipart/form-data")
    public ResponseEntity<ResponseMessage<Void>> register(
            @RequestPart("data") @Valid String jsonData,
            @RequestPart(value = "image", required = false) MultipartFile profileImage) {
        try {
            log.debug("받은 JSON 데이터: {}", jsonData);
            if (profileImage != null) {
                log.debug("받은 이미지 파일 이름: {}", profileImage.getOriginalFilename());
            } else {
                log.debug("받은 이미지 파일: 없음");
            }

            UserDto userDto = objectMapper.readValue(jsonData, UserDto.class);
            userService.register(userDto, profileImage);

            return ResponseEntity.ok(
                    ResponseMessage.success(ResponseCode.REQUEST_SUCCESS.getCode(), AccountMessage.REGISTER_SUCCESS.getMessage(), null)
            );
        } catch (RuntimeException e) {
            log.error("RuntimeException 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ResponseMessage.fail(ResponseCode.REQUEST_FAIL.getCode(), e.getMessage())
            );
        } catch (Exception e) {
            log.error("Exception 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ResponseMessage.fail(ResponseCode.REQUEST_FAIL.getCode(), "잘못된 요청 데이터입니다.")
            );
        }
    }



    @PostMapping("/logins")
    public ResponseEntity<ResponseMessage<TokenResponseDto>> login(@RequestBody LoginRequestDto loginRequestDto) {
        User user = userService.login(loginRequestDto);
        String accessToken = userService.generateAccessToken(user.getUsername());
        String refreshToken = userService.generateRefreshToken(user.getUsername());

        userService.saveRefreshToken(user.getUsername(), refreshToken);

        TokenResponseDto tokenResponse = new TokenResponseDto(accessToken, refreshToken, user.getUsername(), user.getEmail());
        return ResponseEntity.ok(
                ResponseMessage.success(ResponseCode.LOGIN_SUCCESS.getCode(), AccountMessage.LOGIN_SUCCESS.getMessage(), tokenResponse)
        );
    }

    @GetMapping("/logout")
    public ResponseEntity<ResponseMessage<Void>> logoutUser() {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        userService.invalidateToken(token);

        return ResponseEntity.ok(
                ResponseMessage.success(ResponseCode.REQUEST_SUCCESS.getCode(), "로그아웃 성공", null)
        );
    }

    @PatchMapping("/password")
    public ResponseEntity<ResponseMessage<Void>> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        String username = getCurrentUsername();
        userService.resetPassword(username, resetPasswordDto);

        return ResponseEntity.ok(
                ResponseMessage.success(ResponseCode.REQUEST_SUCCESS.getCode(), "비밀번호가 변경되었습니다.", null)
        );
    }

    @GetMapping("/users")
    public ResponseEntity<ResponseMessage<List<UserDto>>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers(); // 서비스에서 모든 사용자 정보 조회
        return ResponseEntity.ok(
                ResponseMessage.success(ResponseCode.REQUEST_SUCCESS.getCode(), "사용자 목록 조회 성공", users)
        );
    }

    @DeleteMapping("/users")
    public ResponseEntity<ResponseMessage<Void>> deleteUser() {
        String username = getCurrentUsername();
        userService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{email}/{id}")
    public ResponseEntity<ResponseMessage<String>> isExistAccount(@PathVariable String email, @PathVariable String id) {
        boolean emailExists = userService.isExistAccount(email, null);
        boolean idExists = userService.isExistAccount(null, id);

        if (emailExists && idExists) {
            return ResponseEntity.badRequest().body(
                    ResponseMessage.fail(ResponseCode.REQUEST_FAIL.getCode(), "이메일과 아이디가 모두 중복됩니다.")
            );
        } else if (emailExists) {
            return ResponseEntity.badRequest().body(
                    ResponseMessage.fail(ResponseCode.REQUEST_FAIL.getCode(), AccountMessage.DUPLICATE_EMAIL.getMessage())
            );
        } else if (idExists) {
            return ResponseEntity.badRequest().body(
                    ResponseMessage.fail(ResponseCode.REQUEST_FAIL.getCode(), AccountMessage.DUPLICATE_ID.getMessage())
            );
        }

        return ResponseEntity.ok(
                ResponseMessage.success(ResponseCode.REQUEST_SUCCESS.getCode(), "중복된 항목이 없습니다.", null)
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseMessage<TokenResponseDto>> refreshAccessToken(@RequestHeader("Authorization") String refreshToken) {
        String username = userService.validateAndExtractUsername(refreshToken);
        String newAccessToken = userService.generateAccessToken(username);

        TokenResponseDto tokenResponse = new TokenResponseDto(newAccessToken);
        return ResponseEntity.ok(
                ResponseMessage.success(ResponseCode.REQUEST_SUCCESS.getCode(), "새로운 액세스 토큰이 발급되었습니다.", tokenResponse)
        );
    }

    @GetMapping("/users/profile")
    public ResponseEntity<ResponseMessage<UserDto>> getUserProfile() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        UserDto userDto = UserDto.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImageUrl())
                .build();

        return ResponseEntity.ok(
                ResponseMessage.success(ResponseCode.REQUEST_SUCCESS.getCode(), "사용자 프로필 조회 성공", userDto)
        );
    }

    @PatchMapping("/users/profile")
    public ResponseEntity<ResponseMessage<Void>> updateProfile(
            @RequestPart("data") String jsonData,
            @RequestPart(value = "image", required = false) MultipartFile profileImage) {
        String username = getCurrentUsername();
        try {
            UserDto userDto = objectMapper.readValue(jsonData, UserDto.class);
            userService.updateProfile(username, userDto, profileImage);
            return ResponseEntity.ok(
                    ResponseMessage.success(ResponseCode.REQUEST_SUCCESS.getCode(), "프로필 수정 성공", null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseMessage.fail(ResponseCode.SERVER_ERROR.getCode(), "프로필 수정 중 오류가 발생했습니다.")
            );
        }
    }

    @GetMapping("/profiles/statistics")
    public ResponseEntity<ResponseMessage<Map<String, Object>>> getProfileStatistics() {
        String username = getCurrentUsername();
        Map<String, Object> statistics = userService.getProfileStatistics(username);
        return ResponseEntity.ok(
                ResponseMessage.success(ResponseCode.REQUEST_SUCCESS.getCode(), "프로필 통계 조회 성공", statistics)
        );
    }

    @PatchMapping("/profile-image")
    public ResponseEntity<ResponseMessage<Void>> updateProfileImage(@RequestPart("image") MultipartFile profileImage) {
        String username = getCurrentUsername();
        userService.updateProfile(username, null, profileImage);
        return ResponseEntity.ok(
                ResponseMessage.success(ResponseCode.REQUEST_SUCCESS.getCode(), "프로필 이미지가 업데이트되었습니다.", null)
        );
    }

    @PutMapping("/profiles")
    public ResponseEntity<ResponseMessage<Void>> updateProfile(@RequestBody UserDto userDto) {
        String username = getCurrentUsername();
        userService.updateProfile(username, userDto, null);
        return ResponseEntity.ok(
                ResponseMessage.success(ResponseCode.REQUEST_SUCCESS.getCode(), "프로필 수정 성공", null)
        );
    }
}
