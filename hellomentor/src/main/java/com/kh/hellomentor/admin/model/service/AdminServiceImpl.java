package com.kh.hellomentor.admin.model.service;

import com.kh.hellomentor.admin.model.dao.AdminDao;
import com.kh.hellomentor.board.model.vo.Attachment;
import com.kh.hellomentor.board.model.vo.Board;
import com.kh.hellomentor.board.model.vo.Report;
import com.kh.hellomentor.matching.model.vo.Mentoring;
import com.kh.hellomentor.member.model.vo.Member;
import com.kh.hellomentor.member.model.vo.Profile;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AdminServiceImpl implements AdminService {

	 @Autowired
	   private AdminDao adminDao;
	
	
	@Override
	public int selectListCount() {
		return adminDao.selectCountList();
	}
	@Override
	public int selectSListCount() {
		return adminDao.selectSCountList();
	}
	@Override
	public int selectRListCount() {
		return adminDao.selectRCountList();
	}
	@Override
	public int selectIListCount() {
		 return adminDao.selectICountList();
	}
	@Override
	public List<Member> getSideMemberList(int page, int pageSize) {
		return adminDao.getSideMemberList(page, pageSize);
	}
	@Override
	public List<Member> searchMembers(String searchOption, String keyword, int page, int pageSize) {
		return adminDao.SearchMemberList(searchOption, keyword, page, pageSize);
	}
	@Override
	public int selectMemberListCount(String searchOption, String keyword) {
		return adminDao.selectMemberListCount(searchOption, keyword);
	}
	@Override
	public Member detailViewMember(int userNo) {
		return adminDao.detailViewMember(userNo);
	}
	@Override
	public Mentoring detailViewMemberIntro(int userNo) {
		return adminDao.detailViewMemberIntro(userNo);
	}
	@Override
	public List<Board> detailViewMemberBoardListF(int userNo) {
		return adminDao.detailViewMemberBoardListF(userNo);
	}
	@Override
	public List<Board> detailViewMemberBoardListK(int userNo) {
		return adminDao.detailViewMemberBoardListK(userNo);
	}
	@Override
	public List<Board> detailViewMemberBoardListS(int userNo) {
		return adminDao.detailViewMemberBoardListS(userNo);
	}
	
	@Override
	public List<Board> reportList(int page, int pageSize, String order) {
		return adminDao.reportList(page, pageSize, order);
	}
	@Override
	public List<Member> reportList2(int page, int pageSize, String order) {
		return adminDao.reportList2(page, pageSize, order);
	}
	@Override
	public List<Report> reportList3(int page, int pageSize, String order) {
		return adminDao.reportList3(page, pageSize, order);
	}
	@Override
	public List<Board> completereportList(int page, int pageSize, String statusFilter, String order) {
		return adminDao.completereportList(page, pageSize, statusFilter, order);
	}
	@Override
	public List<Member> completereportList2(int page, int pageSize, String statusFilter, String order) {
		return adminDao.completereportList2(page, pageSize, statusFilter, order);
	}
	@Override
	public List<Report> completereportList3(int page, int pageSize, String statusFilter, String order) {
		return adminDao.completereportList3(page, pageSize, statusFilter, order);
	}
	
	@Override
	public int completeRListCount(String statusFilter) {
		return adminDao.completeRListCount(statusFilter);
	}
	@Override
	public List<Report> getSideReportList(int page, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Board detailViewReport1(int postNo) {
		return adminDao.detailViewReport1(postNo);
	}
	@Override
	public Member detailViewReport2(int postNo) {
		return adminDao.detailViewReport2(postNo);
	}
	@Override
	public Board detailViewReport3(int postNo) {
		return adminDao.detailViewReport3(postNo);
	}
	@Override
	public Member detailViewReport4(int postNo) {
		return adminDao.detailViewReport4(postNo);
	}
	@Override
	public Attachment reportAttachment(int postNo) {
		return adminDao.reportAttachment(postNo);
	}
	@Override
	public int reportSend(int postNo) {
		return adminDao.reportSend(postNo);
	}
	
	@Override
	public int reportAlramSend(int postNo, int userNo) {
		return adminDao.reportAlramSend(postNo, userNo);
	}
	@Override
	public int sendNotice(int categoryId) {
		return adminDao.sendNotice(categoryId);
	}
	@Override
	public int sendNoticeBoard(String pTitle, String pContent) {
		return adminDao.sendNoticeBoard(pTitle,  pContent);
	}
	@Override
	public int sendFaqBoard(String pTitle, String pContent) {
		return adminDao.sendFaqBoard(pTitle,  pContent);
	}
	@Override
	public int QUpdate(int postNo, String pContent) {
		return adminDao.QUpdate(postNo,  pContent);
	}
	@Override
	public Profile mProfileList(int userNo) {
		return adminDao.mProfileList(userNo);
	}

}