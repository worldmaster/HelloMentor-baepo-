package com.kh.hellomentor.member.model.service;

import com.kh.hellomentor.member.model.vo.Calendar;
import com.kh.hellomentor.member.model.vo.Payment;
import com.kh.hellomentor.member.model.vo.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.hellomentor.member.model.dao.MemberDao;
import com.kh.hellomentor.member.model.vo.Member;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberDao memberDao;

    @Override
    public Member loginUser(Member m) {
        return memberDao.loginUser(m);
    }

    @Override
    public int insertMember(Member m) {
        return memberDao.insertMember(m);
    }
    
    @Override
    public int idCheck(String userId) {
    	return memberDao.idCheck(userId);
    }
    
    @Override
    public List<Member> getFollowList(int userNo) {
        return memberDao.getFollowList(userNo);
    }

    @Override
    public List<Profile> getFollowingProfileList(int userNo) {
        return memberDao.getFollowingProfileList(userNo);
    }

    @Override
    public List<Member> getFollowerList(int userNo) {
        return memberDao.getFollwerList(userNo);
    }

    @Override
    public void updateMember(Member updatedMember) {
        memberDao.updateMember(updatedMember);
    }

    @Override
    public List<Profile> getFollowerProfileList(int userNo) {
        return memberDao.getFollowerProfileList(userNo);
    }

    @Override
    public void updateProfileImg(Profile profile) {
        memberDao.updateProfileImg(profile);
    }

    @Override
    public void insertProfileImg(Profile profile) {
        memberDao.insertProfileImg(profile);
    }

    @Override
    public Boolean isProfileImgExists(int userNo) {
       return memberDao.isProfileImgExists(userNo);
    }

    @Override
    public List<Payment> getPaymentHistory(int userNo, String type) {return memberDao.getPaymentHistory(userNo, type);}

    @Override
    public void saveMemo(Calendar memoRequest) {
        memberDao.saveMemo(memoRequest);
    }

    @Override
    public void updateMemo(Calendar memoRequest) {
        memberDao.updateMemo(memoRequest);
    }

    @Override
    public boolean isMemoExists(Calendar memoRequest) {
        return memberDao.isMemoExists(memoRequest);
    }

    @Override
    public void deleteMemo(Calendar memoRequest) {
        memberDao.deleteMemo(memoRequest);
    }

    @Override
    public Calendar loadMemo(Calendar memoRequest) {
        return memberDao.loadMemo(memoRequest);
    }

    @Override
    public boolean performExit(int userNo) {
        return memberDao.performExit(userNo);
    }


    @Override
    public int insertUpdateToken(Map<String, Object> tokenData) {
        return memberDao.insertUpdateToken(tokenData);
    }

    @Override
    public int exchangeToken(Member m) {
        return memberDao.exchangeToken(m);
    }

    @Override
    public int getUpdateToken(int userNo) {
        return memberDao.getUpdateToken(userNo);
    }

    @Override
    public int paymentResult(int userNo) {
        return memberDao.paymentResult(userNo);
    }
}

