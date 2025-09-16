package com.paassible.boardservice.shortcut.service;


import com.paassible.boardservice.board.exception.BoardException;
import com.paassible.boardservice.board.service.BoardMemberService;
import com.paassible.boardservice.shortcut.dto.ShortcutRequest;
import com.paassible.boardservice.shortcut.dto.ShortcutResponse;
import com.paassible.boardservice.shortcut.entity.Shortcut;
import com.paassible.boardservice.shortcut.repository.ShortcutRepository;
import com.paassible.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShortcutService {

    private final ShortcutRepository shortcutRepository;
    private final BoardMemberService boardMemberService;

    public List<ShortcutResponse> getShortcuts(Long userId, Long boardId) {
        boardMemberService.validateUserInBoard(userId, boardId);

        return shortcutRepository.findByBoardId(boardId).stream()
                .map(ShortcutResponse::from)
                .toList();
    }

    public void createShortcut(Long userId, Long boardId, ShortcutRequest request) {
        boardMemberService.validateUserInBoard(userId, boardId);

        Shortcut shortcut = Shortcut.builder()
                .boardId(boardId)
                .name(request.getName())
                .url(request.getUrl())
                .build();
        shortcutRepository.save(shortcut);
    }

    public void deleteShortcut(Long boardId, Long shortcutId, Long userId) {
        boardMemberService.validateUserInBoard(userId, boardId);

        Shortcut shortcut = shortcutRepository.findByIdAndBoardId(shortcutId, boardId)
                .orElseThrow(() -> new BoardException(ErrorCode.SHORTCUT_NOT_FOUND));

        shortcutRepository.delete(shortcut);
    }
}
