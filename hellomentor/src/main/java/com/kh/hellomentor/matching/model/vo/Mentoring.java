package com.kh.hellomentor.matching.model.vo;


import com.kh.hellomentor.member.model.vo.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mentoring {
    private int regisNo;
    private int userNo;
    private String title;
    private String content1;
    private String content2;
    private String content3;
    private String codeLang;


    private String userId;
    private String introduction;
    private Member member;
}
