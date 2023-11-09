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
public class Payment {
    private int paymentNo;
    private int userNo;
    private Date paymentDate;
    private int price;
    private String payContent;
    private String payType;
}
