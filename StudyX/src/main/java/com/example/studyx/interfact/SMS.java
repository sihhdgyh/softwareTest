package com.example.studyx.interfact;

import com.example.studyx.service.SMSCodeService;
import org.springframework.beans.factory.annotation.Autowired;

public class SMS {
    @Autowired
    private SMSCodeService smsCodeService;


    public String getCodeToSMS(String mail){
        return smsCodeService.getCodeToSMS(mail);
    }
    public boolean send(String content,String mail,String subject){
        return smsCodeService.send(content,mail);
    }

}
