package com.paassible.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode implements BaseResponseCode {
    OK(HttpStatus.OK, "OK", "요청이 성공적으로 처리되었습니다."),
    CREATED(HttpStatus.CREATED, "CREATED", "리소스가 생성되었습니다."),
    MODIFIED(HttpStatus.NO_CONTENT, "MODIFIED", "리소스가 수정되었습니다."),
    DELETED(HttpStatus.NO_CONTENT, "DELETED", "리소스가 삭제되었습니다."),

    // user
    LOGIN(HttpStatus.OK, "LOGIN", "로그인이 완료되었습니다."),
    LOGOUT(HttpStatus.OK, "LOGOUT", "로그아웃이 완료되었습니다."),
    WITHDRAWAL(HttpStatus.OK, "WITHDRAWAL", "회원 탈퇴되었습니다."),
    AGREE(HttpStatus.OK, "AGREE", "약관 동의가 완료되었습니다."),

    // chat
    INVITED(HttpStatus.OK, "INVITED", "채팅방에 초대되었습니다."),
    LEAVED(HttpStatus.OK, "LEAVED", "채팅방을 퇴장하였습니다."),
    READ(HttpStatus.OK, "READ", "메시지 읽음 처리가 되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}