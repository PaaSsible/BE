package com.paassible.meetservice.util;

public final class MeetKeys {

    public static String participants(long meetId) {
        return "meeting:" + meetId + ":participants";
    }

    public static String lastSpokeAt(long meetId)  {
        return "meeting:" + meetId + ":lastSpokeAt";
    }
    public static String lastPicked(long meetId)   {
        return "meeting:" + meetId + ":lastPicked";
    }

    public static String speaking(long meetId) {
        return "meeting:" + meetId + ":speaking";
    }

    public static String lastSpokeAtPattern() {
        return "meeting:*:lastSpokeAt";
    }

    public static String lastSpokeAtAnyPattern() {
        return "meeting:*:lastSpokeAt";
    }

    public static String silentSet(long meetId) {
        return "meeting:" + meetId + ":silent";
    }
}

