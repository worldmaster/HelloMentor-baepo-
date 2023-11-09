package com.kh.hellomentor.matching.model.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyApplicant {
    private int postNo;
    private int applicantNo;
    private String userNo;
    private String status;
}
