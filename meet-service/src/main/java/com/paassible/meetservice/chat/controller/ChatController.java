package com.paassible.meetservice.chat.controller;

import com.paassible.meetservice.chat.dto.ChatMessage;
import com.paassible.meetservice.meet.entity.Meet;
import com.paassible.meetservice.meet.service.MeetValidator;
import com.paassible.meetservice.util.ChatKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final RedisTemplate<String, ChatMessage> chatRedis;
    private final RedisTemplate<String,String> stringRedis;
    private final MeetValidator meetValidator;


    @MessageMapping("/meet/{meetId}/chat")
    public void handleChat(@DestinationVariable Long meetId,
                           ChatMessage message,
                           Principal principal) {

        if (principal == null) {
            log.warn("메시지 폐기: 인증되지 않은 사용자 meetId={}, content={}", meetId, message.content());
            return;
        }

        try{
            Long authUserId = Long.valueOf(principal.getName());

            Meet meet = meetValidator.validateMeetOngoing(meetId);
            meetValidator.validateUserInBoard(meet.getBoardId(), authUserId);

            Long chatId = stringRedis.opsForValue()
                    .increment("chat:meet:" + meetId + ":id-seq");


            ChatMessage enriched = new ChatMessage(
                    chatId,
                    meetId,
                    authUserId,
                    message.senderName(),
                    message.targetUserId(),
                    message.content(),
                    LocalDateTime.now()
            );

            if (enriched.targetUserId() != null) {
                simpMessagingTemplate.convertAndSendToUser(
                        enriched.targetUserId().toString(), "/queue/chat", enriched);
                writeListWithTtl(ChatKeys.dmChat(meetId, enriched.targetUserId()), enriched, 12, TimeUnit.HOURS);
                writeListWithTtl(ChatKeys.dmChat(meetId, enriched.senderId()), enriched, 12, TimeUnit.HOURS);
                stringRedis.opsForSet().add(ChatKeys.dmUsersIndex(meetId), enriched.targetUserId().toString());
                stringRedis.opsForSet().add(ChatKeys.dmUsersIndex(meetId), enriched.senderId().toString());
            } else {

                simpMessagingTemplate.convertAndSend("/topic/meet/" + meetId + "/chat/public", enriched);
                writeListWithTtl(ChatKeys.publicChat(meetId), enriched, 12, TimeUnit.HOURS);
            }

        }catch (NumberFormatException e){
            log.error("사용자 ID 형식이 올바르지 않음: {}", principal.getName(), e);
        }catch (Exception e){
            log.error("채팅 메시지 처리 실패. meetId={}, userId={}",
                    meetId, principal.getName(), e);
            //에러 메시지 클라이언트에 전송 고려하기
        }

    }

    private void writeListWithTtl(String key, ChatMessage chatMessage, int ttl, TimeUnit timeUnit) {
        try {
            Boolean isNew = !Boolean.TRUE.equals(chatRedis.hasKey(key));
            chatRedis.opsForList().rightPush(key, chatMessage);
            if (Boolean.TRUE.equals(isNew)) {
                chatRedis.expire(key, ttl, timeUnit);
            }
        } catch (Exception e) {
            log.error("Redis 채팅 저장 실패 key={}", key, e);
        }
    }
}
