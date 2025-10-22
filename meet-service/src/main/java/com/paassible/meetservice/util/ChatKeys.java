package com.paassible.meetservice.util;

public class ChatKeys {
    private ChatKeys() {}

    public static String publicChat(Long meetId){
        return "chat:meet:"+meetId+":public";
    }

    public static String dmChat(Long meetId, Long userId){
        return "chat:meet:" + meetId + ":dm:" + userId;
    }

    public static String dmUsersIndex(Long meetId){
        return "chat:meet:"+meetId+":dm-users";
    }

    public static String participant(Long meetId){
        return "meeting:"+meetId+":participant";
    }
}
