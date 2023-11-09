package com.kh.hellomentor.board.model.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kh.hellomentor.board.model.vo.*;
import com.kh.hellomentor.matching.model.vo.Mentoring;
import com.kh.hellomentor.matching.model.vo.StudyApplicant;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.hellomentor.member.controller.MemberController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class BoardDao {
    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    @Autowired
    private SqlSessionTemplate session;

    public List<Board> getPostsByUserNo(int userNo) {
        return session.selectList("boardMapper.getMyPost", userNo);
    }

    public List<Reply> getReplyByUserNo(int userNo) {
        return session.selectList("boardMapper.getMyReply", userNo);
    }


    // 이찬우 구역 시작
    // 0. 조회수 증가
    public int increaseCount(int postNo) {
    	return session.update("boardMapper.increaseCount",postNo);
    }
    //0-1. 게시글 삭제
    public int deletePost(int postNo) {
        return session.update("boardMapper.deletePost",postNo);
    }
    // 1. 공지사항 게시글 조회 (페이징바, 필터링)
    public int selectNoticeCount() {
    	return session.selectOne("boardMapper.selectNoticeCount");
    };
 
    public int searchNoticeCount(String ntkind, String keyword) {
    	 Map<String, Object> paramMap = new HashMap<>();
  	   paramMap.put("ntkind", ntkind);
  	    paramMap.put("keyword", keyword);
  	  log.info("ntkind {}", ntkind);
    	return session.selectOne("boardMapper.searchNoticeCount",paramMap);
    	
    };

    public List<Board> selectNoticeList(int page, int pageSize){
    	 int start = (page - 1) * pageSize;
         
  	   // MyBatis 매퍼를 사용하여 데이터베이스에서 페이징된 데이터를 조회합니다.
  	   Map<String, Object> params = new HashMap<>();
  	   params.put("start", start);
  	   params.put("pageSize", pageSize);
    	return session.selectList("boardMapper.selectNoticeList",params);
    };
 
    public List<Board> searchNoticeList(String ntkind, String keyword, int page, int pageSize){
    	 Map<String, Object> paramMap = new HashMap<>();
	        int start = (page - 1) * pageSize;
	       paramMap.put("ntkind", ntkind);
	       paramMap.put("keyword", keyword);
	       paramMap.put("start", start);
	      paramMap.put("pageSize", pageSize);
	      log.info("ntkind {}", ntkind);
    	return session.selectList("boardMapper.searchNoticeList",paramMap);
    	
    };

    // 1-2. 공지사항 상세 조회
    public Board selectNoticeDetail(int postNo) {
        return session.selectOne("boardMapper.selectNoticeDetail",postNo);
    }
    
    //2. FAQ 조회

    public int selectFaqCount() {
    	return session.selectOne("boardMapper.selectFaqCount");
    };
 
    public int searchFaqCount(String faqkind, String keyword) {
    	 Map<String, Object> paramMap = new HashMap<>();
  	   paramMap.put("faqkind", faqkind);
  	    paramMap.put("keyword", keyword);
    	return session.selectOne("boardMapper.searchFaqCount",paramMap);
    };

    public List<Board> selectFaqList(int page, int pageSize){
    	 int start = (page - 1) * pageSize;
         
  	   // MyBatis 매퍼를 사용하여 데이터베이스에서 페이징된 데이터를 조회합니다.
  	   Map<String, Object> params = new HashMap<>();
  	   params.put("start", start);
  	   params.put("pageSize", pageSize);
    	return session.selectList("boardMapper.selectFaqList",params);
    };
 
    public List<Board> searchFaqList(String faqkind, String keyword, int page, int pageSize){
    	 Map<String, Object> paramMap = new HashMap<>();
	        int start = (page - 1) * pageSize;
	       paramMap.put("faqkind", faqkind);
	       paramMap.put("keyword", keyword);
	       paramMap.put("start", start);
	      paramMap.put("pageSize", pageSize);
    	return session.selectList("boardMapper.searchFaqList",paramMap);
    };
    
    //3. 1:1문의 등록
    public int insertInquiry(Board board) {
    	int result = 0;
    	int postNo = 0;
    	result = session.insert("boardMapper.insertInquiry" , board);
		
		if(result > 0) {
			postNo = board.getPostNo();
			System.out.println(postNo);
		}
		
		return postNo;
    }
    public int insertInquiry2(Inquiry inquiry) {
    	return session.insert("boardMapper.insertInquiry2" , inquiry);
    }

    public int insertInquiryAttachment(List<Attachment> list) {
        return session.insert("boardMapper.insertInquiryAttachment", list);
    }
    
    //4. 문의내역 조회
    public int selectInquiryCount() {
    	return session.selectOne("boardMapper.selectInquiryCount");
    };
 

    public List<Board> selectInquiryList(int userNo, int page, int pageSize) {
    	 int start = (page - 1) * pageSize;
         
    	   // MyBatis 매퍼를 사용하여 데이터베이스에서 페이징된 데이터를 조회합니다.
    	   Map<String, Object> params = new HashMap<>();
    	   params.put("start", start);
    	   params.put("pageSize", pageSize);
    	   params.put("userNo", userNo);
        return session.selectList("boardMapper.selectInquiryList",params);
    }
    public List<Inquiry> selectInquiryList2(int userNo, int page, int pageSize) {
    	 int start = (page - 1) * pageSize;
         
    	   // MyBatis 매퍼를 사용하여 데이터베이스에서 페이징된 데이터를 조회합니다.
    	   Map<String, Object> params = new HashMap<>();
    	   params.put("start", start);
    	   params.put("pageSize", pageSize);
    	   params.put("userNo", userNo);
        return session.selectList("boardMapper.selectInquiryList2",params);
    }
    
    //4-1. 문의내역 상세 조회
    public Board selectInquiryDetail(int postNo) {
        return session.selectOne("boardMapper.selectInquiryDetail",postNo);
    }
    public Inquiry selectInquiryDetail2(int postNo) {
        return session.selectOne("boardMapper.selectInquiryDetail2",postNo);
    }
    
    //5. 자유게시판 조회
    public int selectFreeCount() {
    	return session.selectOne("boardMapper.selectFreeCount");
    };
 
    public int searchFreeCount(String freekind, String keyword,String views) {
    	 Map<String, Object> paramMap = new HashMap<>();
  	   paramMap.put("freekind", freekind);
  	    paramMap.put("keyword", keyword);
  	   paramMap.put("views", views);
    	return session.selectOne("boardMapper.searchFreeCount",paramMap);
    };

    public List<Board> selectFreeList(int page, int pageSize){
    	 int start = (page - 1) * pageSize;
         
  	   // MyBatis 매퍼를 사용하여 데이터베이스에서 페이징된 데이터를 조회합니다.
  	   Map<String, Object> params = new HashMap<>();
  	   params.put("start", start);
  	   params.put("pageSize", pageSize);
    	return session.selectList("boardMapper.selectFreeList",params);
    };
    public List<Free> selectFreeList2(int page, int pageSize){
   	 int start = (page - 1) * pageSize;
        
 	   // MyBatis 매퍼를 사용하여 데이터베이스에서 페이징된 데이터를 조회합니다.
 	   Map<String, Object> params = new HashMap<>();
 	   params.put("start", start);
 	   params.put("pageSize", pageSize);
   	return session.selectList("boardMapper.selectFreeList2",params);
   };
 
    public List<Board> searchFreeList(String freekind, String keyword, String views, int page, int pageSize){
    	 Map<String, Object> paramMap = new HashMap<>();
	        int start = (page - 1) * pageSize;
	       paramMap.put("freekind", freekind);
	       paramMap.put("keyword", keyword);
	       paramMap.put("views", views);
	       paramMap.put("start", start);
	      paramMap.put("pageSize", pageSize);
    	return session.selectList("boardMapper.searchFreeList",paramMap);
    };
    public List<Free> searchFreeList2(String freekind, String keyword,String views, int page, int pageSize){
   	 Map<String, Object> paramMap = new HashMap<>();
	        int start = (page - 1) * pageSize;
	       paramMap.put("freekind", freekind);
	       paramMap.put("keyword", keyword);
	       paramMap.put("views", views);
	       paramMap.put("start", start);
	      paramMap.put("pageSize", pageSize);
   	return session.selectList("boardMapper.searchFreeList2",paramMap);
   };
    
    //5-1. 자유게시판 조회 (화제글 3개)
    public List<Board> selectBestFreeList() {
        return session.selectList("boardMapper.selectBestFreeList");
    }
    public List<Free> selectBestFreeList2() {
        return session.selectList("boardMapper.selectBestFreeList2");
    }
    //5-2. 자유게시판 상세 조회
    public Board selectFreeDetail(int postNo) {
        return session.selectOne("boardMapper.selectFreeDetail",postNo);
    }
    public Free selectFreeDetail2(int postNo) {
        return session.selectOne("boardMapper.selectFreeDetail2",postNo);
    }
    public List<Attachment> selectAttachment(int postNo) {
        return session.selectList("boardMapper.selectAttachment",postNo);
    }

    //5-3. 자유게시판 글 작성
    public int insertFree(Board board) {
    	int result = 0;
    	int postNo = 0;
    	result = session.insert("boardMapper.insertFree" , board);
		
		if(result > 0) {
			postNo = board.getPostNo();
			// 게시글 삽입 성공시 selectKey태그를 사용하여 셋팅한 boardNo값을 b에 담아줌.
			System.out.println(postNo);
		}
		
		return postNo;
    }
    public int insertFree2(int postNo) {
    	return session.insert("boardMapper.insertFree2" , postNo);
    }
   
    public int insertFreeAttachment(List<Attachment> list) {
        return session.insert("boardMapper.insertFreeAttachment", list);
    }
    //5-4. 자유게시판 댓글 삽입
    public int insertFreeReply(Reply reply) {
		return session.insert("boardMapper.insertFreeReply" , reply);
	}
	//5-5. 자유게시판 댓글 조회
    public List<Reply> selectFreeReplyList(int postNo) {
        return session.selectList("boardMapper.selectFreeReplyList",postNo);
    }
    //5-6. 자유게시판 댓글 삭제
    public int deleteReply(int replyNo) {
        return session.update("boardMapper.deleteReply",replyNo);
    }
    // 5-7. 자유게시판 추천수 증가
    public int increaseUpvotes(int postNo) {
    	return session.update("boardMapper.increaseUpvotes",postNo);
    }
    // 5-8. 자유게시판 수정
    public int insertAttachment(Attachment attach) {
		return session.insert("boardMapper.insertAttachment", attach);
	}
    public int updateFree(Board b) {
		return session.update("boardMapper.updateFree" , b);
	}
    public int deleteAttachment(List<String> deleteList) {
		return session.delete("boardMapper.deleteAttachment" , deleteList);
	}
    //6. 지식인 조회 (메인)
    public int selectKnowledgeCount() {
    	return session.selectOne("boardMapper.selectKnowledgeCount");
    };
 
    public int searchKnowledgeCount(String knowledgekind, String keyword, String best, String accepted) {
    	 Map<String, Object> paramMap = new HashMap<>();
  	   paramMap.put("knowledgekind", knowledgekind);
  	    paramMap.put("keyword", keyword);
  	  paramMap.put("best", best);
      paramMap.put("accepted", accepted);
    	return session.selectOne("boardMapper.searchKnowledgeCount",paramMap);
    };

    public List<Board> selectKnowledgeList(int page, int pageSize){
    	 int start = (page - 1) * pageSize;
         
  	   // MyBatis 매퍼를 사용하여 데이터베이스에서 페이징된 데이터를 조회합니다.
  	   Map<String, Object> params = new HashMap<>();
  	   params.put("start", start);
  	   params.put("pageSize", pageSize);
    	return session.selectList("boardMapper.selectKnowledgeList",params);
    };
    public List<Knowledge> selectKnowledgeList2(int page, int pageSize){
   	 int start = (page - 1) * pageSize;
        
 	   // MyBatis 매퍼를 사용하여 데이터베이스에서 페이징된 데이터를 조회합니다.
 	   Map<String, Object> params = new HashMap<>();
 	   params.put("start", start);
 	   params.put("pageSize", pageSize);
   	return session.selectList("boardMapper.selectKnowledgeList2",params);
   };
  
    public List<Board> searchKnowledgeList(String knowledgekind, String keyword, String best, String accepted,int page, int pageSize){
    	 Map<String, Object> paramMap = new HashMap<>();
	        int start = (page - 1) * pageSize;
	       paramMap.put("knowledgekind", knowledgekind);
	       paramMap.put("keyword", keyword);
	       paramMap.put("best", best);
	       paramMap.put("accepted", accepted);
	       paramMap.put("start", start);
	      paramMap.put("pageSize", pageSize);
	 
    	return session.selectList("boardMapper.searchKnowledgeList",paramMap);
    };
    public List<Knowledge> searchKnowledgeList2(String knowledgekind, String keyword, String best, String accepted, int page, int pageSize){
   	 Map<String, Object> paramMap = new HashMap<>();
	        int start = (page - 1) * pageSize;
	       paramMap.put("knowledgekind", knowledgekind);
	       paramMap.put("keyword", keyword);
	       paramMap.put("best", best);
	       paramMap.put("accepted", accepted);
	       paramMap.put("start", start);
	      paramMap.put("pageSize", pageSize);
   	return session.selectList("boardMapper.searchKnowledgeList2",paramMap);
   };
   
   public List<Answer> searchKnowledgeList3(String knowledgekind, String keyword, int page, int pageSize){
  	 Map<String, Object> paramMap = new HashMap<>();
	        int start = (page - 1) * pageSize;
	       paramMap.put("knowledgekind", knowledgekind);
	       paramMap.put("keyword", keyword);
	       paramMap.put("start", start);
	      paramMap.put("pageSize", pageSize);
  	return session.selectList("boardMapper.searchKnowledgeList3",paramMap);
  };
  
    
    // 6-1. 지식인 상세 조회
    public Board selectKnowledgeDetail(int postNo) {
        return session.selectOne("boardMapper.selectKnowledgeDetail",postNo);
    }
    public Knowledge selectKnowledgeDetail2(int postNo) {
        return session.selectOne("boardMapper.selectKnowledgeDetail2",postNo);
    }
    public List<Board> selectKnowledgeDetailAnswer(int postNo) {
        return session.selectList("boardMapper.selectKnowledgeDetailAnswer",postNo);
    }
    public int selectKnowledgeAccepted(int postNo) {
        return session.selectOne("boardMapper.selectKnowledgeAccepted",postNo);
    }
  
    
    // 6-2. 지식인 답변 갯수
    public int selectKnowledgeAnswerCount(int postNo) {
		return session.selectOne("boardMapper.selectKnowledgeAnswerCount", postNo);
	}
    
    //6-3. 지식인 질문 등록
    public int insertKnowledgeQuestion(Board board) {
    	int result = 0;
    	int postNo = 0;
    	result = session.insert("boardMapper.insertKnowledgeQuestion" , board);
		
		if(result > 0) {
			postNo = board.getPostNo();
			// 게시글 삽입 성공시 selectKey태그를 사용하여 셋팅한 boardNo값을 b에 담아줌.
			System.out.println(postNo);
		}
		
		return postNo;
    }
    public int insertKnowledgeQuestion2(Knowledge knowledge) {
    	return session.insert("boardMapper.insertKnowledgeQuestion2" , knowledge);
    }

    public int insertKnowledgeAttachment(List<Attachment> list) {
        return session.insert("boardMapper.insertKnowledgeAttachment", list);
    }
    //6-4. 지식인 답변 등록
    public int insertKnowledgeAnswer(Board board) {
    	int result = 0;
    	int postNo = 0;
    	result = session.insert("boardMapper.insertKnowledgeAnswer" , board);
		
		if(result > 0) {
			postNo = board.getPostNo();
			// 게시글 삽입 성공시 selectKey태그를 사용하여 셋팅한 boardNo값을 b에 담아줌.
			System.out.println(postNo);
		}
		
		return postNo;
    }
    public int insertKnowledgeAnswer2(Answer answer) {
    	return session.insert("boardMapper.insertKnowledgeAnswer2" , answer);
    }
    // 6-6. 지식인 질문 수정
    public int updateKnowledgeQuestion(Board b) {
		return session.update("boardMapper.updateKnowledgeQuestion" , b);
	}
    public int updateKnowledgeQuestion2(Knowledge k) {
		return session.update("boardMapper.updateKnowledgeQuestion2" , k);
	}
    //6-8. 지식인 채택
    public int updateknowledgeAcceped(int postNo) {
    	return session.update("boardMapper.updateknowledgeAcceped",postNo);
    };
    // 6-9. 지식인 추천수 증가
    public int increaseKnowledgeUpvotes(int postNo) {
    	return session.update("boardMapper.increaseKnowledgeUpvotes",postNo);
    }
    // 6-10. 지식인 답변 수정
    public int knowledgeAnswerUpdate(Board b) {
		return session.update("boardMapper.knowledgeAnswerUpdate" , b);
	}
    

    //이찬우 구역 끝


    //정승훈 구역

    public List<StudyApplicant> selectPepleList(Map<String, Object> paramMap) {
        return session.selectList("boardMapper.selectPepleList", paramMap);
    }

    public List<Map<String, Object>> selectRecruitmentCount(Map<String, Object> paramMap) {
        return session.selectList("boardMapper.selectRecruitmentCount", paramMap);
    }






    public int insertStudy(Board b) {
        return session.insert("boardMapper.insertStudy",b);
    }

    public Board selectDetailStudy(int postNo) {
        return session.selectOne("boardMapper.selectDetailStudy",postNo);
    }

    public int studyDetailApplicant(int postNo) {
        return session.selectOne("boardMapper.studyDetailApplicant",postNo);
    }



    public int insertBoardAndStudy(Map<String, Object> boardData) {
        int result = 0;
        Board b = (Board) boardData.get("board");
        Study s = (Study) boardData.get("study");
        StudyApplicant sa = (StudyApplicant) boardData.get("studyApplicant");


        // board 테이블에 데이터 삽입
        result = session.insert("boardMapper.insertBoard", b);

        if (result > 0) {
            // board 테이블에 데이터를 삽입한 후 자동 생성된 postNo 값을 가져옴
            int postNo = b.getPostNo();

            // study 테이블에 데이터 삽입
            s.setPostNo(postNo);
            result = session.insert("boardMapper.insertStudy", s);

            if (result > 0) {
                // studyApplicant 테이블에 데이터 삽입
                sa.setPostNo(postNo);
                result = session.insert("boardMapper.insertStudyApplicant1", sa);
            }
        }

        return result;
    }


    //-------------------------2023-09-09 정승훈 작업---------------------------
    public int insertReply(Reply r) {
        return session.insert("boardMapper.insertReply",r);
    }
    public List<Reply> selectReplyList(int postNo) {
        return session.selectList("boardMapper.selectReplyList",postNo);
    }

    //모집인원
    public List<Study> selectStudyList(Study study) {
        return session.selectList("boardMapper.selectStudyList1",study);
    }

    //스터디 작성자 설정한 인원수
    public int selectStudypeople(int postNo) {
        return session.selectOne("boardMapper.selectStudypeople",postNo);
    }


    //댓글삭제
    public int deleteStudyReply(Reply r) {
        return session.delete("boardMapper.deleteStudyReply",r);
    }


    public int insertStudyApplicant(StudyApplicant sa) {
        return session.insert("boardMapper.insertStudyApplicant",sa);
    }


    public int studyDelete(int postNo) {
        return session.delete("boardMapper.studyDelete",postNo);
    }


    public int selectListCount() {
        return session.selectOne("boardMapper.selectSListCount");}

    public int selectStudyListCount(String searchOption, String keyword) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("searchOption", searchOption);
        paramMap.put("keyword", keyword);
        return session.selectOne("boardMapper.selectStudyListCount", paramMap);
    }

    public List<Board> selectStudyList(String searchOption, String keyword, int page, int pageSize, Map<String, Object> paramMap) {
        int start = (page - 1) * pageSize;
        paramMap.put("searchOption", searchOption);
        paramMap.put("keyword", keyword);
        paramMap.put("start", start);
        paramMap.put("pageSize", pageSize);

        return session.selectList("boardMapper.searchStudyList", paramMap);
    }

    public List<Board> getSideStudyList(int page, int pageSize) {
        //페이징 정보를 이용하여 시작 위치를 계산합니다.
        int start = (page - 1) * pageSize;

        // MyBatis 매퍼를 사용하여 데이터베이스에서 페이징된 데이터를 조회합니다.
        Map<String, Object> params = new HashMap<>();
        params.put("start", start);
        params.put("pageSize", pageSize);

        return session.selectList("boardMapper.getSideStudyList", params);
    }


    public StudyApplicant duStudy(Map<String, Integer> params) {
        return session.selectOne("boardMapper.duStudy", params);
    }















    public Board selectBoard(int postNo) {
        return session.selectOne("boardMapper.selectBoard",postNo);
    }


    public int insertReport(Map<String, Object> reportInfo) {
        Board board = null;
        int result1 = session.insert("boardMapper.insertReport1", reportInfo);
        int result2 = 0;
        int result3 = 0;

        if(result1 > 0) {
            result2 = session.insert("boardMapper.insertReport2", reportInfo);

            if(reportInfo.get("changeName") != null) {
                result3 = session.insert("boardMapper.insertReportAttach", reportInfo);
                return result1 * result2 * result3;
            }

            return result1 * result2;
        }
        return result1 * result2;
    }

    public List<Map<String, Object>> topFiveBoard(String boardType) {
        return session.selectList("boardMapper.topFiveBoard", boardType);
    }

    public List<Map<String, Object>> newMentoring() {
        return session.selectList("boardMapper.newMentoring");
    }
}
