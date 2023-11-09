package com.kh.hellomentor.notification.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    private int notifId;
    private int userNo;
    private String notifContent;
    private Date createDate;
    private String readStatus;
    private String boardType;
    private int postNo;

}
