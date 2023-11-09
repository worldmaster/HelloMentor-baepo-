package com.kh.hellomentor.admin.model.service;

import java.util.List;

import com.kh.hellomentor.board.model.vo.Attachment;
import com.kh.hellomentor.board.model.vo.Board;
import com.kh.hellomentor.board.model.vo.Report;
import com.kh.hellomentor.matching.model.vo.Mentoring;
import com.kh.hellomentor.member.model.vo.Member;
import com.kh.hellomentor.member.model.vo.Profile;

public interface AdminService {

	int selectListCount();

	int selectSListCount();

	int selectRListCount();

	int selectIListCount();

	int selectMemberListCount(String searchOption, String keyword);
	
	List<Member> getSideMemberList(int page, int pageSize);

	List<Member> searchMembers(String searchOption, String keyword, int page, int pageSize);

	Member detailViewMember(int userNo);

	Mentoring detailViewMemberIntro(int userNo);

	List<Board> detailViewMemberBoardListF(int userNo);
	
	List<Board> detailViewMemberBoardListK(int userNo);
	
	List<Board> detailViewMemberBoardListS(int userNo);

	List<Report> getSideReportList(int page, int pageSize);

	List<Board> reportList(int page, int pageSize, String order);
    
	List<Member> reportList2(int page, int pageSize, String order);

	List<Report> reportList3(int page, int pageSize, String order);

	int completeRListCount(String statusFilter);

	List<Board> completereportList(int page, int pageSize, String statusFilter, String order);

	List<Member> completereportList2(int page, int pageSize, String statusFilter, String order);

	List<Report> completereportList3(int page, int pageSize, String statusFilter, String order);

	Board detailViewReport1(int postNo);

	Member detailViewReport2(int postNo);

	Board detailViewReport3(int postNo);

	Member detailViewReport4(int postNo);

	Attachment reportAttachment(int postNo);

	int reportSend(int postNo);

	int reportAlramSend(int postNo, int userNo);

	int sendNotice(int categoryId);

	int sendNoticeBoard(String pTitle, String pContent);
    
	int sendFaqBoard(String pTitle, String pContent);

	int QUpdate(int postNo, String pContent);

	Profile mProfileList(int userNo);

}

