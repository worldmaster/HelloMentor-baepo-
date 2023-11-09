package com.kh.hellomentor.matching.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.kh.hellomentor.board.model.vo.Board;
import com.kh.hellomentor.chat.model.repo.ChatRoomRepository;
import com.kh.hellomentor.member.model.vo.Follow;
import com.kh.hellomentor.member.model.vo.Payment;
import com.kh.hellomentor.member.model.vo.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.kh.hellomentor.matching.model.service.MatchingService;
import com.kh.hellomentor.matching.model.vo.Matching;
import com.kh.hellomentor.matching.model.vo.Mentoring;
import com.kh.hellomentor.member.model.vo.Member;
import com.kh.hellomentor.common.template.Pagination;
import com.kh.hellomentor.common.vo.PageInfo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
@SessionAttributes({"loginUser"})
public class MatchingController {


    @Autowired
    private MatchingService matchingService;

    @Autowired
    private ChatRoomRepository repository;

    @GetMapping("/mentoring")
    public String selectList(
            @RequestParam(value = "currentPage", defaultValue = "1") int currentPage,
            HttpSession session,
            Model model,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "pageSize", defaultValue = "9") int pageSize,
            @RequestParam(name = "searchOption", required = false) String searchOption,
            @RequestParam(name = "keyword", required = false) String keyword,
            //검색요청이 들어오는경우 paramMap내부에는 keyword, condition
            @RequestParam Map<String, Object> paramMap
    ) {
//
        Member loginUser = (Member) session.getAttribute("loginUser");

        if (loginUser == null) {
            log.info("로그인정보가 없어요유");
        } else {
//
            String memberType = loginUser.getMemberType();
            // memberType을 문자열에서 정수로 변환
            int memberTypeId = 0; // 기본값 설정
            if ("E".equals(memberType)) {
                memberTypeId = 1; // 예를 들어, 'E'를 1로 매핑
            } else if ("O".equals(memberType)) {
                memberTypeId = 2; // 예를 들어, 'O'를 2로 매핑
            }
            paramMap.put("memberType", memberType);

            List<Mentoring> list;
            int totalItems = 0;

            if (searchOption != null && keyword != null) {
                // 검색 로직을 수행하고 결과를 처리
                totalItems = (matchingService.selectMentorListCount(searchOption, keyword, memberTypeId));//현재 검색된 게시글의 총 갯수 board테이블 게시글 boardType = 'S'인경우
                // Board 데이터 조회
                list = matchingService.selectMentorList(searchOption, keyword, page, pageSize, paramMap); //현재 검색된 게시글 board조회한것들 제목이랑 아이디
            } else {
                // 일반 목록을 가져옵니다
                totalItems = matchingService.selectListCount(memberTypeId); //전체 일반목록의 총 갯수
                list = matchingService.getSideMentorList(page, pageSize, memberType); //전체 일반목록의 게시글
            }
            long tatalPages = 0;

            // 전체 항목 수를 가져옵니다 (yourService에서 구현)
            if (totalItems == 0) {
                totalItems = 1;
                tatalPages = (totalItems + pageSize - 1) / pageSize;
            }
            tatalPages = (totalItems + pageSize - 1) / pageSize;
//          모델에 데이터와 페이징 정보를 추가합니다
            model.addAttribute("sideMember", list);
            model.addAttribute("currentPage", page);
            model.addAttribute("keyword", keyword);
            model.addAttribute("searchOption", searchOption);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalItems", totalItems);
            model.addAttribute("totalPages", tatalPages); // 총 페이지 수 추가

            System.out.println("토탈아이템의 값: " + totalItems);
            System.out.println("토탈페이지수: " + tatalPages);
            System.out.println("page: " + page);



            model.addAttribute("list", list);
            model.addAttribute("param", paramMap);


            log.info("list {}", list);
            log.info("param {}", paramMap);


        }
        return "matching/matching-list";

    }

    //멘토/멘티 등록페이지로 이동
    @GetMapping("/mentoring/insert")
    public String enrollMentoring(
            Model model
    ) {
        return "matching/insertMemberInfo";
    }

    //멘토/멘티 등록
    @PostMapping("/mentoring/insert")
    public String insertMentoring(
            Mentoring mt,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes,
            @RequestParam("codeLang") String codeLang
    ) {
        Member loginUser = (Member) session.getAttribute("loginUser");
        mt.setUserNo(loginUser.getUserNo()); //사용자 번호 설정도 같이 넣어줘야됨.
        mt.setCodeLang(codeLang);

        int result = 0;



        result = matchingService.insertMentoring(mt);

        System.out.println("삽입이되나?"+result);


        if (result > 0 && loginUser.getMemberType().equals("E")) {
            redirectAttributes.addFlashAttribute("message", "멘티 등록이 완료되었습니다.멘토의 제안을 기다려주세요.");
            return "redirect:/main";
        } else if (result > 0 && loginUser.getMemberType().equals("O")) {
            redirectAttributes.addFlashAttribute("message", "멘토의 등록이 완료되었습니다. 멘티의 신청을 기다려주세요.");
            return "redirect:/main";
        } else {
            return "common/main";
        }


    }


    //멘토 멘티 상세 모달
    @GetMapping("/detail/mentoring")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getData(
            @RequestParam int postNo
    ) {
        Map<String, Object> responseData = new HashMap<>();


        Member member = matchingService.getMemberByPostNo(postNo);
        Profile profile = matchingService.getProfileByPostNo(postNo);
        Mentoring mentor = matchingService.getMentoringByPostNo(postNo);


        log.info("member {}", member);
        log.info("mentor {}", mentor);
        log.info("profile {}", profile);


        responseData.put("member", member);
        responseData.put("mentor", mentor);
        responseData.put("profile", profile);

        return ResponseEntity.ok(responseData);
    }


    //토큰 결제 하기
    @PostMapping("/payment")
    @ResponseBody
    public int processPayment(
            @RequestParam("postNo") int postNo,
            @RequestParam("userNo") int userNo,
            @RequestParam("token1") int token1,
            Payment payment,
            Matching matching,
            HttpSession session,
            Member m
    ) {
        //userNo는 게시글을 등록한 사람의 번호임 즉 결제나 제안을 하는 userNo가 아니고 받는사람.
        Member loginUser = (Member) session.getAttribute("loginUser");


        payment.setUserNo(loginUser.getUserNo());


        matching.setMatchingRegisNo(postNo); //게시글 등록 번호
        matching.setMentorNo(loginUser.getUserNo()); //보내는사람 번호 즉 로그인한 사람의 번호
        matching.setMenteeNo(userNo); //받는사람의 번호 게시글 등록한 사람의 번호


        int newToken = token1; //선택된 토큰
        int currentToken = loginUser.getToken(); //기존에 있던 토큰
        int updateToken = currentToken - newToken; //선택된 토큰과 기존에 있는 토큰 빼기

        m.setToken(updateToken); //업데이트된 토큰의 값을 membmer객체에 넣기
        m.setUserNo(loginUser.getUserNo()); //누구의 토큰을 업데이트 했는지 알아야 되니까


        Map<String, Object> paymentData = new HashMap<>();
        paymentData.put("payment", payment);
        paymentData.put("matching", matching);
        paymentData.put("member", m);

        System.out.println("게시글번호" + postNo);
        System.out.println("로그인한사람 번호" + loginUser.getUserNo());
        System.out.println("작성한사람의 번호" + userNo);


        int result = 0;

        result = matchingService.insertPaymentMatching(paymentData);
        if(result > 0){
            repository.createChatRoomDTO(userNo, loginUser.getUserNo());
        }

        System.out.println("dawdad");
        System.out.println(result);

        int supdateToken = matchingService.getUpdateToken(loginUser.getUserNo());

        Map<String, Object> mentorToken = new HashMap<>();

        mentorToken.put("token1",token1); //멘토에게 입금될 토큰의 값
        mentorToken.put("userNo",userNo); //해당 멘토의 번호

        int mentorupdateToken = matchingService.mentorupdateToken(mentorToken);


        loginUser.setToken(updateToken);
        session.setAttribute("loginUser", loginUser);

        return result;
    }

    @PostMapping("/suggest")
    @ResponseBody
    public int suggest(
            @RequestParam("postNo") int postNo,
            @RequestParam("userNo") int userNo,
            Matching matching,
            HttpSession session
    ) {
        //userNo는 게시글을 등록한 사람의 번호임 즉 결제나 제안을 하는 userNo가 아니고 받는사람.
        Member loginUser = (Member) session.getAttribute("loginUser");

        matching.setMatchingRegisNo(postNo); //게시글 등록 번호
        matching.setMentorNo(loginUser.getUserNo()); //보내는사람 번호 즉 로그인한 사람의 번호
        matching.setMenteeNo(userNo); //받는사람의 번호 게시글 등록한 사람의 번호


        Map<String, Object> suggestData = new HashMap<>();
        suggestData.put("matching", matching);

        int result = 0;

        result = matchingService.suggestMatching(suggestData);

        return result;
    }
    @PostMapping("/follow")
    @ResponseBody
    public int follow(
            @RequestParam("userNo") int userNo, //팔로우를 받는사람
            Follow follow,
            HttpSession session
    ) {
        //userNo는 게시글을 등록한 사람의 번호임 즉 결제나 제안을 하는 userNo가 아니고 받는사람.
        Member loginUser = (Member) session.getAttribute("loginUser");

        //팔로우를 받는사람
        follow.setToUser(userNo);
        //팔로우를 보내는사람
        follow.setFromUser(loginUser.getUserNo());

        int result = 0;

        result = matchingService.follow(follow);

        System.out.println("팔로우 잘되나?" +result);

        return result;
    }


    @PostMapping("/unfollow")
    @ResponseBody
    public int unfollow(
            @RequestParam("userNo") int userNo, //팔로우를 받는사람
            Follow follow,
            HttpSession session
    ) {
        //userNo는 게시글을 등록한 사람의 번호임 즉 결제나 제안을 하는 userNo가 아니고 받는사람.
        Member loginUser = (Member) session.getAttribute("loginUser");

        //팔로우를 받는사람
        follow.setToUser(userNo);
        //팔로우를 하는사람
        follow.setFromUser(loginUser.getUserNo());

        int result = 0;

        result = matchingService.unfollow(follow);

        System.out.println("언팔로우 잘되나?" +result);

        return result;
    }

    //   보낸 제안내역
    @RequestMapping("/mentoring_mentor_applications")
    public String mentor_applications(HttpSession session, Model model) {
        Member loginUser = (Member) session.getAttribute("loginUser");
        int userNo = loginUser.getUserNo();


        List<Member> mentorList = matchingService.getMentorList(userNo);
        List<Profile> mentorProfileList = matchingService.getMentorProfileList(userNo);
        List<Mentoring> mentoringList = matchingService.getMentoringList(userNo);
        List<Matching> matchingList = matchingService.getMatchingList(userNo);


        List<Map<String, Object>> combinedList = new ArrayList<>();

        for (Member mentor : mentorList) {
            Map<String, Object> combinedInfo = new HashMap<>();
            combinedInfo.put("userNo", mentor.getUserNo());
            combinedInfo.put("userId", mentor.getUserId());
            combinedInfo.put("introduction", mentor.getIntroduction());
            combinedInfo.put("memberType", mentor.getMemberType());

            Profile profile = null;
            for (Profile p : mentorProfileList) {
                if (p.getUserNo() == mentor.getUserNo()) {
                    profile = p;
                    break;
                }
            }

            if (profile != null) {
                combinedInfo.put("filePath", profile.getFilePath());
                combinedInfo.put("changeName", profile.getChangeName());

            } else {
                Profile defaultProfile = new Profile();
                defaultProfile.setFilePath("/img/");
                defaultProfile.setChangeName("default-profile.jpg");
                combinedInfo.put("filePath", defaultProfile.getFilePath());
                combinedInfo.put("changeName", defaultProfile.getChangeName());


            }


            for (Matching matching : matchingList) {
                if (matching.getMenteeNo() == mentor.getUserNo()) {
                    for (Mentoring mentoring : mentoringList) {
                        if (matching.getMatchingRegisNo() == mentoring.getRegisNo()) {
                            combinedInfo.put("title", mentoring.getTitle());
                            combinedInfo.put("regisNo", mentoring.getRegisNo());
                            break;
                        }
                    }
                    break;
                }
            }

            combinedList.add(combinedInfo);

        }

        combinedList.removeIf(combinedInfo -> {
            Matching matching = (Matching) combinedInfo.get("matching");
            return matching != null && "C".equals(matching.getStatus());
        });
        model.addAttribute("combinedList", combinedList);
        log.info("combinedList: " + combinedList);
        return "mypage/mentoring_mentor_applications";
    }


    // 받은 제안내역
    @RequestMapping("/mentoring_mentor_applications2")
    public String mentor_applications2(HttpSession session, Model model) {
        Member loginUser = (Member) session.getAttribute("loginUser");
        int userNo = loginUser.getUserNo();

        List<Member> mentorList = matchingService.getMentorList2(userNo);
        List<Profile> mentorProfileList = matchingService.getMentorProfileList2(userNo);
        List<Mentoring> mentoringList = matchingService.getMentoringList2(userNo);
        List<Matching> matchingList = matchingService.getMatchingList2(userNo);

        List<Map<String, Object>> combinedList = new ArrayList<>();

        for (Member mentor : mentorList) {
            Map<String, Object> combinedInfo = new HashMap<>();
            combinedInfo.put("userNo", mentor.getUserNo());
            combinedInfo.put("userId", mentor.getUserId());
            combinedInfo.put("introduction", mentor.getIntroduction());
            combinedInfo.put("memberType", mentor.getMemberType());

            Profile profile = null;
            for (Profile p : mentorProfileList) {
                if (p.getUserNo() == mentor.getUserNo()) {
                    profile = p;
                    break;
                }
            }

            if (profile != null) {
                combinedInfo.put("filePath", profile.getFilePath());
                combinedInfo.put("changeName", profile.getChangeName());
            } else {
                Profile defaultProfile = new Profile();
                defaultProfile.setFilePath("/img/");
                defaultProfile.setChangeName("default-profile.jpg");
                combinedInfo.put("filePath", defaultProfile.getFilePath());
                combinedInfo.put("changeName", defaultProfile.getChangeName());
            }

            for (Matching matching : matchingList) {
                if (matching.getMentorNo() == mentor.getUserNo()) {
                    combinedInfo.put("status", matching.getStatus());
                    for (Mentoring mentoring : mentoringList) {
                        if (matching.getMatchingRegisNo() == mentoring.getRegisNo()) {
                            combinedInfo.put("title", mentoring.getTitle());
                            combinedInfo.put("regisNo", mentoring.getRegisNo());
                            break;
                        }
                    }
                    break;
                }
            }

            combinedList.add(combinedInfo);
        }


        combinedList.removeIf(combinedInfo -> {
            String status = (String) combinedInfo.get("status");
            return status != null && "C".equals(status);
        });


        model.addAttribute("combinedList", combinedList);
        log.info("combinedList: " + combinedList);
        return "mypage/mentoring_mentor_applications2";
    }

    @DeleteMapping("/mentoring_cancel")
    @ResponseBody
    public Map<String, Object> mentoring_cancel(@RequestParam("userNo") int userNo, @RequestParam("regisNo") int regisNo) {
        Map<String, Object> response = new HashMap<>();

        try {
            matchingService.mentoring_cancel(userNo, regisNo);
            response.put("success", true);
            response.put("message", "제안이 취소되었습니다.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "제안 취소에 실패했습니다.");
        }

        return response;
    }

    @PostMapping("/mentoring_accept")
    @ResponseBody
    public Map<String, Object> mentoring_accept(@RequestParam("userNo") int userNo, @RequestParam("regisNo") int regisNo, HttpSession session) {
        Member loginUser = (Member) session.getAttribute("loginUser");
        int loginuserNo = loginUser.getUserNo();
        Map<String, Object> response = new HashMap<>();


        try {
            matchingService.mentoring_accept(userNo, regisNo, loginuserNo);
            repository.createChatRoomDTO(userNo, loginuserNo);
            response.put("success", true);
            response.put("message", "제안이 수락되었습니다. 채팅방을 확인하세요");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "제안 수락에 실패했습니다.");
        }

        return response;
    }






}







