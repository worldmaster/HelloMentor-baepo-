package com.kh.hellomentor.member.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
    private int userNo;
    private String userId;
    private String userPwd;
    private String userName;
    private String phone;
    private String email;
    private Date enrollDate;
    private String status;
    private String introduction;
    private String memberType;
    private int token;
    private String grade;
    private String profile;
}
