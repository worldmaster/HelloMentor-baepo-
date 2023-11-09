package com.kh.hellomentor;

import com.kh.hellomentor.member.model.vo.Member;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class HomeController {

    @RequestMapping("/")
    public String index() {
        return "login/login";
    }

    @RequestMapping("/mypage")
    public String myPage() {
        return "mypage/mypage";
    }


    @RequestMapping("/choose-sign-up")
    public String chooseSignUp() {
        return "login/choose-sign-up";
    }

    @RequestMapping("/sign-up")
    public String signUp() {
        return "login/sign-up";
    }

    @RequestMapping("/adminMain")
    public String adminMain() {
        return "admin/admin-main";
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        HttpSession session = request.getSession(false);
        if(session != null) {
            session.invalidate();
            redirectAttributes.addFlashAttribute("message", "로그아웃 되었습니다.");
        }
        return "redirect:/";
    }

    //정승훈 토큰충전
    @RequestMapping("/insert/token")
    public String insertToken(Model model, HttpSession session){
        Member loginUser = (Member) session.getAttribute("loginUser");
        model.addAttribute("token",loginUser.getToken());
        return "/token/tokenInsert";
    }

    //토큰환전
    @RequestMapping("/exchange/token")
    public String exchangeToken(Model model,HttpSession session){
        Member loginUser = (Member) session.getAttribute("loginUser");
        model.addAttribute("token",loginUser.getToken());
        return "/token/tokenExchange";
    }
}




