package com.kh.hellomentor.chat.model.vo;


import lombok.*;


@Getter
@Setter
public class ChatMessageDTO {
    private String roomId;
    private String writer;
    private int userNo;
    private String profile;
    private String message;
    private String sendDateTime;
}
