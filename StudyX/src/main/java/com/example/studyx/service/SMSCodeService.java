package com.example.studyx.service;


//import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Service
public class SMSCodeService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private JavaMailSender mailSender;

    @CachePut(value="smsCode",key="#mail")
    public String getCodeToSMS(String mail){
        if(mail==null||"".equals(mail)){
            System.out.println("邮箱不能为空");
            return "error";
        }
        String[] patch ={"00000","0000","000","00","0",""};
        int hash="11111111111".hashCode();
        int encryption=20206666;
        long result = hash ^ encryption;
        long nowTime=System.currentTimeMillis();
        result=result^nowTime;
        long code=result%1000000;
        code=code<0?-code:code;
        String codeStr=code+"";
        int len=codeStr.length();
        System.out.println("生成验证码成功");
        return patch[len-1]+code;
    }
    public boolean send(String code,String mail) {
        if(code==null||code.length()!=6||"".equals(mail)||mail==null){
            System.out.println("发送失败");
            return false;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("2083978036@qq.com");
        message.setTo(mail);
        message.setSubject("it is a test for spring boot");
        message.setText("你好，验证码为："+code);
        try {
            mailSender.send(message);
            logger.info("邮件已发送。");
            return true;

        } catch (Exception e) {
            logger.error("发送邮件时发生异常了！", e);
            return false;
        }
    }
}

