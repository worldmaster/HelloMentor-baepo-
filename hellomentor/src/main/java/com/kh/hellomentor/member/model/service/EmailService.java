package com.kh.hellomentor.member.model.service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {
	
	@Autowired
    private JavaMailSender emailSender;
    private String authNum; // 인증 번호
    private static final String senderEmail = "hellomentor1021@gmail.com";

    // 인증번호 8자리 무작위 생성
    public void createCode() {
        Random random = new Random();
        StringBuffer key = new StringBuffer();

        for(int i=0; i<8; i++) {
            int idx = random.nextInt(3);

            switch (idx) {
                case 0 :
                    key.append((char) ((int)random.nextInt(26) + 97));
                    break;
                case 1:
                    key.append((char) ((int)random.nextInt(26) + 65));
                    break;
                case 2:
                    key.append(random.nextInt(9));
                    break;
            }
        }
        authNum = key.toString();
    }


    //실제 메일 전송
    public String sendEmail(String email) {
    	createCode();
     
        String body = "  요청하신 인증 번호입니다."+System.lineSeparator();
        body +="  인증번호: " + authNum + System.lineSeparator();
        body += System.lineSeparator();
        body +="감사합니다. - F5팀 일동 -" +System.lineSeparator();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(senderEmail);
        message.setSubject("hellomentor 회원가입 인증번호");
        
        message.setText(body);
    	
        
        //실제 메일 전송
        emailSender.send(message);

        return authNum; //인증 코드 반환
    }
}
