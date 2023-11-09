package com.kh.hellomentor.board.model.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attachment {
    private int attachmentId;
    private int postNo;
    private String changeName;
    private long fileSize;
    private String filePath;
    private String originName;
}
