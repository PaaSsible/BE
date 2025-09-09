package com.paassible.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

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

    // board
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "B001", "존재하지 않는 보드입니다."),
    BOARD_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "B002", "해당 보드에 없는 사용자입니다."),
    OWNER_NOT_FOUND(HttpStatus.NOT_FOUND, "B003", "보드 소유자가 존재하지 않습니다."),
    BOARD_TASK_NOT_FOUND(HttpStatus.NOT_FOUND, "T004", "해당 보드에는 없는 업무입니다"),
    BOARD_UPDATE_OWNER(HttpStatus.FORBIDDEN, "B005", "보드 소유자만 보드를 수정할 수 있습니다"),

    // task
    TASK_NOT_FOUND(HttpStatus.NOT_FOUND, "T001", "존재하지 않는 업무입니다."),
    TASK_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "T002", "존재하지 않는 업무 댓글입니다"),
    COMMENT_NOT_OWNER(HttpStatus.FORBIDDEN, "T003", "본인이 작성한 댓글만 수정/삭제할 수 있습니다."),

    // user
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "존재하지 않는 사용자입니다."),

    //recruit
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "존재하지 않는 게시물입니다."),
    INVALID_POSITION(HttpStatus.BAD_REQUEST, "R002", "잘못된 포지션 입력입니다."),
    INVALID_STACK(HttpStatus.BAD_REQUEST, "R003", "잘못된 스택 입력입니다."),

    //meet
    MEET_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "존재하지 않는 회의입니다."),
    MEET_ALREADY_JOINED(HttpStatus.CONFLICT, "M002", "이미 이 회의에 참가했습니다."),
    MEET_NOT_PARTICIPANT(HttpStatus.BAD_REQUEST, "M003", "회의 참가자가 아닙니다."),
    MEET_ALREADY_ENDED(HttpStatus.BAD_REQUEST, "M002", "이미 종료된 회의입니다."),
    PARTICIPANT_ALREADY_LEFT(HttpStatus.CONFLICT, "M005", "이미 퇴장한 참가자입니다."),



    // chat
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CR001", "존재하지 않는 채팅방입니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
