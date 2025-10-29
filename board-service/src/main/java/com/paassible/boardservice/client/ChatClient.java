package com.paassible.boardservice.client;

import com.paassible.boardservice.board.dto.CommunicationResponse;
import com.paassible.boardservice.chat.dto.ChatRoomIdResponse;
import com.paassible.boardservice.chat.dto.ChatRoomInviteRequest;
import com.paassible.boardservice.chat.dto.ChatRoomRequest;
import com.paassible.boardservice.chat.dto.ChatRoomResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "chat-service", url = "${chat-service.url}")
public interface ChatClient {

    @PostMapping("/chats/internal/board")
    void createGroupChat(@RequestParam("userId") Long userId,
                             @RequestParam("boardId") Long boardId,
                             @RequestParam("boardName") String boardName);

    @PostMapping("/chats/internal/participant")
    void addParticipant(@RequestParam("userId") Long userId,
                        @RequestParam("boardId") Long boardId);

    @PostMapping("/chats/internal/rooms")
    ChatRoomIdResponse createChatRoom(@RequestParam("userId") Long userId,
                                      @RequestParam("boardId") Long boardId,
                                      @RequestBody ChatRoomRequest request);

    @PostMapping("/chats/internal/rooms/{roomId}/invite")
    void inviteParticipants(@RequestParam("userId") Long userId,
                            @RequestParam("boardId") Long boardId,
                            @PathVariable("roomId") Long roomId,
                            @RequestBody ChatRoomInviteRequest request);

    @GetMapping("/chats/internal/rooms")
    List<ChatRoomResponse> getChatRooms(@RequestParam("userId") Long userId,
                                        @RequestParam("boardId") Long boardId);

    @GetMapping("/chats/internal/communication")
    CommunicationResponse getCommunicationFrequency(@RequestParam("userId") Long userId,
                                                    @RequestParam("boardId") Long boardId);

}