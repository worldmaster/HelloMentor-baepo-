package com.kh.hellomentor.matching.model.service;

import java.util.List;
import java.util.Map;


import com.kh.hellomentor.matching.model.vo.Matching;
import com.kh.hellomentor.matching.model.vo.Mentoring;
import com.kh.hellomentor.member.model.vo.Follow;
import com.kh.hellomentor.member.model.vo.Member;
import com.kh.hellomentor.member.model.vo.Profile;

public interface MatchingService {

    List<Mentoring> selectList(int currentPage, Map<String, Object> paramMap);

    public int insertMentoring(Mentoring mt);


    int selectMentorListCount(String searchOption, String keyword, int memberTypeId);

    List<Mentoring> selectMentorList(String searchOption, String keyword, int page, int pageSize, Map<String, Object> paramMap);

    int selectListCount(int memberTypeId);

    List<Mentoring> getSideMentorList(int page, int pageSize, String memberType);


    Member getMemberByPostNo(int postNo);

    Profile getProfileByPostNo(int postNo);

    Mentoring getMentoringByPostNo(int postNo);

    int insertPaymentMatching(Map<String, Object> paymentData);

    int getUpdateToken(int userNo);

    int suggestMatching(Map<String, Object> suggestData);

    int follow(Follow follow);

    int unfollow(Follow follow);

    List<Member> getMentorList(int userNo);

    List<Profile> getMentorProfileList(int userNo);

    List<Mentoring> getMentoringList(int userNo);

    List<Matching> getMatchingList(int userNo);

    List<Member> getMentorList2(int userNo);

    List<Profile> getMentorProfileList2(int userNo);

    List<Mentoring> getMentoringList2(int userNo);

    List<Matching> getMatchingList2(int userNo);

    void mentoring_cancel(int userNo, int regisNo);

    void mentoring_accept(int userNo, int regisNo, int loginuserNo);

    int mentorupdateToken(Map<String, Object> mentorToken);
}
