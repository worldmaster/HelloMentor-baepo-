package com.kh.hellomentor.common.interceptor;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.kh.hellomentor.member.model.vo.Member;

//로그인 안 한 사용자가 글쓰기 url로 직접 접근 하는 것을 방지하는 interceptor 
public class LoginInterceptor extends HandlerInterceptorAdapter {
	
	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws IOException {
		
		
		// 요청 url정보 // /spring/board/list/C -> board/list/C
		String requestUrl = req.getRequestURI().substring(req.getContextPath().length());
		
		// 로그인한 사용자 정보 
		HttpSession session = req.getSession();
		Member loginUser = (Member) session.getAttribute("loginUser");
		
		//로그인 한 사용자라면 true/ 로그인하지 않은 사용자 false
		
		if(loginUser != null) {
			return true;
		}else {
			
			res.sendRedirect(req.getContextPath()+"/");
			return false;
		}
		
		
	}
}
