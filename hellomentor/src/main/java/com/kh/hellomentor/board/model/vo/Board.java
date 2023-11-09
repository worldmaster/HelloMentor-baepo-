package com.kh.hellomentor.board.model.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {
    private int postNo;
    private String userNo;
    private String postTitle;
    private String postContent;
    private boolean isDeleted;
    private int views;
    private Date createDate;
    private String boardType;
}
