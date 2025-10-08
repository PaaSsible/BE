package com.paassible.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements BaseResponseCode {

    // common
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "C001", "입력값 검증에 실패했습니다."),
    JSON_PARSE_ERROR(HttpStatus.BAD_REQUEST, "C002", "잘못된 형식의 JSON 요청입니다."),
    MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "C003", "필수 파라미터가 누락되었습니다."),
    TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "C004", "요청 파라미터 타입이 잘못되었습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C005", "허용되지 않은 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C006", "서버 내부 오류가 발생했습니다."),
    FORBIDDEN_ACTION(HttpStatus.FORBIDDEN, "C007", "해당 작업에 대한 권한이 없습니다."),
    INVALID_CURSOR(HttpStatus.BAD_REQUEST, "C008", "유효하지 않은 커서입니다"),

    // auth
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A001", "리프레쉬 토큰이 만료되었습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "유효하지 않은 리프레쉬 토큰입니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "유효하지 않은 토큰입니다."),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "A004", "인증에 실패하였습니다."),
    TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "A005", "인증 토큰이 존재하지 않습니다."),
    INVALID_AUTHORIZATION_HEADER(HttpStatus.UNAUTHORIZED, "A006", "유효하지 않은 Authorization 헤더입니다"),
    GOOGLE_TOKEN_EMPTY(HttpStatus.INTERNAL_SERVER_ERROR, "A007", "구글 응답에 access_token이 없습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "A008", "권한이 부족합니다"),
    TERMS_NOT_AGREED(HttpStatus.FORBIDDEN, "A009", "약관에 동의하지 않았습니다"),

    // board
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "B001", "존재하지 않는 보드입니다."),
    BOARD_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "B002", "해당 보드에 없는 사용자입니다."),
    OWNER_NOT_FOUND(HttpStatus.NOT_FOUND, "B003", "보드 소유자가 존재하지 않습니다."),
    BOARD_TASK_NOT_FOUND(HttpStatus.NOT_FOUND, "T004", "해당 보드에는 없는 업무입니다"),
    BOARD_UPDATE_OWNER(HttpStatus.FORBIDDEN, "B005", "보드 소유자만 보드를 수정할 수 있습니다"),
    SHORTCUT_NOT_FOUND(HttpStatus.NOT_FOUND, "B006", "존재하지 않는 바로가기입니다."),

    // task
    TASK_NOT_FOUND(HttpStatus.NOT_FOUND, "T001", "존재하지 않는 업무입니다."),
    TASK_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "T002", "해당 보드의 업무에 존재하는 댓글이 없습니다."),
    COMMENT_NOT_OWNER(HttpStatus.FORBIDDEN, "T003", "본인이 작성한 댓글만 수정/삭제할 수 있습니다."),

    // user
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "존재하지 않는 사용자입니다."),

    //recruit

    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "존재하지 않는 게시물입니다."),
    INVALID_POSITION(HttpStatus.BAD_REQUEST, "P002", "잘못된 포지션 입력입니다."),
    INVALID_STACK(HttpStatus.BAD_REQUEST, "P003", "잘못된 스택 입력입니다."),
    INVALID_SORT_OPTION(HttpStatus.BAD_REQUEST, "P004", "잘못된 정렬 옵션입니다."),

    PARENT_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "P005", "존재하지 않는 부모 댓글입니다."),
    INVALID_COMMENT_DEPTH(HttpStatus.BAD_REQUEST, "P006", "대댓글의 대댓글은 작성할 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "P007", "존재하지 않는 댓글입니다."),
    COMMENT_UPDATE_FORBIDDEN(HttpStatus.FORBIDDEN, "P008", "댓글 수정 권한이 없습니다."),
    COMMENT_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN, "P009", "댓글 삭제 권한이 없습니다."),

    APPLICATION_ALREADY_EXISTS(HttpStatus.CONFLICT, "A001", "이미 신청한 지원입니다."),
    APPLICATION_UNAUTHORIZED(HttpStatus.FORBIDDEN, "A002", "해당 게시글의 지원자 관련 기능에 접근할 권한이 없습니다."),
    APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "A003", "존재하지 않는 지원입니다."),
    APPLICATION_MISMATCH(HttpStatus.BAD_REQUEST, "A004", "해당 모집글의 지원이 아닙니다."),
    CANNOT_APPLY_TO_OWN_POST(HttpStatus.FORBIDDEN, "A002", "자신이 작성한 모집글에는 지원할 수 없습니다."),


    //meet
    MEET_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "존재하지 않는 회의입니다."),
    MEET_ALREADY_JOINED(HttpStatus.CONFLICT, "M002", "이미 이 회의에 참가했습니다."),
    MEET_NOT_PARTICIPANT(HttpStatus.BAD_REQUEST, "M003", "회의 참가자가 아닙니다."),
    MEET_ALREADY_ENDED(HttpStatus.BAD_REQUEST, "M004", "이미 종료된 회의입니다."),
    PARTICIPANT_ALREADY_LEFT(HttpStatus.CONFLICT, "M005", "이미 퇴장한 참가자입니다."),
    MEET_ALREADY_EXISTS(HttpStatus.CONFLICT, "M006", "이미 진행 중인 회의가 존재합니다."),
    // chat
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CR001", "존재하지 않는 채팅방입니다."),
    CHAT_MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "CR002", "존재하지 않는 채팅 메시지입니다."),
    ROOM_PARTICIPANT_NOT_FOUND(HttpStatus.NOT_FOUND, "CR003", "존재하지 않는 채팅 메시지입니다."),
    FORBIDDEN_ROOM_ACCESS(HttpStatus.FORBIDDEN, "CR004", "해당 방에 존재하지 않는 멤버입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public static ErrorCode fromCode(String code) {
        return Arrays.stream(values())
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElse(INTERNAL_SERVER_ERROR);
    }
}
