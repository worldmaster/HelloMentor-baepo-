package com.kh.hellomentor.admin.model.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.hellomentor.board.model.vo.Attachment;
import com.kh.hellomentor.board.model.vo.Board;
import com.kh.hellomentor.board.model.vo.Report;
import com.kh.hellomentor.matching.model.vo.Mentoring;
import com.kh.hellomentor.member.model.vo.Member;
import com.kh.hellomentor.member.model.vo.Profile;

@Repository
public class AdminDao {


	   @Autowired
	   private SqlSessionTemplate sqlSession;


	public int selectCountList() {
		return sqlSession.selectOne("adminMapper.selectMList");
	}

	public int selectSCountList() {
		return sqlSession.selectOne("adminMapper.selectSList");
	}

	public int selectRCountList() {
		return sqlSession.selectOne("adminMapper.selectRList");
	}

	public int selectICountList() {
		return sqlSession.selectOne("adminMapper.selectIList");
	}

	public List<Member> getSideMemberList(int page, int pageSize) {
		//페이징 정보를 이용하여 시작 위치를 계산합니다.
		int start = (page - 1) * pageSize;
		              
		// MyBatis 매퍼를 사용하여 데이터베이스에서 페이징된 데이터를 조회합니다.
		Map<String, Object> params = new HashMap<>();
		params.put("start", start);
		params.put("pageSize", pageSize);
		
		return sqlSession.selectList("adminMapper.getSideMemberList", params);
	}

	public List<Member> SearchMemberList(String searchOption, String keyword, int page, int pageSize) {
	  	  Map<String, Object> paramMap = new HashMap<>();
	  	   int start = (page - 1) * pageSize;
		    paramMap.put("searchOption", searchOption);
		    paramMap.put("keyword", keyword);
		    paramMap.put("start", start);
			paramMap.put("pageSize", pageSize);
		    
		    return sqlSession.selectList("adminMapper.searchMemberList", paramMap);
	}

	public int selectMemberListCount(String searchOption, String keyword) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("searchOption", searchOption);
	    paramMap.put("keyword", keyword);
	    return sqlSession.selectOne("adminMapper.selectMemberListCount", paramMap);
	}

	public Member detailViewMember(int userNo) {
		return sqlSession.selectOne("adminMapper.detailViewMember", userNo);
	}

	public Mentoring detailViewMemberIntro(int userNo) {
		return sqlSession.selectOne("adminMapper.detailViewMemberIntro", userNo);
	}

	public List<Board> detailViewMemberBoardListF(int userNo) {
		return sqlSession.selectList("adminMapper.detailViewMemberBoardListF", userNo);
	}

	public List<Board> detailViewMemberBoardListK(int userNo) {
		return sqlSession.selectList("adminMapper.detailViewMemberBoardListK", userNo);
	}

	public List<Board> detailViewMemberBoardListS(int userNo) {
		return sqlSession.selectList("adminMapper.detailViewMemberBoardListS", userNo);
	}

	public List<Board> reportList(int page, int pageSize, String order) {
			int start = (page - 1) * pageSize;
			              
			// MyBatis 매퍼를 사용하여 데이터베이스에서 페이징된 데이터를 조회합니다.
			Map<String, Object> params = new HashMap<>();
			params.put("start", start);
			params.put("order", order);
			params.put("pageSize", pageSize);
			
			return sqlSession.selectList("adminMapper.reportList", params);
	}
	public List<Member> reportList2(int page, int pageSize, String order) {
		int start = (page - 1) * pageSize;
		              
		// MyBatis 매퍼를 사용하여 데이터베이스에서 페이징된 데이터를 조회합니다.
		Map<String, Object> params = new HashMap<>();
		params.put("start", start);
		params.put("order", order);
		params.put("pageSize", pageSize);
		
		return sqlSession.selectList("adminMapper.reportList2", params);
	}

	public List<Report> reportList3(int page, int pageSize, String order) {
		int start = (page - 1) * pageSize;
	    
		// MyBatis 매퍼를 사용하여 데이터베이스에서 페이징된 데이터를 조회합니다.
		Map<String, Object> params = new HashMap<>();
		params.put("start", start);
		params.put("order", order);
		params.put("pageSize", pageSize);
		
		return sqlSession.selectList("adminMapper.reportList3", params);
	}

	public List<Board> completereportList(int page, int pageSize, String statusFilter , String order) {
	int start = (page - 1) * pageSize;
	    
		// MyBatis 매퍼를 사용하여 데이터베이스에서 페이징된 데이터를 조회합니다.
		Map<String, Object> params = new HashMap<>();
		params.put("start", start);
		params.put("order", order);
		params.put("pageSize", pageSize);
		params.put("status", statusFilter);
		
		return sqlSession.selectList("adminMapper.completereportList", params);
	}

	public List<Member> completereportList2(int page, int pageSize, String statusFilter, String order) {
	int start = (page - 1) * pageSize;
	    
		// MyBatis 매퍼를 사용하여 데이터베이스에서 페이징된 데이터를 조회합니다.
		Map<String, Object> params = new HashMap<>();
		params.put("start", start);
		params.put("order", order);
		params.put("pageSize", pageSize);
		params.put("status", statusFilter);
		
		return sqlSession.selectList("adminMapper.completereportList2", params);
	}

	public List<Report> completereportList3(int page, int pageSize, String statusFilter, String order) {
	    int start = (page - 1) * pageSize;
	    
		// MyBatis 매퍼를 사용하여 데이터베이스에서 페이징된 데이터를 조회합니다.
		Map<String, Object> params = new HashMap<>();
		params.put("start", start);
		params.put("order", order);
		params.put("pageSize", pageSize);
		params.put("status", statusFilter);
		
		return sqlSession.selectList("adminMapper.completereportList3", params);
	}

	public int completeRListCount(String statusFilter) {
		return sqlSession.selectOne("adminMapper.completeRListCount", statusFilter);
	}

	public Board detailViewReport1(int postNo) {
		return sqlSession.selectOne("adminMapper.detailViewReport1", postNo);
	}
	public Member detailViewReport2(int postNo) {
		return sqlSession.selectOne("adminMapper.detailViewReport2", postNo);
	}
	public Board detailViewReport3(int postNo) {
		return sqlSession.selectOne("adminMapper.detailViewReport3", postNo);
	}
	public Member detailViewReport4(int postNo) {
		return sqlSession.selectOne("adminMapper.detailViewReport4", postNo);
	}
	public Attachment reportAttachment(int postNo) {
		return sqlSession.selectOne("adminMapper.reportAttachment", postNo);
	}

	public int reportSend(int postNo) {
		return sqlSession.update("adminMapper.reportSend", postNo);
	}

	public int reportAlramSend(int postNo, int userNo) {
		
		Map<String, Object> params = new HashMap<>();
		params.put("postNo", postNo);
		params.put("userNo", userNo);
		
		return sqlSession.insert("adminMapper.reportAlramSend", params) ;
	}

	public int sendNotice(int categoryId) {
		return sqlSession.insert("adminMapper.sendNotice", categoryId);
	}

	public int sendNoticeBoard(String pTitle, String pContent) {
		Map<String, Object> params = new HashMap<>();
		params.put("pTitle", pTitle);
		params.put("pContent", pContent);
		
		return sqlSession.insert("adminMapper.sendNoticeBoard", params);
	}

	public int sendFaqBoard(String pTitle, String pContent) {
		Map<String, Object> params = new HashMap<>();
		params.put("pTitle", pTitle);
		params.put("pContent", pContent);
		
		return sqlSession.insert("adminMapper.sendFaqBoard", params);
	}

	public int QUpdate(int postNo, String pContent) {
		Map<String, Object> params = new HashMap<>();
		params.put("postNo", postNo);
		params.put("pContent", pContent);
		return sqlSession.update("adminMapper.QUpdate", params);
	}

	public Profile mProfileList(int userNo) {
		return sqlSession.selectOne("adminMapper.getprofile", userNo);
	}


}