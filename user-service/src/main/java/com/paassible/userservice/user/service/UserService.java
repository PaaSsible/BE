package com.paassible.userservice.user.service;

import com.paassible.common.response.ErrorCode;
import com.paassible.common.security.jwt.Role;
import com.paassible.userservice.auth.oauth.GoogleUserInfo;
import com.paassible.userservice.user.dto.UserResponse;
import com.paassible.userservice.user.entity.User;
import com.paassible.userservice.user.exception.UserException;
import com.paassible.userservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
    }

    public UserResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        return UserResponse.from(user);
    }

    @Transactional
    public User findOrCreateUser(GoogleUserInfo info) {
        return userRepository
                .findUserBySocialId(info.getSub())
                .map(
                        user -> {
                            if (user.isDeleted()) {
                                user.updateDeleted(false);
                            }
                            return user;
                        })
                .orElseGet(
                        () -> {
                            User newUser =
                                    User.builder()
                                            .socialId(info.getSub())
                                            .email(info.getEmail())
                                            .nickname(info.getName())
                                            .role(Role.PENDING)
                                            .agreedToTerms(false)
                                            .deleted(false)
                                            .build();
                            return userRepository.save(newUser);
                        });
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
        user.updateDeleted(true);
    }

    @Transactional
    public void agreeTerms(Long userId) {
        User user = getUser(userId);
        user.updateAgreedToTerms(true);
    }

    // 프로필 설정 시에 확인 누르면 바로 같이 role 업데이트 되도록
    @Transactional
    public void updateRoleToMember(Long userId) {
        User user = getUser(userId);
        user.updateRole(Role.MEMBER);
    }
}
