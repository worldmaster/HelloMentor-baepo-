package com.kh.hellomentor.matching.model.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kh.hellomentor.matching.model.vo.Matching;
import com.kh.hellomentor.member.model.vo.Follow;
import com.kh.hellomentor.member.model.vo.Member;
import com.kh.hellomentor.member.model.vo.Payment;
import com.kh.hellomentor.member.model.vo.Profile;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


import com.kh.hellomentor.matching.model.vo.Mentoring;


@Repository
public class MatchingDao {



    @Autowired
    private SqlSessionTemplate sqlSession;

    public List<Mentoring> selectList(int currentPage, Map<String, Object> paramMap) {
        int offset = (currentPage -1) * 9;
        int limit  = 9;

        RowBounds rowBounds = new RowBounds(offset,limit);

        return sqlSession.selectList("matchingMapper.selectList",paramMap,rowBounds);
    }


    public int selectList(Mentoring mt) {

        return sqlSession.insert("matchingMapper.insertMentoring",mt);

    }


    public int selectMentorListCount(String searchOption, String keyword, int memberTypeId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("searchOption", searchOption);
        paramMap.put("keyword", keyword);
        paramMap.put("memberType",memberTypeId);
        return sqlSession.selectOne("matchingMapper.selectMentorListCount", paramMap);
    }

    public List<Mentoring> selectMentorList(String searchOption, String keyword, int page, int pageSize, Map<String, Object> paramMap) {
        int start = (page - 1) * pageSize;
        paramMap.put("searchOption", searchOption);
        paramMap.put("keyword", keyword);
        paramMap.put("start", start);
        paramMap.put("pageSize", pageSize);
        System.out.println(paramMap);
        return sqlSession.selectList("matchingMapper.selectMentorList", paramMap);
    }

    public int selectListCount(int memberTypeId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("memberType",memberTypeId);
        return sqlSession.selectOne("matchingMapper.selectSListCount",paramMap);
    }


    public List<Mentoring> getSideMentorList(int page, int pageSize, String memberType) {
        //페이징 정보를 이용하여 시작 위치를 계산합니다.
        int start = (page - 1) * pageSize;
        // MyBatis 매퍼를 사용하여 데이터베이스에서 페이징된 데이터를 조회합니다.
        Map<String, Object> params = new HashMap<>();
        params.put("start", start);
        params.put("pageSize", pageSize);
        params.put("memberType",memberType);

        return sqlSession.selectList("matchingMapper.getSideMentorList", params);
    }

    public Member getMemberByPostNo(int postNo) {
        return sqlSession.selectOne("matchingMapper.getMemberByPostNo", postNo);
    }

    public Profile getProfileByPostNo(int postNo) {
        return sqlSession.selectOne("matchingMapper.getProfileByPostNo", postNo);
    }

    public Mentoring getMentoringByPostNo(int postNo) {
        return sqlSession.selectOne("matchingMapper.getMentoringByPostNo", postNo);
    }


    public int insertPaymentMatching(Map<String, Object> paymentData) {
        int result = 0;
        int result1 = 0;
        int updateTokenResult = 0;

        Payment p = (Payment) paymentData.get("payment");
        Matching m = (Matching) paymentData.get("matching");
        Member member = (Member) paymentData.get("member");

        result = sqlSession.insert("matchingMapper.insertPayment",p);

        result1 = sqlSession.insert("matchingMapper.insertMathcing",m);

        updateTokenResult = sqlSession.update("matchingMapper.updateToken",member);

        return result * result1 * updateTokenResult;
    }

    //변경된 토큰을 세션에 담기위한 조회
    public int getUpdateToken(int userNo) {
        return sqlSession.selectOne("matchingMapper.selectToken",userNo);
    }

    //제안하기
    public int suggestMatching(Map<String, Object> suggestData) {
        int result = 0;
        Matching m = (Matching) suggestData.get("matching");
        result = sqlSession.insert("matchingMapper.suggestMatching",m);

        return result;
    }

    public int follow(Follow follow) {
        int result = 0;
        result = sqlSession.insert("matchingMapper.follow",follow);
        return result;
    }

    public int unfollow(Follow follow) {
        int result = 0;
        result = sqlSession.insert("matchingMapper.unfollow",follow);
        return result;
    }

    public List<Member> getMentorList(int userNo) {
        return sqlSession.selectList("matchingMapper.getMentorList",userNo);
    }


    public List<Profile> getMentorProfileList(int userNo) {
        return sqlSession.selectList("matchingMapper.getMentorProfileList",userNo);
    }

    public List<Mentoring> getMentoringList(int userNo) {
        return sqlSession.selectList("matchingMapper.getMentoringList",userNo);
    }

    public List<Matching> getMatchingList(int userNo) {
        return sqlSession.selectList("matchingMapper.getMatchingList",userNo);
    }

    public List<Member> getMentorList2(int userNo) {
        return sqlSession.selectList("matchingMapper.getMentorList2",userNo);
    }

    public List<Profile> getMentorProfileList2(int userNo) {
        return sqlSession.selectList("matchingMapper.getMentorProfileList2",userNo);
    }

    public List<Mentoring> getMentoringList2(int userNo) {
        return sqlSession.selectList("matchingMapper.getMentoringList2",userNo);
    }

    public List<Matching> getMatchingList2(int userNo) {
        return sqlSession.selectList("matchingMapper.getMatchingList2",userNo);
    }

    public void mentoring_cancel(int userNo, int regisNo) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userNo", userNo);
        paramMap.put("regisNo", regisNo);
        sqlSession.delete("matchingMapper.mentoringCancel", paramMap);
    }

    public void mentoring_accept(int userNo, int regisNo, int loginuserNo) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userNo", userNo);
        paramMap.put("regisNo", regisNo);
        paramMap.put("loginNo", loginuserNo);
        sqlSession.update("matchingMapper.mentoringAccept", paramMap);
    }

    public int mentorupdateToken(Map<String, Object> mentorToken) {
        int result = 0;

        result = sqlSession.update("matchingMapper.mentorupdateToken",mentorToken);

        return result;
    }

}
