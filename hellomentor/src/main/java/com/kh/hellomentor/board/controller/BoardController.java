package com.kh.hellomentor.board.controller;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.kh.hellomentor.board.model.vo.*;
import com.kh.hellomentor.matching.model.vo.StudyApplicant;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.kh.hellomentor.board.model.service.BoardService;
import com.kh.hellomentor.common.Utils;
import com.kh.hellomentor.member.controller.MemberController;
import com.kh.hellomentor.member.model.vo.Member;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@SessionAttributes({"loginUser"})
public class BoardController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    @Autowired
    private BoardService boardService;

    @Autowired
    private ServletContext application;


    //    마이페이지 내가 쓴 글
    @RequestMapping("/profile_my_post")
    public String profileMyPost(Model model, HttpSession session) {

        Member loginUser = (Member) session.getAttribute("loginUser");

        int userNo = loginUser.getUserNo();

        List<Board> myPosts = boardService.getPostsByUserNo(userNo);


        // 치환된 값을 사용하도록 수정
        List<Board> postsWithReplacedBoardType = myPosts.stream()
                .map(board -> {
                    String originalBoardType = board.getBoardType();
                    String replacedBoardType = replaceBoardType(originalBoardType);
                    board.setBoardType(replacedBoardType);
                    return board;
                })
                .collect(Collectors.toList());

        Set<String> uniqueBoardTypes = postsWithReplacedBoardType.stream()
                .map(Board::getBoardType)
                .collect(Collectors.toSet());

        model.addAttribute("myPosts", postsWithReplacedBoardType);
        model.addAttribute("boardTypes", uniqueBoardTypes);
        return "mypage/profile_my_post";
    }

    // 마이페이지 내가 쓴 댓글
    @RequestMapping("/profile_my_reply")
    public String profileMyReply(Model model, HttpSession session) {
        Member loginUser = (Member) session.getAttribute("loginUser");

        int userNo = loginUser.getUserNo();

        List<Reply> myReplies = boardService.getReplyByUserNo(userNo);

        List<Reply> repliesWithReplacedBoardType = myReplies.stream()
                .map(reply -> {
                    String originalBoardType = reply.getBoardType();
                    String replacedBoardType = replaceBoardType(originalBoardType);
                    reply.setBoardType(replacedBoardType);
                    return reply;
                })
                .collect(Collectors.toList());

        Set<String> uniqueBoardTypes = repliesWithReplacedBoardType.stream()
                .map(Reply::getBoardType)
                .collect(Collectors.toSet());

        model.addAttribute("myreply", repliesWithReplacedBoardType);
        model.addAttribute("boardTypes", uniqueBoardTypes);

        return "mypage/profile_my_reply";
    }


    //    게시판 타입 이름으로 바꿔주는 메소드
    private String replaceBoardType(String originalBoardType) {
        switch (originalBoardType) {
            case "A":
                return "문의게시판";
            case "N":
                return "공지사항게시판";
            case "Q":
                return "FAQ게시판";
            case "F":
                return "자유게시판";
            case "K":
                return "지식인게시판";
            case "KA":
                return "지식인답글게시판";
            case "S":
                return "스터디게시판";
            case "R":
                return "신고게시판";
            default:
                return originalBoardType;
        }
    }


    //이찬우 구역 시작
    //1. 공지사항 게시글 조회
    @GetMapping("/noticelist")
    public String selectNoticeList(
            Model model,
            HttpSession session,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "ntkind", required = false) String ntkind,
            @RequestParam(name = "keyword", required = false) String keyword
    ) {
        Member loginUser = (Member) session.getAttribute("loginUser");
        model.addAttribute("loginUser", loginUser);
        log.info("loginUser : {}", loginUser);


    	  // 페이지 번호와 페이지당 항목 수로 페이징 정보를 생성합니다
        long totalItems = 0;

        List<Board> pageItems;
        if (ntkind != null  && keyword != null) {
           // 검색 로직을 수행하고 결과를 처리
           totalItems = boardService.searchNoticeCount(ntkind, keyword); // 현재 검색된 게시글의 총 갯수
           pageItems = boardService.searchNoticeList(ntkind, keyword, page, pageSize); // 현재 검색된 게시글
        } else {
           // 일반 목록을 가져옵니다
           totalItems = boardService.selectNoticeCount(); // 전체 일반목록의 총 갯수
           pageItems = boardService.selectNoticeList(page, pageSize); // 전체 일반목록의 게시글
        }
        long totalPages = 0; //

        if(totalItems == 0) {
           totalItems = 1;
           totalPages = (totalItems + pageSize - 1) / pageSize;
        }
        totalPages = (totalItems + pageSize - 1) / pageSize;

        //페이징 처리시 totalPages가 html로 넘어가서 총 갯수에 맞게 밑에 번호버튼이 생성됨
         // 모델에 데이터와 페이징 정보를 추가합니다.

        model.addAttribute("list", pageItems);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("ntkind", ntkind);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("totalPages", totalPages); // 총 페이지 수 추가
        log.info("pageItems {}", pageItems);
        log.info("page {}", page);
        log.info("keyword {}", keyword);
        log.info("ntkind {}", ntkind);
        log.info("pageSize {}", pageSize);
        log.info("totalItems {}", totalItems);
        log.info("totalPages {}", totalPages);

        return "board/notice/notice-board";
    }

    //1-1. 공지사항 상세 조회
    @GetMapping("/noticedetail")
    public String selectNoticeDetail(
            Model model,
            HttpSession session,
            @RequestParam(name = "nno") int postNo,
            HttpServletRequest req, 
            HttpServletResponse res,
            RedirectAttributes redirectAttributes
    ) {
       log.info("postNo {}", postNo);
       
        Member loginUser = (Member) session.getAttribute("loginUser");
        model.addAttribute("loginUser", loginUser);

        Board selectedPost = boardService.selectNoticeDetail(postNo);
        model.addAttribute("selectedPost", selectedPost);
        log.info("selectedPost {}", selectedPost);
        
     // 상세조회 성공시 쿠키를 이용해서 조회수가 중복으로 증가되지 않도록 방지 + 본인의 글은 애초에 조회수 증가되지 않게 설정
     		if (selectedPost != null) {

     			// int userNo = 0;
     			String userId = "";

     			if (loginUser != null) {
     				userId = loginUser.getUserId();
     			}

     			// 게시글의 작성자 아이디와, 현재 세션의 접속중인 아이디가 같지 않은 경우에만 조회수증가
     			if (!selectedPost.getUserNo().equals(userId)) {

     				// 쿠키
     				Cookie cookie = null;

     				Cookie[] cArr = req.getCookies(); // 사용자의 쿠키정보 얻어오기.
     				if (cArr != null && cArr.length > 0) {

     					for (Cookie c : cArr) {
     						if (c.getName().equals("readPostNo")) {
     							cookie = c;
     							break;
     						}
     					}
     				}

     				int result = 0;

     				if (cookie == null) { // 원래 readBoardNo라는 이름의 쿠키가 없는 케이스
     					// 쿠키 생성
     					cookie = new Cookie("readPostNo", postNo + "");// 게시글작성자와 현재 세션에 저장된 작성자 정보가 일치하지않고, 쿠기도 없다.
     					// 조회수 증가 서비스 호출
     					result = boardService.increaseCount(postNo);
     				} else { // 존재 했던 케이스
     					// 쿠키에 저장된 값중에 현재 조회된 게시글번호(boardNo)를 추가
     					// 단, 기존 쿠키값에 중복되는 번호가 없는 경우에만 추가 => 조회수증가와함께

     					String[] arr = cookie.getValue().split("/");
     					// "reacBoardNo" : "1/2/5/10/135" ==> ["1","2","5","10","135"]

     					// 배열을 컬렉션으로 변환 => indexOf를 사용하기 위  해서
     					// List.indexOf(obj) : list안에서 매개변수로 들어온 obj와 일치(equals)하는 부분의 인덱스를 반환
     					// 일치하는 값이 없는경우 -1 반환
     					List<String> list2 = Arrays.asList(arr);
     					log.info("list2 {}", list2);

     					if (list2.indexOf(postNo + "") == -1) { // 기존 쿠키값에 현재 게시글 번호와 일치하는 값이 없는경우(처음들어온글)
     						// 단, 기존 쿠키값에 중복되는 번호가 없는 경우에만 추가 => 조회수증가와함께
     						cookie.setValue(cookie.getValue() + "/" + postNo);
     						result = boardService.increaseCount(postNo);
     					}
     				}
     				log.info("result {}", result);
     				if (result > 0) { // 성공적으로 조회수 증가함 (브라우저 단위)
     					selectedPost.setViews(selectedPost.getViews() + 1);

     					cookie.setPath(req.getContextPath());
     					cookie.setMaxAge(60 * 60 * 1); // 1시간만 유지
     					res.addCookie(cookie);
     				}
     			}
     		} else {
     			redirectAttributes.addFlashAttribute("message", "게시글 조회 실패");
     		}

        
        
        return "board/notice/notice-detail";

    }
    //1-2. 공지사항 삭제
    @GetMapping("/deletenotice")
    public String deleteNotice(
            Model model,
            HttpSession session,
            @RequestParam(name = "nno") int postNo,
            RedirectAttributes redirectAttributes
    ) {
       log.info("postNo {}", postNo);
       
       int result = boardService.deletePost(postNo);
       log.info("result {}", result);
        if (result > 0) {
             redirectAttributes.addFlashAttribute("message", postNo + "번 공지사항이 성공적으로 삭제되었습니다");
             return "redirect:/noticelist";
         } else {
             redirectAttributes.addFlashAttribute("message", "게시글 삭제에 실패했습니다.");
             return "redirect:/noticelist";
         }
    }
    //2. FAQ 글 조회
    @GetMapping("/faqlist")
    public String selectFaqList(
            Model model,
            HttpSession session,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "faqkind", required = false) String faqkind,
            @RequestParam(name = "keyword", required = false) String keyword
    ) {
        Member loginUser = (Member) session.getAttribute("loginUser");
        model.addAttribute("loginUser", loginUser);
        

        	  // 페이지 번호와 페이지당 항목 수로 페이징 정보를 생성합니다
            long totalItems = 0;

            List<Board> pageItems;
            if (faqkind != null  && keyword != null) {
               // 검색 로직을 수행하고 결과를 처리
               totalItems = boardService.searchFaqCount(faqkind, keyword); // 현재 검색된 게시글의 총 갯수
               pageItems = boardService.searchFaqList(faqkind, keyword, page, pageSize); // 현재 검색된 게시글
            } else {
               // 일반 목록을 가져옵니다
               totalItems = boardService.selectFaqCount(); // 전체 일반목록의 총 갯수
               pageItems = boardService.selectFaqList(page, pageSize); // 전체 일반목록의 게시글
            }
            long totalPages = 0; //

            if(totalItems == 0) {
               totalItems = 1;
               totalPages = (totalItems + pageSize - 1) / pageSize;
            }
            totalPages = (totalItems + pageSize - 1) / pageSize;

            //페이징 처리시 totalPages가 html로 넘어가서 총 갯수에 맞게 밑에 번호버튼이 생성됨
             // 모델에 데이터와 페이징 정보를 추가합니다.

            model.addAttribute("list", pageItems);
            model.addAttribute("currentPage", page);
            model.addAttribute("keyword", keyword);
            model.addAttribute("faqkind", faqkind);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalItems", totalItems);
            model.addAttribute("totalPages", totalPages); // 총 페이지 수 추가
            log.info("pageItems {}", pageItems);
            log.info("page {}", page);
            log.info("keyword {}", keyword);
            log.info("faqkind {}", faqkind);
            log.info("pageSize {}", pageSize);
            log.info("totalItems {}", totalItems);
            log.info("totalPages {}", totalPages);

            return "board/faq/faq-board";
        }

    //2-1. FAQ 삭제
    @GetMapping("/deletefaq")
    public String deleteFaq(
            Model model,
            HttpSession session,
            @RequestParam(name = "fno") int postNo,
            RedirectAttributes redirectAttributes
    ) {
       log.info("postNo {}", postNo);
       
       int result = boardService.deletePost(postNo);
       log.info("result {}", result);
       
        if (result > 0) {
             redirectAttributes.addFlashAttribute("message",  postNo + "번 공지사항이 성공적으로 삭제되었습니다");
             return "redirect:/faqlist";
         } else {
             redirectAttributes.addFlashAttribute("message", "게시글 삭제에 실패했습니다.");
             return "redirect:/faqlist";
         }
    }

    //3. 문의 내역 insert
    @GetMapping("/inquiryinsert")
    public String moveInquiryInsert(Model model) {
        return "board/inquiry/inquiry-insert";
    }

    @PostMapping("/inquiryinsert")
    public String inquiryInsert(
            Board board,
            Inquiry inquiry,
            @RequestParam(value = "upfile", required = false) List<MultipartFile> upfiles,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes

    ) {
        Member loginUser = (Member) session.getAttribute("loginUser");
        int userNo = loginUser.getUserNo();
        
        // 이미지, 파일을 저장할 저장경로 얻어오기
        String webPath = "/img/attachment/inquiry/";
        String currentDirectory = System.getProperty("user.dir");
        String FilesLocation = currentDirectory + "/src/main/resources/static"+webPath;


        board.setUserNo(userNo + "");

        // 디렉토리생성 , 해당디렉토리가 존재하지 않는다면 생성
        File dir = new File(webPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        List<Attachment> attachList = new ArrayList();

        int level = -1;
        for (MultipartFile upfile : upfiles) {
            // input[name=upFile]로 만들어두면 비어있는 file이 넘어올수 있음.
            level++;
            if (upfile.isEmpty())
                continue;

            //  파일명 재정의 해주는 함수.
            String changeName = Utils.saveFile(upfile, FilesLocation);
            Attachment at = Attachment.
                    builder().
                    changeName(changeName).
                    originName(upfile.getOriginalFilename()).
                    build();
            attachList.add(at);
        }

        int postNo = 0;
        int result = 0;

        try {
            postNo = boardService.insertInquiry(board, attachList, webPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("postNo {}", postNo);
        inquiry.setPostNo(postNo);
        log.info("inquiry {}", inquiry);
        result = boardService.insertInquiry2(inquiry);
        log.info("result {}", result);
        if (result > 0) {
           redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 작성되었습니다");
            return "redirect:/inquirylist";
        } else {
           redirectAttributes.addFlashAttribute("message",  "게시글 작성에 실패하였습니다. 다시 작성해주세요");
            return "redirect:/inquiryinsert";
        }
    }

    //4. 문의 내역 조회
    @GetMapping("/inquirylist")
    public String selectInquiryList(
            Model model,
            HttpSession session,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "pageSize", defaultValue = "15") int pageSize
    ) {
        Member loginUser = (Member) session.getAttribute("loginUser");
        model.addAttribute("loginUser", loginUser);
        int userNo = loginUser.getUserNo();

        // 페이지 번호와 페이지당 항목 수로 페이징 정보를 생성합니다
        long totalItems = 0;
        List<Board> list;
        List<Inquiry> list2;

        // 일반 목록을 가져옵니다
        totalItems = boardService.selectInquiryCount(); // 전체 일반목록의 총 갯수
        list = boardService.selectInquiryList(userNo, page, pageSize); // 전체 일반목록의 게시글
        list2 = boardService.selectInquiryList2(userNo, page, pageSize);
         long totalPages = 0;

         if(totalItems == 0) {
             totalItems = 1;
             totalPages = (totalItems + pageSize - 1) / pageSize;
          }
          totalPages = (totalItems + pageSize - 1) / pageSize;

          List<Object[]> combinedList = new ArrayList<>();
          for (int i = 0; i < list.size(); i++) {
              combinedList.add(new Object[]{list.get(i), list2.get(i)});
          }

          model.addAttribute("combinedList", combinedList);
          model.addAttribute("currentPage", page);
          model.addAttribute("pageSize", pageSize);
          model.addAttribute("totalItems", totalItems);
          model.addAttribute("totalPages", totalPages); // 총 페이지 수 추가
          log.info("combinedList {}", combinedList);
          log.info("page {}", page);
          log.info("pageSize {}", pageSize);
          log.info("totalItems {}", totalItems);
          log.info("totalPages {}", totalPages);


        return "board/inquiry/inquiry-board";
    }

    //4-1. 문의 내역 상세 조회
    @GetMapping("/inquirydetail")
    public String selectInquiryDetail(
            Model model,
            HttpSession session,
            @RequestParam(name = "ino") int postNo
    ) {
       log.info("postNo {}", postNo);
       
       Member loginUser = (Member) session.getAttribute("loginUser");
       model.addAttribute("loginUser", loginUser);
       
        Board selectedPost = boardService.selectInquiryDetail(postNo);
        model.addAttribute("selectedPost", selectedPost);
        log.info("selectedPost {}", selectedPost);
        
        Inquiry selectedPost2 = boardService.selectInquiryDetail2(postNo);
        model.addAttribute("selectedPost2", selectedPost2);
        log.info("selectedPost2 {}", selectedPost2);

        List<Attachment> attachList = boardService.selectAttachment(postNo);
        model.addAttribute("attachList", attachList);
        log.info("attachList {}", attachList);

        return "board/inquiry/inquiry-detail";
    }


    //5. 자유게시판 글 조회 (화제 게시글 포함)
    @GetMapping("/freelist")
    public String selectFreeList(
            Model model,
            HttpSession session,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "freekind", required = false) String freekind,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "views", required = false) String views
    ) {
    	Member loginUser = (Member) session.getAttribute("loginUser");
        model.addAttribute("loginUser", loginUser);

        // 페이지 번호와 페이지당 항목 수로 페이징 정보를 생성합니다
        long totalItems = 0;

        List<Board> pageItems;
        List<Free> pageItems2;
        if ((freekind != null  && keyword != null) || views!=null) {
           // 검색 로직을 수행하고 결과를 처리
           totalItems = boardService.searchFreeCount(freekind, keyword,views); // 현재 검색된 게시글의 총 갯수
           pageItems = boardService.searchFreeList(freekind, keyword, views,page, pageSize); // 현재 검색된 게시글
           pageItems2 = boardService.searchFreeList2(freekind, keyword, views,page, pageSize);
        } else {
           // 일반 목록을 가져옵니다
           totalItems = boardService.selectFreeCount(); // 전체 일반목록의 총 갯수
           pageItems = boardService.selectFreeList(page, pageSize); // 전체 일반목록의 게시글
           pageItems2 = boardService.selectFreeList2(page, pageSize);
        }
        long totalPages = 0; //

        if(totalItems == 0) {
           totalItems = 1;
           totalPages = (totalItems + pageSize - 1) / pageSize;
        }
        totalPages = (totalItems + pageSize - 1) / pageSize;

        //페이징 처리시 totalPages가 html로 넘어가서 총 갯수에 맞게 밑에 번호버튼이 생성됨
         // 모델에 데이터와 페이징 정보를 추가합니다.

        //일반 게시글


        //핫 게시글
        List<Board> list3 = boardService.selectBestFreeList();
        model.addAttribute("list3", list3);
        log.info("list3 {}", list3);
        
        List<Free> list4 = boardService.selectBestFreeList2();
        model.addAttribute("list4", list4);
        log.info("list4 {}", list4);

        // 일반 게시글 묶어주기
        List<Object[]> combinedList = new ArrayList<>();
        for (int i = 0; i < pageItems.size(); i++) {
            combinedList.add(new Object[]{pageItems.get(i), pageItems2.get(i)});
        }

        //핫게시글 묶어주기
        List<Object[]> combinedList2 = new ArrayList<>();
        for (int i = 0; i < list3.size(); i++) {
            combinedList2.add(new Object[]{list3.get(i), list4.get(i)});
        }

        model.addAttribute("combinedList", combinedList);
        model.addAttribute("combinedList2", combinedList2);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("freekind", freekind);
        model.addAttribute("views", views);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("totalPages", totalPages); // 총 페이지 수 추가
        log.info("combinedList {}", combinedList);
        log.info("combinedList2 {}", combinedList2);
        log.info("page {}", page);
        log.info("views {}", views);
        log.info("keyword {}", keyword);
        log.info("freekind {}", freekind);
        log.info("pageSize {}", pageSize);
        log.info("totalItems {}", totalItems);
        log.info("totalPages {}", totalPages);

        return "board/free/free-board";
    }

    //5-1. 자유게시판 상세 조회
    @GetMapping("/freedetail")
    public String selectFreeDetail(
            Model model,
            HttpSession session,
            @RequestParam(name = "fno") int postNo,
            HttpServletRequest req, 
            HttpServletResponse res,
            RedirectAttributes redirectAttributes
    ) {
       log.info("postNo {}", postNo);
       
        Member loginUser = (Member) session.getAttribute("loginUser");
        model.addAttribute("loginUser", loginUser);

        Board selectedPost = boardService.selectFreeDetail(postNo);
        model.addAttribute("selectedPost", selectedPost);
        log.info("selectedPost {}", selectedPost);

        Free selectedPost2 = boardService.selectFreeDetail2(postNo);
        model.addAttribute("selectedPost2", selectedPost2);
        log.info("selectedPost2 {}", selectedPost2);

        List<Reply> list = boardService.selectFreeReplyList(postNo);
        model.addAttribute("list", list);
        log.info("list {}", list);
        
        List<Attachment> attachList = boardService.selectAttachment(postNo);
        model.addAttribute("attachList", attachList);
        log.info("attachList {}", attachList);

    	// 상세조회 성공시 쿠키를 이용해서 조회수가 중복으로 증가되지 않도록 방지 + 본인의 글은 애초에 조회수 증가되지 않게 설정
		if (selectedPost != null) {

			// int userNo = 0;
			String userId = "";

			if (loginUser != null) {
				userId = loginUser.getUserId();
			}

			// 게시글의 작성자 아이디와, 현재 세션의 접속중인 아이디가 같지 않은 경우에만 조회수증가
			if (!selectedPost.getUserNo().equals(userId)) {

				// 쿠키
				Cookie cookie = null;

				Cookie[] cArr = req.getCookies(); // 사용자의 쿠키정보 얻어오기.
				if (cArr != null && cArr.length > 0) {

					for (Cookie c : cArr) {
						if (c.getName().equals("readPostNo")) {
							cookie = c;
							break;
						}
					}
				}

				int result = 0;

				if (cookie == null) { // 원래 readBoardNo라는 이름의 쿠키가 없는 케이스
					// 쿠키 생성
					cookie = new Cookie("readPostNo", postNo + "");// 게시글작성자와 현재 세션에 저장된 작성자 정보가 일치하지않고, 쿠기도 없다.
					// 조회수 증가 서비스 호출
					result = boardService.increaseCount(postNo);
				} else { // 존재 했던 케이스
					// 쿠키에 저장된 값중에 현재 조회된 게시글번호(boardNo)를 추가
					// 단, 기존 쿠키값에 중복되는 번호가 없는 경우에만 추가 => 조회수증가와함께

					String[] arr = cookie.getValue().split("/");
					// "reacBoardNo" : "1/2/5/10/135" ==> ["1","2","5","10","135"]

					// 배열을 컬렉션으로 변환 => indexOf를 사용하기 위  해서
					// List.indexOf(obj) : list안에서 매개변수로 들어온 obj와 일치(equals)하는 부분의 인덱스를 반환
					// 일치하는 값이 없는경우 -1 반환
					List<String> list3 = Arrays.asList(arr);
					log.info("list3 {}", list3);

					if (list3.indexOf(postNo + "") == -1) { // 기존 쿠키값에 현재 게시글 번호와 일치하는 값이 없는경우(처음들어온글)
						// 단, 기존 쿠키값에 중복되는 번호가 없는 경우에만 추가 => 조회수증가와함께
						cookie.setValue(cookie.getValue() + "/" + postNo);
						result = boardService.increaseCount(postNo);
					}
				}
				log.info("result {}", result);
				if (result > 0) { // 성공적으로 조회수 증가함 (브라우저 단위)
					selectedPost.setViews(selectedPost.getViews() + 1);

					cookie.setPath(req.getContextPath());
					cookie.setMaxAge(60 * 60 * 1); // 1시간만 유지
					res.addCookie(cookie);
				}
			}
		} else {
			redirectAttributes.addFlashAttribute("message", "게시글 조회 실패");
		}

        return "board/free/free-detail";

    }

    //5-2. 자유게시판 글 등록
    @GetMapping("/freeinsert")
    public String moveFreeInsert() {
        return "board/free/free-insert";
    }
    @PostMapping("/freeinsert")
    public String freeInsert(
            Board board,
            @RequestParam(value = "upfile", required = false) List<MultipartFile> upfiles,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes

    ) {
    	log.info("upfiles {}", upfiles);
    	
        Member loginUser = (Member) session.getAttribute("loginUser");
        int userNo = loginUser.getUserNo();
        // 이미지, 파일을 저장할 저장경로 얻어오기
        String webPath = "/img/attachment/free/";
        String currentDirectory = System.getProperty("user.dir");
        String FilesLocation = currentDirectory + "/src/main/resources/static"+webPath;

        board.setUserNo(userNo + "");

        // 디렉토리생성 , 해당디렉토리가 존재하지 않는다면 생성
        File dir = new File(FilesLocation);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        List<Attachment> attachList = new ArrayList();

        int level = -1;
        for (MultipartFile upfile : upfiles) {
            // input[name=upFile]로 만들어두면 비어있는 file이 넘어올수 있음.
            level++;
            if (upfile.isEmpty())
                continue;

            //  파일명 재정의 해주는 함수.
            String changeName = Utils.saveFile(upfile, FilesLocation);
            Attachment at = Attachment.
                    builder().
                    changeName(changeName).
                    originName(upfile.getOriginalFilename()).
                    build();
            attachList.add(at);
        }

        int postNo = 0;
        int result = 0;

        try {
            postNo = boardService.insertFree(board, attachList, webPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("postNo {}", postNo);
        result = boardService.insertFree2(postNo);
        log.info("result {}", result);
        
        if (result > 0) {
           redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 작성되었습니다");
            return "redirect:/freelist";
        } else {
           redirectAttributes.addFlashAttribute("message", "게시글이 작성에 실패하였습니다. 다시 작성해주세요.");
            return "redirect:/freeinsert";
        }
    }
   
    //5-3. 자유게시판 댓글 등록
    @GetMapping("/insertFreeReply")
	@ResponseBody // 리턴되는 값이 뷰페이지가 아니라 값 자체임을 의미
	public int insertFreeReply(Reply reply, HttpSession session) {

    	Member loginUser = (Member) session.getAttribute("loginUser");
		if (loginUser != null) {
			reply.setUserNo(loginUser.getUserNo() + "");
		}

		int result = boardService.insertFreeReply(reply);
		log.info("result {}", result);

		return result;
	}
    //5-4. 자유게시판 댓글 조회
	@GetMapping("/selectFreeReplyList")
	@ResponseBody
	public List<Reply> selectFreeReplyList(int postNo) {
		return boardService.selectFreeReplyList(postNo);
	}
	//5-5. 자유게시판 댓글 삭제
    @GetMapping("/deletereply")
    public String deleteReply(
            Model model,
            HttpSession session,
            @RequestParam(name = "rno") int replyId,
            @RequestParam(name = "fno") int postNo,
            RedirectAttributes redirectAttributes
    ) {
       log.info("replyId {}", replyId);
       
       int result = boardService.deleteReply(replyId);
       log.info("result {}", result);
        if (result > 0) {
             redirectAttributes.addFlashAttribute("message", "댓글이 성공적으로 삭제되었습니다");
             return "redirect:/freedetail?fno="+postNo;
         } else {
             redirectAttributes.addFlashAttribute("message", "댓글 삭제에 실패했습니다.");
             return "redirect:/freedetail?fno="+postNo;
         }
    }
  //5-6. 자유게시판 글 삭제
    @GetMapping("/deletefree")
    public String deleteFree(
            Model model,
            HttpSession session,
            @RequestParam(name = "fno") int postNo,
            RedirectAttributes redirectAttributes
    ) {
       log.info("postNo {}", postNo);
       
       int result = boardService.deletePost(postNo);
       log.info("result {}", result);
        if (result > 0) {
             redirectAttributes.addFlashAttribute("message", postNo + "번 글이 성공적으로 삭제되었습니다");
             return "redirect:/freelist";
         } else {
             redirectAttributes.addFlashAttribute("message", "글 삭제에 실패했습니다.");
             return "redirect:/freelist";
         }
    }

   
    //5-7. 자유게시판 추천수
    @GetMapping("/increaseupvotes")
    public String increaseUpvotes(Model model, HttpSession session,
            @RequestParam(name = "fno") int postNo,
            @RequestParam(name = "uno") String userNo,
            HttpServletRequest req,
            HttpServletResponse res,
            RedirectAttributes redirectAttributes) {
        log.info("postNo {}", postNo);

        Member loginUser = (Member) session.getAttribute("loginUser");
        model.addAttribute("loginUser", loginUser);

        Free selectedPost2 = boardService.selectFreeDetail2(postNo);
        log.info("selectedPost2 {}", selectedPost2);

        if (selectedPost2 != null) {

            String userId = "";

            if (loginUser != null) {
                userId = loginUser.getUserId();
            }

            if (!userNo.equals(userId)) {

                Cookie cookie = null;

                Cookie[] cArr = req.getCookies();
                if (cArr != null && cArr.length > 0) {

                    for (Cookie c : cArr) {
                        if (c.getName().equals("increaseUpVotes")) {
                            cookie = c;
                            break;
                        }
                    }
                }

                int result = 0;

                if (cookie == null) {
                    cookie = new Cookie("increaseUpVotes", postNo + "");
                    result = boardService.increaseUpvotes(postNo);
                    redirectAttributes.addFlashAttribute("message", "추천되었습니다.");
                } else {

                    String[] arr = cookie.getValue().split("/");

                    List<String> list2 = Arrays.asList(arr);
                    log.info("list2 {}", list2);

                    if (list2.indexOf(postNo + "") == -1) {
                        cookie.setValue(cookie.getValue() + "/" + postNo);
                        result = boardService.increaseUpvotes(postNo);
                        redirectAttributes.addFlashAttribute("message", "추천되었습니다.");
                    }else
                    	 redirectAttributes.addFlashAttribute("message", "이미 추천하셨습니다.");
                }
                log.info("result {}", result);
                if (result > 0) {
                    selectedPost2.setUpVotes(selectedPost2.getUpVotes() + 1);
                    cookie.setPath(req.getContextPath());
                    cookie.setMaxAge(60 * 60 * 1);
                    res.addCookie(cookie);
                }
            }else {
            	redirectAttributes.addFlashAttribute("message", "자신의 글에는 추천할 수 없습니다.");
            }

        } else {
            redirectAttributes.addFlashAttribute("message", "추천수 증가 실패");
        }

        return "redirect:/freedetail?fno="+postNo;
    }

    //5-8. 자유게시판 글 수정
    @GetMapping("/freeupdate")
    public String moveFreeUpdate(
    		 Model model,
             HttpSession session,
             @RequestParam(name = "fno") int postNo
             ) {
    	 model.addAttribute("postNo", postNo);

        Board selectedPost = boardService.selectFreeDetail(postNo);
        selectedPost.setPostContent(Utils.newLineClear(selectedPost.getPostContent()));
        model.addAttribute("selectedPost", selectedPost);
        log.info("selectedPost {}", selectedPost);

        List<Attachment> attachList = boardService.selectAttachment(postNo);
        model.addAttribute("attachList", attachList);
        log.info("attachList {}", attachList);

        return "board/free/free-update";
    }
    @PostMapping("/freeupdate")
	public String updateBoard(
			Board board, 
			Attachment attachment,
			@RequestParam(value = "upfile", required = false) List<MultipartFile> upfiles,
			HttpSession session,
			Model model,
			RedirectAttributes redirectAttributes,
			@RequestParam(value = "deletedAttachmentIds", required = false) List<String> deleteList
			
			) {
    	
    	log.info("deleteList {}", deleteList);
    	log.info("upfiles {}", upfiles);
		// 이미지, 파일을 저장할 저장경로 얻어오기
		// /resources/images/board/{boardCode}/
    	
    	String webPath = "/img/attachment/free/";
        String currentDirectory = System.getProperty("user.dir");
        String FilesLocation = currentDirectory + "/src/main/resources/static"+webPath;
        
  

		// Board 객체에 데이터 추가(boardCode , boardWriter , boardNo)
		Member loginUser = (Member) session.getAttribute("loginUser");
		board.setUserNo(loginUser.getUserId() + "");

		log.info("board ================== {}", board);
		int result = 0;

		try {
			result = boardService.updateFree(board, deleteList, upfiles, webPath, FilesLocation);
		} catch (Exception e) {
			log.error("error = {}", e.getMessage());
		}

		if (result > 0) {
			redirectAttributes.addFlashAttribute("message", "게시글 수정 성공");
			return "redirect:/freedetail?fno="+board.getPostNo();
		} else {
			redirectAttributes.addFlashAttribute("message", "게시글 수정 실패");
			 return "redirect:/freeupdate?fno="+board.getPostNo();
		}
	}
    //6. 지식인 글 조회
    @GetMapping("/knowledgelist")
    public String selectKnowledgeList(
            Model model,
            HttpSession session,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "knowledgekind", required = false) String knowledgekind,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "best", required = false) String best,
            @RequestParam(name = "accepted", required = false) String accepted
    ) {
    	Member loginUser = (Member) session.getAttribute("loginUser");
        model.addAttribute("loginUser", loginUser);

        	  // 페이지 번호와 페이지당 항목 수로 페이징 정보를 생성합니다
            long totalItems = 0;

            List<Board> pageItems;
            List<Knowledge> pageItems2;
            List<Answer> pageItems3;
            if ((knowledgekind != null  && keyword != null) || best!=null || accepted!=null) {
               // 검색 로직을 수행하고 결과를 처리

               totalItems = boardService.searchKnowledgeCount(knowledgekind, keyword, best, accepted); // 현재 검색된 게시글의 총 갯수
               pageItems = boardService.searchKnowledgeList(knowledgekind, keyword, best, accepted, page, pageSize); // 현재 검색된 게시글
               pageItems2 = boardService.searchKnowledgeList2(knowledgekind, keyword, best, accepted, page, pageSize); // 현재 검색된 게시글
            } else {
               // 일반 목록을 가져옵니다
               totalItems = boardService.selectKnowledgeCount(); // 전체 일반목록의 총 갯수
               pageItems = boardService.selectKnowledgeList(page, pageSize); // 전체 일반목록의 게시글
               pageItems2 = boardService.selectKnowledgeList2(page, pageSize);
            }
            long totalPages = 0; //

            if(totalItems == 0) {
               totalItems = 1;
               totalPages = (totalItems + pageSize - 1) / pageSize;
            }
            totalPages = (totalItems + pageSize - 1) / pageSize;

            //페이징 처리시 totalPages가 html로 넘어가서 총 갯수에 맞게 밑에 번호버튼이 생성됨
             // 모델에 데이터와 페이징 정보를 추가합니다.
        List<Object[]> combinedList = new ArrayList<>();
        for (int i = 0; i < pageItems.size(); i++) {
            combinedList.add(new Object[]{pageItems.get(i), pageItems2.get(i)});
        }


        model.addAttribute("combinedList", combinedList);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("knowledgekind", knowledgekind);
        model.addAttribute("best", best);
        model.addAttribute("accepted", accepted);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("totalPages", totalPages); // 총 페이지 수 추가
        log.info("combinedList {}", combinedList);
        log.info("page {}", page);
        log.info("keyword {}", keyword);
        log.info("best {}", best);
        log.info("accepted {}", accepted);
        log.info("knowledgekind {}", knowledgekind);
        log.info("pageSize {}", pageSize);
        log.info("totalItems {}", totalItems);
        log.info("totalPages {}", totalPages);

        return "board/knowledge/knowledge-board";
    }

    //6-2. 지식인 상세 조회
    @GetMapping("/knowledgedetail")
    public String selectKnowledgeDetail(
            Model model,
            HttpSession session,
            @RequestParam(name = "kno") int postNo,
            HttpServletRequest req, 
            HttpServletResponse res,
            RedirectAttributes redirectAttributes
    ) {
       log.info("postNo {}", postNo);
       
        Member loginUser = (Member) session.getAttribute("loginUser");
        model.addAttribute("loginUser", loginUser);
        
        int total = boardService.selectKnowledgeAnswerCount(postNo);
        model.addAttribute("total", total);
        log.info("total {}", total);

        int isAccepted = boardService.selectKnowledgeAccepted(postNo);
        model.addAttribute("isAccepted", isAccepted);
        log.info("isAccepted {}", isAccepted);

        Board selectedPost = boardService.selectKnowledgeDetail(postNo);
        model.addAttribute("selectedPost", selectedPost);
        log.info("selectedPost {}", selectedPost);

        Knowledge selectedPost2 = boardService.selectKnowledgeDetail2(postNo);
        model.addAttribute("selectedPost2", selectedPost2);
        log.info("selectedPost2 {}", selectedPost2);

        List<Board> list = boardService.selectKnowledgeDetailAnswer(postNo);
        model.addAttribute("list", list);
        log.info("list {}", list);
        
        List<Attachment> attachList = boardService.selectAttachment(postNo);
        model.addAttribute("attachList", attachList);
        log.info("attachList {}", attachList);


        	// 상세조회 성공시 쿠키를 이용해서 조회수가 중복으로 증가되지 않도록 방지 + 본인의 글은 애초에 조회수 증가되지 않게 설정
     		if (selectedPost != null) {

     			// int userNo = 0;
     			String userId = "";

     			if (loginUser != null) {
     				userId = loginUser.getUserId();
     			}

     			// 게시글의 작성자 아이디와, 현재 세션의 접속중인 아이디가 같지 않은 경우에만 조회수증가
     			if (!selectedPost.getUserNo().equals(userId)) {

     				// 쿠키
     				Cookie cookie = null;

     				Cookie[] cArr = req.getCookies(); // 사용자의 쿠키정보 얻어오기.
     				if (cArr != null && cArr.length > 0) {

     					for (Cookie c : cArr) {
     						if (c.getName().equals("readPostNo")) {
     							cookie = c;
     							break;
     						}
     					}
     				}

     				int result = 0;

     				if (cookie == null) { // 원래 readBoardNo라는 이름의 쿠키가 없는 케이스
     					// 쿠키 생성
     					cookie = new Cookie("readPostNo", postNo + "");// 게시글작성자와 현재 세션에 저장된 작성자 정보가 일치하지않고, 쿠기도 없다.
     					// 조회수 증가 서비스 호출
     					result = boardService.increaseCount(postNo);
     				} else { // 존재 했던 케이스
     					// 쿠키에 저장된 값중에 현재 조회된 게시글번호(boardNo)를 추가
     					// 단, 기존 쿠키값에 중복되는 번호가 없는 경우에만 추가 => 조회수증가와함께

     					String[] arr = cookie.getValue().split("/");
     					// "reacBoardNo" : "1/2/5/10/135" ==> ["1","2","5","10","135"]

     					// 배열을 컬렉션으로 변환 => indexOf를 사용하기 위  해서
     					// List.indexOf(obj) : list안에서 매개변수로 들어온 obj와 일치(equals)하는 부분의 인덱스를 반환
     					// 일치하는 값이 없는경우 -1 반환
     					List<String> list2 = Arrays.asList(arr);
     					log.info("list2 {}", list2);

     					if (list2.indexOf(postNo + "") == -1) { // 기존 쿠키값에 현재 게시글 번호와 일치하는 값이 없는경우(처음들어온글)
     						// 단, 기존 쿠키값에 중복되는 번호가 없는 경우에만 추가 => 조회수증가와함께
     						cookie.setValue(cookie.getValue() + "/" + postNo);
     						result = boardService.increaseCount(postNo);
     					}
     				}
     				log.info("result {}", result);
     				if (result > 0) { // 성공적으로 조회수 증가함 (브라우저 단위)
     					selectedPost.setViews(selectedPost.getViews() + 1);

     					cookie.setPath(req.getContextPath());
     					cookie.setMaxAge(60 * 60 * 1); // 1시간만 유지
     					res.addCookie(cookie);
     				}
     			}
     		} else {
     			redirectAttributes.addFlashAttribute("message", "게시글 조회 실패");
     		}
        return "board/knowledge/knowledge-detail";

    }

    //6-3.지식인 글 등록
    @GetMapping("/knowledgequestioninsert")
    public String moveKnowledgeQuestionInsert() {
        return "board/knowledge/knowledge-question";
    }

    @PostMapping("/knowledgequestioninsert")
    public String knowledgeQuestionInsert(
            Board board,
            Knowledge knowledge,
            @RequestParam(value = "upfile", required = false) List<MultipartFile> upfiles,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes

    ) {
        Member loginUser = (Member) session.getAttribute("loginUser");
        int userNo = loginUser.getUserNo();
        // 이미지, 파일을 저장할 저장경로 얻어오기
        String webPath = "/img/attachment/knowledge/";
        String currentDirectory = System.getProperty("user.dir");
        String FilesLocation = currentDirectory + "/src/main/resources/static"+webPath;

        board.setUserNo(userNo + "");

        // 디렉토리생성 , 해당디렉토리가 존재하지 않는다면 생성
        File dir = new File(FilesLocation);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        List<Attachment> attachList = new ArrayList();

        int level = -1;
        for (MultipartFile upfile : upfiles) {
            // input[name=upFile]로 만들어두면 비어있는 file이 넘어올수 있음.
            level++;
            if (upfile.isEmpty())
                continue;

            //  파일명 재정의 해주는 함수.
            String changeName = Utils.saveFile(upfile, FilesLocation);
            Attachment at = Attachment.
                    builder().
                    changeName(changeName).
                    originName(upfile.getOriginalFilename()).
                    build();
            attachList.add(at);
        }

        int postNo = 0;
        int result = 0;

        try {
            postNo = boardService.insertKnowledgeQuestion(board, attachList, webPath);
            log.info("postNo {}", postNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        knowledge.setPostNo(postNo);
        result = boardService.insertKnowledgeQuestion2(knowledge);
        log.info("result {}", result);
        
        if (result > 0) {
           redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 작성되었습니다");
            return "redirect:/knowledgelist";
        } else {
           redirectAttributes.addFlashAttribute("message", "게시글이 작성에 실패하였습니다. 다시 작성해주세요.");
            return "redirect:/knowledgequestioninsert";
        }
    }

    //6-4.지식인 답변 등록
    @GetMapping("/knowledgeanswerinsert")
    public String moveKnowledgeAnswerInsert(
    		 @RequestParam(name = "ano") int knowledgePostNo,
    		 Model model
    		) {
    	model.addAttribute("knowledgePostNo", knowledgePostNo);
        return "board/knowledge/knowledge-answer";
    }

    @PostMapping("/knowledgeanswerinsert")
    public String knowledgeAnswerInsert(
            Board board,
            Answer answer,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes

    ) {

        Member loginUser = (Member) session.getAttribute("loginUser");
        int userNo = loginUser.getUserNo();

        board.setUserNo(userNo + "");


        int postNo = 0;
        int result = 0;

        try {
            postNo = boardService.insertKnowledgeAnswer(board);
            log.info("postNo {}", postNo);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        answer.setPostNo(postNo);
        result = boardService.insertKnowledgeAnswer2(answer);
        log.info("result {}", result);
        
        if (result > 0) {
           redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 작성되었습니다");
            return "redirect:/knowledgedetail?kno="+answer.getKnowledgePostNo();
        } else {
           redirectAttributes.addFlashAttribute("message", "게시글 작성에 실패하였습니다. 다시 작성해주세요.");
            return "redirect:/knowledgeanswerinsert?ano="+answer.getKnowledgePostNo();
        }
    }
    //6-5. 지식인 글 삭제
    @GetMapping("/deleteknowledge")
    public String deleteknowledge(
            Model model,
            HttpSession session,
            @RequestParam(name = "kno") int postNo,
            RedirectAttributes redirectAttributes
    ) {
       log.info("postNo {}", postNo);
       
       int result = boardService.deletePost(postNo);
       log.info("result {}", result);
        if (result > 0) {
             redirectAttributes.addFlashAttribute("message", postNo + "번 글이 성공적으로 삭제되었습니다");
             return "redirect:/knowledgelist";
         } else {
             redirectAttributes.addFlashAttribute("message", "글 삭제에 실패했습니다.");
             return "redirect:/knowledgelist";
         }
    }

  //6-6. 지식인 질문 수정
    @GetMapping("/knowledgeupdate")
    public String moveKnowledgeQuestionUpdate(
    		 Model model,
             HttpSession session,
             @RequestParam(name = "kno") int postNo
             ) {
    	 model.addAttribute("postNo", postNo);

        Board selectedPost = boardService.selectKnowledgeDetail(postNo);
        selectedPost.setPostTitle(Utils.newLineClear(selectedPost.getPostTitle()));
        selectedPost.setPostContent(Utils.newLineClear(selectedPost.getPostContent()));
        model.addAttribute("selectedPost", selectedPost);
        log.info("selectedPost {}", selectedPost);

        Knowledge selectedPost2 = boardService.selectKnowledgeDetail2(postNo);
        model.addAttribute("selectedPost2", selectedPost2);
        log.info("selectedPost2 {}", selectedPost2);

        List<Attachment> attachList = boardService.selectAttachment(postNo);
        model.addAttribute("attachList", attachList);
        log.info("attachList {}", attachList);

        return "board/knowledge/knowledge-question-update";
    }
    @PostMapping("/knowledgeupdate")
	public String updateKnowledgeQuestion(
			Board board,
			Knowledge knowledge,
			@RequestParam(value = "upfile", required = false) List<MultipartFile> upfiles,
			HttpSession session,
			Model model,
			RedirectAttributes redirectAttributes,
			@RequestParam(value = "deletedAttachmentIds", required = false) List<String> deleteList
			) {
    	log.info("deleteList {}", deleteList);
    	log.info("upfiles {}", upfiles);
		// 이미지, 파일을 저장할 저장경로 얻어오기
		// /resources/images/board/{boardCode}/
    	String webPath = "/img/attachment/knowledge/";
        String currentDirectory = System.getProperty("user.dir");
        String FilesLocation = currentDirectory + "/src/main/resources/static"+webPath;

        Member loginUser = (Member) session.getAttribute("loginUser");
		board.setUserNo(loginUser.getUserId() + "");

		int result = 0;

		try {
			result = boardService.updateKnowledgeQuestion(board, knowledge, deleteList, upfiles, webPath, FilesLocation);
		} catch (Exception e) {
			log.error("error = {}", e.getMessage());
		}

		if (result > 0) {
			redirectAttributes.addFlashAttribute("message", "게시글 수정 성공");
			return "redirect:/knowledgedetail?kno="+board.getPostNo();
		} else {
			redirectAttributes.addFlashAttribute("message", "게시글 수정 실패");
			 return "redirect:/knowledgeupdate?kno="+board.getPostNo();
		}
	}
    // 6-7. 지식인 답글 삭제
    @GetMapping("/deleteknowledgeanswer")
    public String deleteknowledgeanswer(
            Model model,
            HttpSession session,
            @RequestParam(name = "ano") int postNo,
            @RequestParam(name = "kno") int knowledgePostNo,
            RedirectAttributes redirectAttributes
    ) {
       log.info("postNo {}", postNo);

       int result = boardService.deletePost(postNo);
       log.info("result {}", result);
        if (result > 0) {
             redirectAttributes.addFlashAttribute("message", "답글이 성공적으로 삭제되었습니다");
             return "redirect:/knowledgedetail?kno="+knowledgePostNo;
         } else {
             redirectAttributes.addFlashAttribute("message", "답글 삭제에 실패했습니다.");
             return "redirect:/knowledgedetail?kno="+knowledgePostNo;
         }
    }

    //6-8. 지식인 채택
    @GetMapping("/updateknowledgeacceped")
    public String updateknowledgeacceped(
            Model model,
            HttpSession session,
            @RequestParam(name = "ano") int postNo,
            @RequestParam(name = "kno") int knowledgePostNo,
            RedirectAttributes redirectAttributes
    ) {
       log.info("postNo {}", postNo);

       int result = boardService.updateknowledgeAcceped(postNo);
       log.info("result {}", result);
        if (result > 0) {
             redirectAttributes.addFlashAttribute("message", "성공적으로 채택되었습니다");
             return "redirect:/knowledgedetail?kno="+knowledgePostNo;
         } else {
             redirectAttributes.addFlashAttribute("message", "다시 시도해주세요.");
             return "redirect:/knowledgedetail?kno="+knowledgePostNo;
         }
    }
  //6-9. 지식인 추천수
    @GetMapping("/increaseknowledgeupvotes")
    public String increaseKnowledgeUpvotes(Model model, HttpSession session,
            @RequestParam(name = "kno") int postNo,
            @RequestParam(name = "uno") String userNo,
            HttpServletRequest req,
            HttpServletResponse res,
            RedirectAttributes redirectAttributes) {
        log.info("postNo {}", postNo);

        Member loginUser = (Member) session.getAttribute("loginUser");
        model.addAttribute("loginUser", loginUser);

        Knowledge selectedPost2 = boardService.selectKnowledgeDetail2(postNo);
        log.info("selectedPost2 {}", selectedPost2);

        if (selectedPost2 != null) {

            String userId = "";

            if (loginUser != null) {
                userId = loginUser.getUserId();
            }

            if (!userNo.equals(userId)) {

                Cookie cookie = null;

                Cookie[] cArr = req.getCookies();
                if (cArr != null && cArr.length > 0) {

                    for (Cookie c : cArr) {
                        if (c.getName().equals("increaseUpVotes2")) {
                            cookie = c;
                            break;
                        }
                    }
                }

                int result = 0;

                if (cookie == null) {
                    cookie = new Cookie("increaseUpVotes2", postNo + "");
                    result = boardService.increaseKnowledgeUpvotes(postNo);
                    redirectAttributes.addFlashAttribute("message", "추천되었습니다.");
                } else {

                    String[] arr = cookie.getValue().split("/");

                    List<String> list2 = Arrays.asList(arr);
                    log.info("list2 {}", list2);

                    if (list2.indexOf(postNo + "") == -1) {
                        cookie.setValue(cookie.getValue() + "/" + postNo);
                        result = boardService.increaseKnowledgeUpvotes(postNo);
                        redirectAttributes.addFlashAttribute("message", "추천되었습니다.");
                    }else
                    	 redirectAttributes.addFlashAttribute("message", "이미 추천하셨습니다.");
                }
                log.info("result {}", result);
                if (result > 0) {
                    selectedPost2.setUpVotes(selectedPost2.getUpVotes() + 1);
                    cookie.setPath(req.getContextPath());
                    cookie.setMaxAge(60 * 60 * 1);
                    res.addCookie(cookie);
                }
            }else {
            	redirectAttributes.addFlashAttribute("message", "자신의 글에는 추천할 수 없습니다.");
            }

        } else {
            redirectAttributes.addFlashAttribute("message", "추천수 증가 실패");
        }

        return "redirect:/knowledgedetail?kno="+postNo;
    }
    //6-10. 지식인 답변 수정
    @GetMapping("/knowledgeanswerupdate")
    public String moveKnowledgeAnswerUpdate(
    		 Model model,
             HttpSession session,
             @RequestParam(name = "ano") int postNo,
             @RequestParam(name = "kno") int knowledgePostNo
             ) {
    	 model.addAttribute("postNo", postNo);
    	 model.addAttribute("knowledgePostNo", knowledgePostNo);

    	Board board = boardService.selectKnowledgeDetail(postNo);
    	board.setPostContent(Utils.newLineClear(board.getPostContent()));
         model.addAttribute("board", board);
        log.info("board {}", board);

        return "board/knowledge/knowledge-answer-update";
    }
    @PostMapping("/knowledgeanswerupdate")
	public String knowledgeAnswerUpdate(
			Board board,
			Answer answer,
			HttpSession session,
			Model model,
			RedirectAttributes redirectAttributes
			) {
		// 이미지, 파일을 저장할 저장경로 얻어오기

		log.info("board ================== {}", board);
		int result = 0;

		try {
			result = boardService.knowledgeAnswerUpdate(board);
		} catch (Exception e) {
			e.printStackTrace();
		}


		if (result > 0) {
			redirectAttributes.addFlashAttribute("message", "게시글 수정 성공");
			return "redirect:/knowledgedetail?kno="+answer.getKnowledgePostNo();
		} else {
			redirectAttributes.addFlashAttribute("message", "게시글 수정 실패");
			 return "redirect:/knowledgeanswerupdate?ano="+board.getPostNo();
		}
	}

    //이찬우 구역 끝


    //------------------------------정승훈-----------------------------------------
    //정승훈 스터디화면으로 이동 그리고 조회
    @GetMapping("/study")
    public String selectStudy(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "pageSize", defaultValue = "4") int pageSize,
            @RequestParam(name = "searchOption", required = false) String searchOption,
            @RequestParam(name = "keyword", required = false) String keyword,
            Model model,
            @RequestParam Map<String, Object> paramMap,
            Board board,
            Study study,
            StudyApplicant sa,
            HttpServletRequest request
    ) {
        List<Board> list;
        long totalItems = 0;
//        pageItems = list


        if (searchOption != null && keyword != null) {
            // 검색 로직을 수행하고 결과를 처리
            totalItems = boardService.selectStudyListCount(searchOption, keyword);//현재 검색된 게시글의 총 갯수 board테이블 게시글 boardType = 'S'인경우
            // Board 데이터 조회
            list = boardService.selectStudyList(searchOption,keyword, page, pageSize,paramMap); //현재 검색된 게시글 board조회한것들 제목이랑 아이디
        } else {
            // 일반 목록을 가져옵니다
            totalItems = boardService.selectListCount(); //전체 일반목록의 총 갯수
            list = boardService.getSideStudyList(page, pageSize); //전체 일반목록의 게시글
        }
        long tatalPages =0;

        // 전체 항목 수를 가져옵니다 (yourService에서 구현)
        if(totalItems == 0) {
            totalItems = 1;
            tatalPages = (totalItems + pageSize - 1) / pageSize;
        }
        tatalPages = (totalItems + pageSize - 1) / pageSize;
// 모델에 데이터와 페이징 정보를 추가합니다
        model.addAttribute("sideMember", list);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchOption", searchOption);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("totalPages", tatalPages); // 총 페이지 수 추가

        System.out.println("토탈아이템의 값: " + totalItems);

        //2023-09-08 정승훈 수정


        // Study 데이터 조회
        List<Study> peopleList = boardService.selectStudyList(new Study());





        // Study와 Board 데이터를 연관시켜 Map에 저장 postRecruitmentCountMap1는 총 인원수임 study_Applicant 테이블에서 조회해옴
        Map<Integer, Integer> postRecruitmentCountMap1 = new HashMap<>();
        for (Board boardItem : list) {
            for (Study studyItem : peopleList) {
                if (boardItem.getPostNo() == studyItem.getPostNo()) {
                    postRecruitmentCountMap1.put(boardItem.getPostNo(), studyItem.getPeople());
                    break; // 해당하는 Study를 찾았으면 루프 종료
                }
            }
        }


        // 각 게시물의 POST_NO 목록을 가져옵니다.
        List<Integer> postNoList = list.stream()
                .map(Board::getPostNo)
                .collect(Collectors.toList());

        // POST_NO 목록을 paramMap에 추가합니다.
        paramMap.put("POST_NO_LIST", postNoList);

        log.info("paramMap {}" ,paramMap );

        // POST_NO별 RECRUITMENT_COUNT 조회 스터디 테이블에서의 list들 people에대한..
        List<Map<String, Object>> recruitmentCountList = boardService.selectRecruitmentCount(paramMap);

        log.info("recruitmentCountList {}" ,recruitmentCountList);

        // 각 POST_NO와 RECRUITMENT_COUNT를 매핑하여 Map에 저장
        Map<Integer, Integer> postRecruitmentCountMap = new HashMap<>();
        for (Map<String, Object> entry : recruitmentCountList) {
            Integer postNo = (Integer) entry.get("POST_NO");
            Integer recruitmentCount = ((Long) entry.get("RECRUITMENT_COUNT")).intValue(); // COUNT(*) 결과를 Integer로 변환
            postRecruitmentCountMap.put(postNo, recruitmentCount);
        }


        log.info("postRecruitmentCountMap {}" ,postRecruitmentCountMap );
        // 컨트롤러 모델에 POST_NO별 RECRUITMENT_COUNT를 추가
        model.addAttribute("postRecruitmentCountMap", postRecruitmentCountMap);

        model.addAttribute("postRecruitmentCountMap1", postRecruitmentCountMap1);


        log.info("postRecruitmentCountMap1 {}",postRecruitmentCountMap1);

        log.info("list {}",list);



        // 총 게시글 갯수

        //이미 신청한 회원은 그 게시글에 또 신청못하게 중복신청 방지





        model.addAttribute("param", paramMap); //보드타입
        model.addAttribute("list", list); //study에 대한 정보가 담김
        model.addAttribute("peopleList",peopleList);


        return "/board/study/studyList";
    }




    //스터디등록페이지로 이동
    @GetMapping("/study/insert")
    public String EnrollStudy(
            Model model
    ) {
        return "/board/study/insertStudy";
    }

    //스터디 등록
    @PostMapping("/study/insert")
    public String insertStudy(
            HttpSession session,
            Model model,
            Board board,
            Study study,
            StudyApplicant studyApplicant,
            RedirectAttributes redirectAttributes
    ) {
        Member loginUser = (Member) session.getAttribute("loginUser");
        board.setUserNo(loginUser.getUserNo()+""); //작성자의 번호도 넣어주기
        studyApplicant.setUserNo(loginUser.getUserNo()+"");


        Map<String, Object> boardData = new HashMap<>();
        boardData.put("board", board); // Board 객체를 Map에 추가
        boardData.put("study", study); // People 값을 Map에 추가
        boardData.put("studyApplicant", studyApplicant); // studyApplicant 객체를 Map에 추가


        int result = 0;


        result = boardService.insertBoardAndStudy(boardData);



        model.addAttribute("boardData", boardData); //보드타입



        log.info("POST_TITLE{}",board.getPostTitle());
        log.info("POST_CONTENT{}",board.getPostContent());
        log.info("result {}" , result);
        log.info("boardData {}" , boardData);

        if(result > 0) {
            redirectAttributes.addFlashAttribute("message", "스터디 등록이 완료되었습니다");
            return "redirect:/study";
        }else {
            redirectAttributes.addFlashAttribute("message", "스터디 등록을 실패했습니다.");
            return "common/main";
        }

    }


    //스터디 상세 페이지
    @GetMapping("/study/detail/{postNo}")
    public String detailStudy(
            @PathVariable("postNo") int postNo,
            HttpSession session,
            Model model,
            Board board,
            StudyApplicant studyApplicant,
            HttpServletRequest req,
            HttpServletResponse res
    ) {

        Member loginUser = (Member) session.getAttribute("loginUser");

        //게시글에있는 정보들 조회 (제목,작성자,작성날짜,조회수)
        Board dstudy = boardService.selectDetailStudy(postNo);

        //신청자 인원수 조회
        int studyDetailApplicant = boardService.studyDetailApplicant(postNo);


        //스터디 작성자가 설정한 인원수 가져오기 2023-09-08
        int boardstudypeple = boardService.selectStudypeople(postNo);


//        //댓글리스트 조회
        List<Reply> replyList = boardService.selectReplyList(postNo);

        int duUserNo = loginUser.getUserNo();
        Map<String, Integer> params = new HashMap<>();
        params.put("postNo", postNo);
        params.put("duUserNo", duUserNo);
        //중복신청방지

        StudyApplicant duStudy = boardService.duStudy(params);



        String url = "";

        log.info("postNo{}",postNo);
        log.info("dstudy {}",dstudy);
        log.info("loginUser {}",loginUser);
        log.info("boardstudypeple {}",boardstudypeple);
        System.out.println("adawdad"+duStudy);




        model.addAttribute("duStudy",duStudy);
        model.addAttribute("replyList",replyList);
        model.addAttribute("loginUser",loginUser);
        model.addAttribute("dstudy",dstudy);
        model.addAttribute("studyDetailApplicant",studyDetailApplicant);
        model.addAttribute("boardstudypeple",boardstudypeple);


        url="board/study/updateStudy";

        //게시글에있는 현재참여인원 조회

        return url;


    }

    //------------------------- 정승훈 작업---------------------------

    //댓글등록
    @PutMapping("/study/insertReply")
    @ResponseBody
    public int insertReply(Reply r, HttpSession session){

        Member m = (Member) session.getAttribute("loginUser");

        if(m != null){
            r.setUserNo(m.getUserNo()+"");
        }
        int result = boardService.insertReply(r);

        return result;
    }


    //스터디 댓글 조회
    @GetMapping("/study/selectReplyList")
    @ResponseBody
    public List<Reply> selectReplyList(int postNo,Model model){
        return boardService.selectReplyList(postNo);
    }


    //스터디 댓글 삭제
    @PostMapping("/study/deleteReply")
    @ResponseBody
    public int deleteReply(
            Reply r
    ){
        int result = boardService.deleteStudyReply(r);

        log.info("result {}",result);

        return result;
    }

    //스터디 신청자 등록
    @PostMapping("/study/applicant")
    public String insertApplicant(
            StudyApplicant sa,
            @RequestParam("userNo") String userNo, @RequestParam("postNo") int postNo,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ){
        Member m = (Member) session.getAttribute("loginUser");


        sa.setUserNo(m.getUserNo() + "");
        sa.setPostNo(postNo);

        log.info("postNo {}", postNo);
        log.info("sa {}", sa);

        int result = boardService.insertStudyApplicant(sa);

        log.info("result {}", result);

        if (result > 0) {

            //신청자 인원수 조회
            int studyDetailApplicant = boardService.studyDetailApplicant(postNo);


            //스터디 작성자가 설정한 인원수 가져오기 2023-09-08
            int boardstudypeple = boardService.selectStudypeople(postNo);

            if(studyDetailApplicant == boardstudypeple) {
                log.info("postNo : " + postNo);
                return "redirect:/chat/studyRoomCreate/"+ postNo;
            } else {
                redirectAttributes.addFlashAttribute("message", "스터디 신청이 완료되었습니다");
                return "redirect:/study";
            }
        } else {
            redirectAttributes.addFlashAttribute("message", "스터디 신청을 실패했습니다.");
            return "common/main";
        }


    }

    @PostMapping("/study/delete")
    public String deleteApplicant(
            @RequestParam("postNo") int postNo,
            RedirectAttributes redirectAttributes
    )
    {
        int result = boardService.studyDelete(postNo);

        if (result > 0) {
            redirectAttributes.addFlashAttribute("message", "스터디 게시글이 삭제되었습니다");
            return "redirect:/study";
        } else {
            redirectAttributes.addFlashAttribute("message", "스터디 게시글 삭제를 실패했습니다.");
            return "common/main";
        }
    }

    @GetMapping("/report/{postNo}")
    public String reportWrite(
            @PathVariable("postNo") int postNo,
            HttpSession session,
            Model model,
            HttpServletRequest req,
            HttpServletResponse res
    ) {
        Board reportTarget = boardService.selectBoard(postNo);

        model.addAttribute("reportTarget", reportTarget);

        return "common/report";

    }

    @PostMapping("/report.insert")
    public String insertReport(
            @RequestParam(value = "upfile", required = false) MultipartFile upfile,
            HttpSession session,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            @ModelAttribute("loginUser") Member loginUser,
            @RequestParam("reportTargetId") int reportTargetId,
            @RequestParam("reportTargetUser") int reportTargetUser,
            @RequestParam("report-category") String reportCategory,
            @RequestParam("report-content") String reportContent
    ) {
        int categoryId = Integer.parseInt(reportCategory);
        reportContent = Utils.XSSHandling(reportContent);
        reportContent = Utils.newLineHandling(reportContent);


        // 이미지, 파일을 저장할 저장경로 얻어오기
        String projectRootPath = System.getProperty("user.dir");
        String savePath = projectRootPath + File.separator + "hellomentor" + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "static" + File.separator + "img" + File.separator + "attachment" + File.separator + "report" + File.separator;



        // 디렉토리생성 , 해당디렉토리가 존재하지 않는다면 생성
        File dir = new File(savePath);
            System.out.println("Attempting to create directory: " + dir.getAbsolutePath());
        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println("Failed to create directory: " + dir.getAbsolutePath());
        }

        Map<String, Object> reportInfo = new HashMap<>();

        if (!upfile.isEmpty()) {
            //  파일명 재정의 해주는 함수.
            String changeName = Utils.saveFile(upfile, savePath);
            reportInfo.put("changeName", changeName);
            reportInfo.put("originName", upfile.getOriginalFilename());
            reportInfo.put("fileSize", upfile.getSize());
        }


        reportInfo.put("writerNo", loginUser.getUserNo());
        reportInfo.put("postContent", reportContent);
        reportInfo.put("categoryId", categoryId);
        reportInfo.put("targetUser", reportTargetUser);
        reportInfo.put("targetPost", reportTargetId);
        reportInfo.put("webPath", savePath);



        int result = 0;

        result = boardService.insertReport(reportInfo);

        if (result > 0) {
            redirectAttributes.addFlashAttribute("message", "성공적으로 신고가 접수 되었습니다");
            return "redirect:/main";
        } else {
            redirectAttributes.addFlashAttribute("message", "신고 접수가 실패했습니다.");
            return "redirect:/main";
        }

    }

    @RequestMapping("/main")
    public String main(Model model) {

        model.addAttribute("fLists", boardService.topFiveBoard("F"));
        model.addAttribute("kLists", boardService.topFiveBoard("K"));
        model.addAttribute("sLists", boardService.topFiveBoard("S"));
        model.addAttribute("mLists", boardService.newMentoring());

        return "common/main";
    }


}
