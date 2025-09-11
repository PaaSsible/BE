package com.paassible.boardservice.shortcut.controller;

import com.paassible.boardservice.shortcut.dto.ShortcutRequest;
import com.paassible.boardservice.shortcut.dto.ShortcutResponse;
import com.paassible.boardservice.shortcut.service.ShortcutService;
import com.paassible.common.response.ApiResponse;
import com.paassible.common.response.SuccessCode;
import com.paassible.common.security.dto.UserJwtDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board/{boardId}")
@Tag(name = "바로가기 API", description = "바로가기 조회, 생성, 삭제")
public class ShortcutController {

    private final ShortcutService shortcutService;

    @GetMapping("/shortcuts")
    @Operation(summary = "바로가기 조회", description = "바로가기를 조회합니다.")
    public ResponseEntity<ApiResponse<List<ShortcutResponse>>> getShortcuts(
            @PathVariable Long boardId,
            @AuthenticationPrincipal UserJwtDto user) {

        List<ShortcutResponse> response = shortcutService.getShortcuts(user.getUserId(), boardId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }

    @PostMapping("/shortcut")
    @Operation(summary = "바로가기 생성", description = "바로가기를 추가합니다.")
    public ResponseEntity<ApiResponse<Void>> createShortcut(
            @PathVariable Long boardId,
            @AuthenticationPrincipal UserJwtDto user,
            @RequestBody ShortcutRequest request) {

        shortcutService.createShortcut(user.getUserId(), boardId, request);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.CREATED));
    }

    @DeleteMapping("/shortcut/{shortcutId}")
    @Operation(summary = "바로가기 삭제", description = "바로가기를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteShortcut(
            @PathVariable Long boardId,
            @PathVariable Long shortcutId,
            @AuthenticationPrincipal UserJwtDto user) {

        shortcutService.deleteShortcut(user.getUserId(), boardId, shortcutId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.DELETED));
    }
}
