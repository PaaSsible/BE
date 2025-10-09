package com.paassible.userservice.user.service;

import com.paassible.common.response.ErrorCode;
import com.paassible.common.security.jwt.Role;
import com.paassible.userservice.auth.oauth.GoogleUserInfo;
import com.paassible.userservice.user.dto.ProfileRequest;
import com.paassible.userservice.user.dto.UserResponse;
import com.paassible.userservice.user.entity.User;
import com.paassible.userservice.user.exception.UserException;
import com.paassible.userservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
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

    public UserResponse getProfile(Long userId) {
        User user = getUser(userId);
        return UserResponse.from(user);
    }

    @Transactional
    public void updateProfile(Long userId, ProfileRequest request, MultipartFile image) {
        User user = getUser(userId);

        String profileImageUrl = user.getProfileImageUrl();
        if (image != null && !image.isEmpty()) {
            profileImageUrl = saveProfileImage(image);
        }

        user.updateProfile(request.getNickname(), request.getUniversity(), request.getMajor(), profileImageUrl);
    }

    private String saveProfileImage(MultipartFile image) {
        // s3에 저정하는식으로 수정 필요
        return "url";
    }

    @Transactional
    public void agreeTerms(Long userId) {
        User user = getUser(userId);
        user.updateAgreedToTerms(true);
    }

    @Transactional
    public void updateRoleToMember(User user) {
        user.updateRole(Role.MEMBER);
    }
}
