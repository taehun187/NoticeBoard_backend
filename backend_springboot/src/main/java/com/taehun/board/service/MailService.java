package com.taehun.board.service;

import com.taehun.board.dto.EmailVerificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;

    @Value("${spring.data.redis.ttl}")
    private long verificationCodeTtl;

    public void sendVerificationCode(String email) {
        if (redisTemplate.hasKey(email)) {
            throw new RuntimeException("인증 번호는 일정 시간 내에 재전송할 수 없습니다.");
        }

        String code = generateCode();
        redisTemplate.opsForValue().set(email, code, verificationCodeTtl, TimeUnit.SECONDS);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setTo(email);
            helper.setSubject("이메일 인증 코드");
            helper.setText("인증번호는 다음과 같습니다: " + code, false);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("이메일 전송 실패: " + e.getMessage());
        }
    }

    public boolean verifyCode(EmailVerificationDto dto) {
        String storedCode = redisTemplate.opsForValue().get(dto.getEmail());
        if (storedCode == null) {
            throw new RuntimeException("인증번호가 존재하지 않습니다.");
        }
        if (!storedCode.equals(dto.getCode())) {
            throw new RuntimeException("인증번호가 일치하지 않습니다.");
        }
        return true;
    }

    private String generateCode() {
        return String.format("%06d", (int) (Math.random() * 1000000));
    }
}
