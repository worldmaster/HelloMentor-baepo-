package com.kh.hellomentor.member.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Calendar {
    private int taskId;
    private int userNo;
    private String todoContent;
    private Date todoDeadline;
}
