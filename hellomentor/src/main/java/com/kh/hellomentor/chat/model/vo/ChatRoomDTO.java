package com.kh.hellomentor.chat.model.vo;


import lombok.*;
import org.springframework.web.socket.WebSocketSession;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class ChatRoomDTO {
    private String roomId;
    private String name;
    private String chatRoomType;
    private int relatedNo;
    private Set<WebSocketSession> sessions = new HashSet<>();

    public static ChatRoomDTO createStudyRoom(String name, int postNo) {
        ChatRoomDTO room = new ChatRoomDTO();

        room.roomId = UUID.randomUUID().toString();
        room.name = name;
        room.chatRoomType = "S";
        room.relatedNo = postNo;
        return room;
    }

    public static ChatRoomDTO createRoom(String name, int regisNo) {
        ChatRoomDTO room = new ChatRoomDTO();

        room.roomId = UUID.randomUUID().toString();
        room.name = name;
        room.relatedNo = regisNo;
        room.chatRoomType = "M";
        return room;
    }
}
