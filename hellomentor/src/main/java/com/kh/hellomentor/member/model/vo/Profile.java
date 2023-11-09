package com.kh.hellomentor.member.model.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    private int attachmentId;
    private int userNo;
    private String ChangeName;
    private long fileSize;
    private String filePath;
    private String originName;

}
