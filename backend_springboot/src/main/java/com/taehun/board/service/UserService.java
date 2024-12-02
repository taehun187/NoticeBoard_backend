package com.taehun.board.service;

import com.taehun.board.dto.LoginRequestDto;
import com.taehun.board.dto.ResetPasswordDto;
import com.taehun.board.dto.UserDto;
import com.taehun.board.entity.Post;
import com.taehun.board.entity.User;
import com.taehun.board.repository.PostRepository;
import com.taehun.board.repository.UserRepository;
import com.taehun.board.security.jwt.TokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;
    private final TokenProvider tokenProvider;

    // 회원 등록
    public void register(UserDto userDto, MultipartFile profileImage) {
        validateRegistration(userDto);

        String profileImageUrl = uploadProfileImage(profileImage);

        User user = User.builder()
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .profileImageUrl(profileImageUrl)
                .build();

        userRepository.save(user);
    }

    // 프로필 통계 조회
    public Map<String, Object> getProfileStatistics(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        long totalViews = postRepository.findAllByWriter(user).stream()
                .mapToLong(Post::getViews)
                .sum();

        long totalPosts = postRepository.countByWriter(user);

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalViews", totalViews);
        statistics.put("totalPosts", totalPosts);
        statistics.put("joinDate", user.getCreatedAt().toString());

        return statistics;
    }

    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll(); // 모든 사용자 엔티티 조회
        return users.stream()
                .map(user -> UserDto.builder()
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .profileImageUrl(user.getProfileImageUrl())
                        .build())
                .toList(); // 엔티티를 DTO로 변환 후 반환
    }

    // 프로필 수정
    public void updateProfile(String username, UserDto userDto, MultipartFile profileImage) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        if (userDto.getUsername() != null) {
            user.setUsername(userDto.getUsername());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        if (profileImage != null && !profileImage.isEmpty()) {
            String profileImageUrl = uploadProfileImage(profileImage);
            user.setProfileImageUrl(profileImageUrl);
        }

        userRepository.save(user);
    }

    @Transactional
    public void resetPassword(String username, ResetPasswordDto resetPasswordDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(resetPasswordDto.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("기존 비밀번호가 일치하지 않습니다.");
        }

        if (resetPasswordDto.getCurrentPassword().equals(resetPasswordDto.getNewPassword())) {
            throw new RuntimeException("새 비밀번호는 기존 비밀번호와 달라야 합니다.");
        }

        if (!resetPasswordDto.getNewPassword().equals(resetPasswordDto.getConfirmPassword())) {
            throw new RuntimeException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        user.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
        userRepository.save(user);
    }

    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));
        userRepository.delete(user);
    }

    private void validateRegistration(UserDto userDto) {
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new RuntimeException("이미 사용 중인 아이디입니다.");
        }
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }
        if (!userDto.getPassword().equals(userDto.getCheckPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
    }

    private String uploadProfileImage(MultipartFile profileImage) {
        if (profileImage == null || profileImage.isEmpty()) {
            return null;
        }
        if (profileImage.getSize() > 10 * 1024 * 1024) {
            throw new RuntimeException("파일 크기가 10MB를 초과합니다.");
        }

        try (InputStream inputStream = profileImage.getInputStream()) {
            return s3Service.uploadFile(inputStream, profileImage.getOriginalFilename(), profileImage.getContentType());
        } catch (Exception e) {
            throw new RuntimeException("프로필 이미지 업로드 실패: " + e.getMessage());
        }
    }

    public User login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByUsername(loginRequestDto.getUsername())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        return user;
    }

    public boolean isExistAccount(String email, String id) {
        boolean emailExists = userRepository.findByEmail(email).isPresent();
        boolean idExists = userRepository.findByUsername(id).isPresent();
        return emailExists || idExists;
    }

    public String generateAccessToken(String username) {
        return tokenProvider.generateToken(username, false);
    }

    public String generateRefreshToken(String username) {
        return tokenProvider.generateToken(username, true);
    }

    public void saveRefreshToken(String username, String refreshToken) {
        tokenProvider.saveRefreshToken(username, refreshToken, 60 * 60 * 24 * 7); // 7일 유효
    }

    public void invalidateToken(String token) {
        tokenProvider.blacklistAccessToken(token, tokenProvider.getExpirationFromToken(token).getTime() - System.currentTimeMillis());
    }

    public String validateAndExtractUsername(String token) {
        tokenProvider.validateRefreshTokenOrThrow(tokenProvider.getUsernameFromToken(token), token);
        return tokenProvider.getUsernameFromToken(token);
    }
}
