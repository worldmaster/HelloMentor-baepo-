package com.kh.hellomentor.member.controller;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.kh.hellomentor.matching.model.service.MatchingService;
import com.kh.hellomentor.member.model.vo.BaseResponse;
import com.kh.hellomentor.member.model.vo.Calendar;
import com.kh.hellomentor.member.model.vo.Payment;
import com.kh.hellomentor.member.model.vo.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.kh.hellomentor.member.model.service.EmailService;
import com.kh.hellomentor.member.model.service.MemberService;
import com.kh.hellomentor.member.model.vo.Member;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@Slf4j
public class MemberController {


    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    @Autowired
    private MemberService mService;

    @Autowired
    private MatchingService mtService;

    @Autowired
    private EmailService emailService;


    @PostMapping("login.me")
    public String loginMember(
            RedirectAttributes redirectAttributes,
            @ModelAttribute Member m,
            HttpSession session,
            Model model
    ) {
        Member loginUser = mService.loginUser(m);
        String url = "";
        if (loginUser != null) {
            if (loginUser.getUserId().equals("admin")) {
                session.setAttribute("loginUser", loginUser);
                redirectAttributes.addFlashAttribute("message", "관리자님 반갑습니다");
                url = "redirect:/admin/selectList";
            } else {
                session.setAttribute("loginUser", loginUser);
                model.addAttribute("message", loginUser.getUserName() + "님 반갑습니다");
                url = "redirect:/main";
            }
        } else {
            model.addAttribute("message", "아이디 또는 비밀번호를 확인해주세요.");
            url = "login/login";
        }
        return url;
    }

    @PostMapping("/sign.up")
    public String insertMember(@Validated Member m, HttpSession session, Model model, BindingResult bindingResult) {
        int result = mService.insertMember(m);
        String url = "";

        System.out.println(m);
        if (result > 0) {
            //성공시
            model.addAttribute("message", "회원가입을 축하드립니다. 로그인 해주세요");
            url = "login/login";
        } else {
            //실패 - 에러페이지로
            model.addAttribute("message", "회원가입 실패");
            url = "login/login";
        }
        return url;
    }

    @ResponseBody
    @GetMapping("/member/idCheck.me")
    public int idCheck(@RequestParam("userId") String userId) {
        int result = mService.idCheck(userId);

        return result;

    }

    @ResponseBody
    @PostMapping("login/mailConfirm")  //이메일    
    public String EmailCheck(@RequestParam(name = "email") String email) throws MessagingException {
        System.out.println(email);

        String authCode = emailService.sendEmail(email);

        return authCode;
    }


    @RequestMapping("/home_follow")
    public String homeFollow() {
        return "mypage/home_follow";
    }

    @RequestMapping("/home_following_list")
    public String getFollowList(Model model, HttpSession session) {
        Member loginUser = (Member) session.getAttribute("loginUser");

        int userNo = loginUser.getUserNo();
        List<Member> followingList = mService.getFollowList(userNo);
        List<Profile> profileList = mService.getFollowingProfileList(userNo);


        List<Map<String, Object>> combinedList = new ArrayList<>();
        for (Member member : followingList) {
            Map<String, Object> combinedInfo = new HashMap<>();
            combinedInfo.put("member", member);

            Profile profile = null;
            for (Profile p : profileList) {
                if (p.getUserNo() == member.getUserNo()) {
                    profile = p;
                    break;
                }
            }

            if (profile.getChangeName() != null) {
                combinedInfo.put("profile", profile);
            } else {
                profile.setFilePath("/img/");
                profile.setChangeName("default-profile.jpg");
                combinedInfo.put("profile", profile);
            }

            combinedList.add(combinedInfo);
        }
        model.addAttribute("combinedList", combinedList);

        return "mypage/home_following_list";
    }


    @RequestMapping("/home_follower_list")
    public String getFollowerList(Model model, HttpSession session) {
        Member loginUser = (Member) session.getAttribute("loginUser");
        int userNo = loginUser.getUserNo();
        List<Member> followerList = mService.getFollowerList(userNo);
        List<Profile> profileList = mService.getFollowerProfileList(userNo);


        List<Map<String, Object>> combinedList = new ArrayList<>();
        for (Member member : followerList) {
            Map<String, Object> combinedInfo = new HashMap<>();
            combinedInfo.put("member", member);

            Profile profile = null;
            for (Profile p : profileList) {
                if (p.getUserNo() == member.getUserNo()) {
                    profile = p;
                    break;
                }
            }

            if (profile.getChangeName() != null) {
                combinedInfo.put("profile", profile);
            } else {
                profile.setFilePath("/img/");
                profile.setChangeName("default-profile.jpg");
                combinedInfo.put("profile", profile);
            }

            combinedList.add(combinedInfo);
        }

        model.addAttribute("combinedList", combinedList);
        return "mypage/home_follower_list";
    }


    @RequestMapping("/profile_edit_info")

    public String profileEdit(Model model, HttpSession session) {
        Member loginUser = (Member) session.getAttribute("loginUser");
        model.addAttribute("loginUser", loginUser);
        return "mypage/profile_edit_info";
    }

    public String uploadProfileImg(MultipartFile file, int userNo) throws IOException {
        String currentDirectory = System.getProperty("user.dir");

        String uploadDir = currentDirectory + "/hellomentor/src/main/resources/static/img/profile/";
        String buildUploadDir = currentDirectory + "/hellomentor/build/resources/main/static/img/profile/";


        String fileName = "profile_" + userNo + ".jpg";

        File uploadPath = new File(uploadDir);
        File buildUploadPath = new File(buildUploadDir);

        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }

        if (!buildUploadPath.exists()) {
            buildUploadPath.mkdirs();
        }


        File destFile = new File(uploadPath, fileName);
        File buildDestFile = new File(buildUploadPath, fileName);

        file.transferTo(destFile);

        Files.copy(destFile.toPath(), buildDestFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }


    @PostMapping("/updateProfile")
    public ResponseEntity<String> updateProfile(@RequestParam("file") MultipartFile file,
                                                @RequestParam("originPwd") String originPwd,
                                                @RequestParam("newPwd") String newPwd,
                                                @RequestParam("intro") String intro,
                                                HttpSession session) {
        Member loginUser = (Member) session.getAttribute("loginUser");

        if (!originPwd.equals(loginUser.getUserPwd())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("현재 비밀번호가 일치하지 않습니다.");
        }

        try {
            if (!"notchanged".equals(file.getOriginalFilename())) {
                String fileName = uploadProfileImg(file, loginUser.getUserNo());

                Profile profile = new Profile();
                profile.setUserNo(loginUser.getUserNo());
                profile.setOriginName(file.getOriginalFilename());
                profile.setChangeName(fileName);
                profile.setFilePath("img/profile/");
                profile.setFileSize(file.getSize());

                if (mService.isProfileImgExists(loginUser.getUserNo())) {
                    mService.updateProfileImg(profile);
                } else {
                    mService.insertProfileImg(profile);
                }
            }

            if (!newPwd.isEmpty()) {
                loginUser.setUserPwd(newPwd);
            }

            loginUser.setIntroduction(intro);
            mService.updateMember(loginUser);
            return ResponseEntity.ok("프로필이 업데이트되었습니다.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("프로필 업데이트 중 오류가 발생했습니다. 다시 시도해주세요");
        }
    }


    @RequestMapping("/payment_payment_history")

    public String paymentHistory(Model model, HttpSession session) {
        Member loginUser = (Member) session.getAttribute("loginUser");
        String type = "p"; // P면 페이먼트
        List<Payment> payments = mService.getPaymentHistory(loginUser.getUserNo(), type);
        model.addAttribute("payments", payments);
        model.addAttribute("loginUser", loginUser);
        return "mypage/payment_payment_history";
    }

    @RequestMapping("/payment_exchange_history")

    public String exchangeHistory(Model model, HttpSession session) {
        Member loginUser = (Member) session.getAttribute("loginUser");
        String type = "e"; // E면 익스체인지
        List<Payment> payments = mService.getPaymentHistory(loginUser.getUserNo(), type);
        model.addAttribute("payments", payments);
        model.addAttribute("loginUser", loginUser);
        return "mypage/payment_exchange_history";
    }

    @RequestMapping("/devhelper_calendar")

    public String calendar(Model model, HttpSession session) {
        Member loginUser = (Member) session.getAttribute("loginUser");
        return "mypage/devhelper_calendar";
    }

    @PostMapping("/saveMemo")
    public ResponseEntity<String> saveMemo(@RequestBody Calendar memoRequest, HttpSession session) {
        Member loginUser = (Member) session.getAttribute("loginUser");
        try {
            memoRequest.setUserNo(loginUser.getUserNo());
            boolean isMemoExists = mService.isMemoExists(memoRequest);
            if (isMemoExists) {
                mService.updateMemo(memoRequest);
                return ResponseEntity.ok("메모가 업데이트되었습니다.");
            } else {
                mService.saveMemo(memoRequest);
                return ResponseEntity.ok("메모가 저장되었습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("메모 저장 중 오류 발생: " + e.getMessage());
        }

    }

    @PostMapping("/deleteMemo")
    public ResponseEntity<String> deleteMemo(@RequestBody Calendar memoRequest, HttpSession session) {
        Member loginUser = (Member) session.getAttribute("loginUser");
        memoRequest.setUserNo(loginUser.getUserNo());
        boolean isMemoExists = mService.isMemoExists(memoRequest);
        if (isMemoExists) {
            mService.deleteMemo(memoRequest);
            return ResponseEntity.ok("메모가 삭제되었습니다");
        } else {
            return ResponseEntity.ok("메모가 존재하지 않습니다.");
        }
    }

    @PostMapping("/loadMemo")
    public ResponseEntity<String> loadMemo(@RequestBody Calendar requestData, HttpSession session) {
        Member loginUser = (Member) session.getAttribute("loginUser");
        Calendar data = new Calendar();
        data.setUserNo(loginUser.getUserNo());
        data.setTodoDeadline(requestData.getTodoDeadline());
        data = mService.loadMemo(data);
        if (data != null) {
            return ResponseEntity.ok(data.getTodoContent());
        } else {
            return ResponseEntity.ok("");
        }

    }

    @RequestMapping("/devhelper_codelab")
    public String codelab(HttpSession session) {
        return "mypage/devhelper_codelab";
    }


    @PostMapping("/Compile")
    @ResponseBody
    public ResponseEntity<String> compileCode(@RequestParam("code") String code, @RequestParam("className") String className) {

        String currentDirectory = System.getProperty("user.dir");
        String FilesLocation = currentDirectory + "/src/main/resources/compile/";

        try {
            Path directoryPath = Paths.get(FilesLocation);
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            String fileName = className + ".java";
            Path filePath = Paths.get(FilesLocation, fileName);
            File javaFile = filePath.toFile();

            try (FileOutputStream fos = new FileOutputStream(javaFile)) {
                byte[] sourceCodeBytes = code.getBytes();
                fos.write(sourceCodeBytes);
            }


            String compileCmd = "javac -d " + FilesLocation + "/Classes " + FilesLocation + "/" + fileName;
            Process error = Runtime.getRuntime().exec(compileCmd);

            try (BufferedReader br = new BufferedReader(new InputStreamReader(error.getErrorStream()))) {
                StringBuilder errorMessage = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    errorMessage.append(line).append("\n");
                }
                if (errorMessage.toString().isEmpty()) {
                    return new ResponseEntity<>("컴파일 성공", HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(errorMessage.toString(), HttpStatus.BAD_REQUEST);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error occurred during compilation.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/Run")
    @ResponseBody
    public ResponseEntity<String> runCode(
            @RequestParam("classname") String className,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        try {
            String currentDirectory = System.getProperty("user.dir");
            String FilesLocation = currentDirectory + "/src/main/resources/compile/Classes/";
            String runCmd = "java -cp " + FilesLocation + " " + className;

            Process exe = Runtime.getRuntime().exec(runCmd);

            exe.waitFor();

            BufferedReader bin = new BufferedReader(new InputStreamReader(exe.getInputStream()));
            BufferedReader berr = new BufferedReader(new InputStreamReader(exe.getErrorStream()));
            StringBuilder result = new StringBuilder();
            String line;

            while ((line = bin.readLine()) != null) {
                result.append(line).append("\n");
            }

            if (result.length() == 0) {
                while ((line = berr.readLine()) != null) {
                    result.append(line).append("\n");
                }
            }

            bin.close();
            berr.close();


            return new ResponseEntity<>(result.toString(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error occurred during code execution.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/userExit")
    public ResponseEntity<String> userExit(HttpSession session) {
        Member loginUser = (Member) session.getAttribute("loginUser");


        if (mService.performExit(loginUser.getUserNo())) {
            session.invalidate();
            return ResponseEntity.ok("탈퇴 처리가 완료되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("탈퇴 처리 중 오류가 발생했습니다.");
        }
    }

    //정승훈 회원 토큰 충전
    @PostMapping("/insert/token")
    public String insertToken(
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes,
            Payment payment,
            Member m,
            @RequestParam(name = "token", defaultValue = "") String token
    ) {
        Member loginUser = (Member) session.getAttribute("loginUser");
        int newToken = Integer.parseInt(token); //선택된 토큰
        int currentToken = loginUser.getToken(); //기존에 있던 토큰
        int updatedToken = currentToken + newToken; //선택된 토큰과 + 기존에 있던 토큰

        m.setToken(updatedToken); // 업데이트된 토큰 값을 Member 객체에 설정
        m.setUserNo(loginUser.getUserNo());
        payment.setUserNo(loginUser.getUserNo());


        // token 값에 따라 price 설정
        int price;
        switch (token) {
            case "10":
                price = 1000;
                break;
            case "50":
                price = 5000;
                break;
            case "100":
                price = 10000;
                break;
            case "200":
                price = 20000;
                break;
            case "500":
                price = 50000;
                break;
            default:
                price = 0;
                break;
        }
        payment.setPrice(price); // payment 객체에 가격 설정

        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("payment", payment);
        tokenData.put("m", m);


        int result = mService.insertUpdateToken(tokenData);
        log.info("tokenData {}", tokenData);
        log.info("result {}", result);


        int updateToken = mService.getUpdateToken(loginUser.getUserNo());
        //변경된 토큰의 값을 다시 새로 세션에 담아줘야됨.
        loginUser.setToken(updateToken);
        session.setAttribute("loginUser", loginUser);


        if (result >= 0) {
            redirectAttributes.addFlashAttribute("message", "토큰충전 완료되었습니다");
            return "redirect:/main";
        } else {
            redirectAttributes.addFlashAttribute("message", "토큰충전을 실패했습니다.");
            return "common/main";
        }
    }

    @PostMapping("/exchange/token")
    public String exchageToken(
            HttpSession session,
            Member m,
            Model model,
            Payment payment,
            RedirectAttributes redirectAttributes) {

        Member loginUser = (Member) session.getAttribute("loginUser");

        m.setUserNo(loginUser.getUserNo());
        payment.setUserNo(loginUser.getUserNo());
        log.info("loginUser {}", loginUser.getToken());

        if (loginUser.getToken() == 0) {
            model.addAttribute("message", "토큰이 비어있습니다. 충전페이지로 이동합니다.");
            return "token/tokenInsert";
        } else {
            log.info("loginUser {}", loginUser.getToken());

            int result = mService.exchangeToken(m);

            // 변경된 토큰의 값을 가져오기
            int updateToken = mService.getUpdateToken(loginUser.getUserNo());
            int paymentresult = mService.paymentResult(loginUser.getUserNo());

            //변경된 토큰의 값을 다시 새로 세션에 담아줘야됨.
            loginUser.setToken(updateToken);
            session.setAttribute("loginUser", loginUser);

            log.info("result {}", result);
            log.info("loginUser {}", loginUser.getToken());

            model.addAttribute("token", loginUser.getToken());

            if (result > 0) {
                redirectAttributes.addFlashAttribute("message", "토큰환전이 완료되었습니다.");
                return "redirect:/main";
            } else {
                model.addAttribute("message", "오류발생");
                return "main";
            }
        }
    }


}














   
