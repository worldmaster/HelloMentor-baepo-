package com.kh.hellomentor.member.model.service;

import com.kh.hellomentor.member.model.vo.Calendar;
import com.kh.hellomentor.member.model.vo.Member;
import com.kh.hellomentor.member.model.vo.Payment;
import com.kh.hellomentor.member.model.vo.Profile;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface MemberService {

    Member loginUser(Member m);

    int insertMember(Member m);
    
    int idCheck(String userId);

    List<Member> getFollowList(int userNo);

    List<Profile> getFollowingProfileList(int userNo);

    List<Member> getFollowerList(int userNo);

    void updateMember(Member loginUser);

    List<Profile> getFollowerProfileList(int userNo);

    void updateProfileImg(Profile profile);

    void insertProfileImg(Profile profile);

    Boolean isProfileImgExists(int userNo);

    List<Payment> getPaymentHistory(int userNo, String type);


    void saveMemo(Calendar memoRequest);

    void updateMemo(Calendar memoRequest);

    boolean isMemoExists(Calendar memoRequest);

    void deleteMemo(Calendar memoRequest);

    Calendar loadMemo(Calendar memoRequest);

    boolean performExit(int userNo);

    //정승훈 토큰충전
    int insertUpdateToken(Map<String, Object> tokenData);

    int exchangeToken(Member m);

    int getUpdateToken(int userNo);

    int paymentResult(int userNo);

}