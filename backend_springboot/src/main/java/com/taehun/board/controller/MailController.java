package com.taehun.board.controller;

import com.taehun.board.dto.EmailVerificationDto;
import com.taehun.board.message.MailMessage;
import com.taehun.board.message.ResponseCode;
import com.taehun.board.message.ResponseMessage;
import com.taehun.board.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    // 인증번호 전송
    @PostMapping("/send")
    public ResponseEntity<ResponseMessage<Void>> sendVerificationCode(@RequestBody EmailVerificationDto request) {
        mailService.sendVerificationCode(request.getEmail());
        return ResponseEntity.ok(
                ResponseMessage.success(ResponseCode.REQUEST_SUCCESS.getCode(), MailMessage.SEND_SUCCESS.getMessage(), null)
        );
    }

    // 인증번호 확인
    @PostMapping("/check")
    public ResponseEntity<ResponseMessage<Void>> verifyCode(@RequestBody EmailVerificationDto request) {
        try {
            mailService.verifyCode(request);
            return ResponseEntity.ok(
                    ResponseMessage.success(ResponseCode.REQUEST_SUCCESS.getCode(), MailMessage.VERIFY_SUCCESS.getMessage(), null)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    ResponseMessage.fail(ResponseCode.REQUEST_FAIL.getCode(), e.getMessage())
            );
        }
    }
}
