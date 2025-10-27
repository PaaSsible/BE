package com.paassible.meetservice.util;

public final class MeetKeys {
    private MeetKeys() {}
    public static String participants(long meetId)   { return "meeting:" + meetId + ":participants"; }
    public static String speaking(long meetId)       { return "meeting:speaking:" + meetId; }          // Hash {userId -> lastTrue(ms)}
    public static String lastSpokeAt(long meetId)    { return "meeting:" + meetId + ":lastSpokeAt"; }  // ZSET score=epochMillis
    public static String silentSet(long meetId)      { return "meeting:" + meetId + ":silent"; }       // Set (optional, 이전 스냅샷)
    public static String lastSpokeAtAnyPattern()     { return "meeting:*:lastSpokeAt"; }               // for SCAN
}

