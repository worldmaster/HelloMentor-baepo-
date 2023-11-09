package com.kh.hellomentor.admin.controller;

import com.kh.hellomentor.admin.model.service.AdminService;
import com.kh.hellomentor.board.model.service.BoardService;
import com.kh.hellomentor.board.model.vo.Attachment;
import com.kh.hellomentor.board.model.vo.Board;
import com.kh.hellomentor.board.model.vo.Inquiry;
import com.kh.hellomentor.board.model.vo.Report;
import com.kh.hellomentor.matching.model.vo.Mentoring;
import com.kh.hellomentor.member.model.vo.Member;
import com.kh.hellomentor.member.model.vo.Profile;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
public class AdminController {

	 @Autowired
     private AdminService aService;
      
      @Autowired
     private BoardService boardService; 
      
      
      
     
  	@GetMapping("/admin/selectList") // admin 메인페이지에서 총 게시글 카운트를 확인하는 컨트롤러
  	public String selectList( Model model) {
  	 	//public String selectMList( @RequestParam Map<String, Object> paramMap, Model model) {
  		int total = aService.selectListCount();
  		model.addAttribute("aTotal", total);
  		
  		int stotal = aService.selectSListCount();
  		model.addAttribute("sTotal", stotal);
  		
  		int rtotal = aService.selectRListCount();
  		model.addAttribute("rTotal", rtotal);
  		
  		int itotal = aService.selectIListCount();
  		model.addAttribute("iTotal", itotal);
  		
  		return "admin/admin-main";
  	}
      
      
  	@RequestMapping("/sideMember") //admin 회원관리 페이지 연결
  	public String sideMember() {
  		String	url = "redirect:/admin/sideMemberList";
  		return url;
  	}
  	
      @GetMapping("/admin/sideMemberList") //admin 회원관리 페이지 
      public String sideMemberList(@RequestParam(name = "page", defaultValue = "1") int page,
   		   @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
   		   @RequestParam(name = "searchOption", required = false) String searchOption,
   		   @RequestParam(name = "keyword", required = false) String keyword,
   		   Model model) {
   	   
          // 페이지 번호와 페이지당 항목 수로 페이징 정보를 생성합니다
   	   long totalItems = 0;
   	   
   	   List<Member> pageItems;
   	   if (searchOption != null && keyword != null) {
   		   // 검색 로직을 수행하고 결과를 처리
   		   totalItems = aService.selectMemberListCount(searchOption, keyword); // 현재 검색된 게시글의 총 갯수 
   		   pageItems = aService.searchMembers(searchOption, keyword, page, pageSize); // 현재 검색된 게시글
   	   } else {
   		   // 일반 목록을 가져옵니다
   		   totalItems = aService.selectListCount(); // 전체 일반목록의 총 갯수
   		   pageItems = aService.getSideMemberList(page, pageSize); // 전체 일반목록의 게시글
   	   }
   	   long tatalPages = 0; // 
   	   
   	   if(totalItems == 0) {
   		   totalItems = 1;
   		   tatalPages = (totalItems + pageSize - 1) / pageSize;
   	   }                        
   	   tatalPages = (totalItems + pageSize - 1) / pageSize;
   	   
   	   //페이징 처리시 totalPages가 html로 넘어가서 총 갯수에 맞게 밑에 번호버튼이 생성됨                         
          // 모델에 데이터와 페이징 정보를 추가합니다.
   	   
   	   model.addAttribute("sideMember", pageItems);
   	   model.addAttribute("currentPage", page);
   	   model.addAttribute("keyword", keyword);
   	   model.addAttribute("searchOption", searchOption);
   	   model.addAttribute("pageSize", pageSize);
   	   model.addAttribute("totalItems", totalItems);
   	   model.addAttribute("totalPages", tatalPages); // 총 페이지 수 추가
   	   
   	   return "admin/admin-member-list"; // 해당 뷰로 이동
      }
      
			/*
			 * @RequestMapping("/memberDetail") public String memberDetail() { String url =
			 * ""; url = "redirect:/admin/memberDetailList"; return url; }
			 */      
      
      @GetMapping("/memberDetail") //admin 회원관리 리스트에서 게시글을 클릭시 나오는 디테일뷰
      public String memberDetail(
   		   @RequestParam(name = "userNo") int userNo,
   		   Model model) {
          // userNo를 이용하여 회원 상세 정보를 가져오는 로직을 추가하세요
           Member member = aService.detailViewMember(userNo); // 예시: aService에서 userNo에 해당하는 회원 정보 가져오기
          
           Mentoring mentoring = aService.detailViewMemberIntro(userNo);
           
           List<Board> boardF = aService.detailViewMemberBoardListF(userNo);
           List<Board> boardK = aService.detailViewMemberBoardListK(userNo);
           List<Board> boardS = aService.detailViewMemberBoardListS(userNo);
           Profile profileList = aService.mProfileList(userNo);
           
          // 회원 상세 정보를 모델에 추가하고 회원 상세 정보 페이지로 이동
          model.addAttribute("member", member);
          model.addAttribute("mentoring",mentoring);
		   model.addAttribute("boardF", boardF);
		   model.addAttribute("boardK", boardK);
		   model.addAttribute("boardS", boardS);
		   model.addAttribute("profile", profileList);
           
		   System.out.println(profileList);
		    
          //예를 들어 model.addAttribute("프로필사진", 프로필사진); -> userNo값은 넘어와있으니까 프로필사진만 이런식으로 꺼내서쓰기
          return "admin/admin-member-detail"; // 이동할 뷰 이름
      }
      
    @GetMapping("admin/memberReport") //admin 신고관리 리스트 관련
    public String memberReport(@RequestParam(name = "page", defaultValue = "1") int page,
   		 @RequestParam(name = "statusFilter", defaultValue = "N") String statusFilter,
   		 @RequestParam(name = "order", defaultValue = "ASC") String order,
 		   @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
 		   Model model) {
   	 // 페이지 번호와 페이지당 항목 수로 페이징 정보를 생성합니다
 	   long totalItems = 0;
 	   List<Board> pageItems = null;
 	   List<Member> pageItems2 = null;
 	   List<Report> pageItems3 = null;
		   
 		   // 일반 목록을 가져옵니다
 		   
 		   if(statusFilter.equals("N")) {
 			   totalItems = aService.selectRListCount(); // 전체 신고목록의 총 갯수
 			   pageItems = aService.reportList(page, pageSize, order); // 전체 신고목록의 게시글
 			   model.addAttribute("reportList", pageItems);
 			   pageItems2 = aService.reportList2(page, pageSize, order); // 전체 신고목록의 신고한 사람의 아이디
 			   model.addAttribute("reportList2", pageItems2);
 			   pageItems3 = aService.reportList3(page, pageSize, order); // 전체 신고목록의 처리상태
 			   model.addAttribute("reportList3", pageItems3);
 		   
 		   }else if(statusFilter.equals("Y")) {
 			 totalItems = aService.completeRListCount(statusFilter); // STATUS값이 Y인 신고목록의 총 갯수
			   pageItems = aService.completereportList(page, pageSize,statusFilter, order); //  STATUS값이 Y인 신고목록의 게시글
			   model.addAttribute("reportList", pageItems);
			   pageItems2 = aService.completereportList2(page, pageSize,statusFilter, order); // STATUS값이 Y인 신고목록의 신고한 사람의 아이디
			   model.addAttribute("reportList2", pageItems2);
			   pageItems3 = aService.completereportList3(page, pageSize,statusFilter, order); // STATUS값이 Y인 신고목록의 처리상태
			   model.addAttribute("reportList3", pageItems3);
 		   }
 		   
 		 
 		List<Object[]> combinedList = new ArrayList<>();
 	     for (int i = 0; i < pageItems.size(); i++) {
 	         combinedList.add(new Object[] { pageItems.get(i), pageItems2.get(i), pageItems3.get(i) });
 	     }
 	      model.addAttribute("combinedList", combinedList);
 		   
 	   long tatalPages = 0; // 
 	   
 	   if(totalItems == 0) {
 		   totalItems = 1;
 		   tatalPages = (totalItems + pageSize - 1) / pageSize;
 	   }                        
 	   tatalPages = (totalItems + pageSize - 1) / pageSize;
 	   
 	   //페이징 처리시 totalPages가 html로 넘어가서 총 갯수에 맞게 밑에 번호버튼이 생성됨                         
        // 모델에 데이터와 페이징 정보를 추가합니다.
 	   model.addAttribute("statusFilter", statusFilter);
 	   model.addAttribute("order", order);
      model.addAttribute("currentPage", page);
 	   model.addAttribute("pageSize", pageSize);
 	   model.addAttribute("totalItems", totalItems);
 	   model.addAttribute("totalPages", tatalPages); // 총 페이지 수 추가
   	 
   	 return "admin/admin-report-list";
    } 
  	
    @GetMapping("/reportDetail") //admin 신고관리 리스트에서 게시글을 클릭시 나오는 디테일뷰
    public String reportDetail(
 		   @RequestParam(name = "postNo") int postNo,
 		   Model model) {
   	  Board  bReport1 = aService.detailViewReport1(postNo); //신고당한 사람이 올린 게시글 이름, 신고를 당한 사람의 보드의 타입
         Member mReport1 = aService.detailViewReport2(postNo); //신고를 당한 사람의 ID(Member)
         Board  bReport2 = aService.detailViewReport3(postNo); //신고한 날짜, 신고 제목, 내용을 얻을수있음.
         Member mReport2 = aService.detailViewReport4(postNo); //신고자의 아이디
         Attachment attachment = aService.reportAttachment(postNo); //신고자가 올린 첨부파일
         
        // 회원 상세 정보를 모델에 추가하고 회원 상세 정보 페이지로 이동
           model.addAttribute("bReport1", bReport1);
           model.addAttribute("mReport1",mReport1);
		   model.addAttribute("bReport2", bReport2);
		   model.addAttribute("mReport2", mReport2);
		   model.addAttribute("attachment", attachment);
			
        return "admin/admin-report-detail"; // 이동할 뷰 이름
    }
    
   @PostMapping("/admin/reportSend")//admin 신고접수 클릭시 반응하는 컨트롤러
   public ResponseEntity<String> reportSend(@RequestParam("postNo") int postNo, 
   	                                 	@RequestParam("userNo") int userNo, 
   		                       Model model) {
   	   
   	   int result = aService.reportSend(postNo);
   	   int result1 = aService.reportAlramSend(postNo, userNo);
   	 
		  if((result > 0) && (result1 > 0)) {
			 return ResponseEntity.ok("요청 성공");
		}else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("요청 실패");
		}
    }
   
   @RequestMapping("/admin/noticeEnroll") //admin 공지사항 작성페이지 연결
   public String noticeEnroll() {
   	
   	return "admin/admin-notice-enroll";
   }
   
   @PostMapping("/admin/noticeEnrollWrite")//l // 공지사항
   public String sendNotice(@RequestParam("pTitle") String pTitle,
   		                 @RequestParam("noticeContent") String pContent,
   		                 @RequestParam("categoryId") int categoryId,
   		                 RedirectAttributes redirectAttributes,
   		                 Model model) {
       // board 테이블에 데이터 삽입
   	
      String url = "";
      
	   int result = aService.sendNoticeBoard(pTitle, pContent);
	   int result1 = aService.sendNotice(categoryId);
	 
		  if((result > 0) && (result1 > 0)) {
			  redirectAttributes.addFlashAttribute("message", "공지사항 작성이 완료됐습니다.");
			  return "redirect:/noticelist";
		}else {
			  redirectAttributes.addFlashAttribute("message", "공지사항을 다시 작성해주세요");
			  return "redirect:/noticelist";
		}
   }
   
   @RequestMapping("/admin/faqWritePage") //admin 공지사항 작성페이지 연결
   public String faqWritePage() {
   	
   	return "admin/admin-faq-write";
   }
   
   @PostMapping("/admin/faqWrite")//faq작성
   public String faqWrite(@RequestParam("pTitle") String pTitle,
   		                 @RequestParam("faqContent") String pContent,
   		                 @RequestParam("categoryId") int categoryId,
   		                 RedirectAttributes redirectAttributes,
   		                 Model model) {
       // board 테이블에 데이터 삽입
   	
      String url = "";
      
	   int result = aService.sendFaqBoard(pTitle, pContent);
	   int result1 = aService.sendNotice(categoryId);
	 
		  if((result > 0) && (result1 > 0)) {
			  redirectAttributes.addFlashAttribute("message", "공지사항 작성이 완료됐습니다.");
			  return "redirect:/faqlist";
		}else {
			  redirectAttributes.addFlashAttribute("message", "공지사항을 다시 작성해주세요");
			  return "redirect:/faqlist";
		}
   }
   
 // 문의 내역 상세 조회
   @GetMapping("/admin/Questiondetail")
   public String Questiondetail(
           Model model,
           @RequestParam(name = "ino") int postNo
   ) {
      log.info("postNo {}", postNo);
      
       Board selectedPost = boardService.selectInquiryDetail(postNo);
       model.addAttribute("selectedPost", selectedPost);
       
       Inquiry selectedPost2 = boardService.selectInquiryDetail2(postNo);
       model.addAttribute("selectedPost2", selectedPost2);

       return "admin/admin-question-answer";
   }
   
   @PostMapping("/admin/QuestionWrite")//문의답변작성
   public String QuestionWrite(@RequestParam("postNo") int postNo,
   		                 @RequestParam("postContent") String pContent,
   		                 RedirectAttributes redirectAttributes,
   		                 Model model) {
       // board 테이블에 데이터 삽입
   	
	   int result = aService.QUpdate(postNo, pContent);
	 
		  if(result > 0) {
			  redirectAttributes.addFlashAttribute("message", "문의답변 작성이 완료됐습니다.");
			  
		}else {
			  redirectAttributes.addFlashAttribute("message", "문의답변을 다시 작성해주세요");
			
		}
		  return "redirect:/inquirylist";
   }
   
}
   
   
