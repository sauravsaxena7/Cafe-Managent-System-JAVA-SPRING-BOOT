package com.inn.cafe.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailUtils {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendSimpleMessage(String to, String sub, String message, List<String> list){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("sauravsrivastava121@gmail.com");
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(sub);
        simpleMailMessage.setText(message);



        if(list!=null && list.size()>0){
            simpleMailMessage.setCc(getCcArray(list));
            javaMailSender.send(simpleMailMessage);
        }
    }

    private String[] getCcArray(List<String> list){
        String[] cc =new String[list.size()];
        for (int i=0;i<list.size();i++){
            cc[i]=list.get(i);
        }
        return cc;
    }

    public void forgotPasswordSendEmail(String to,String subject, String password) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true);
        helper.setFrom("sauravsrivastava121@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText("",true);
    }
}
