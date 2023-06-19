package com.example.studyx.interfact;

import com.example.studyx.service.SMSCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SMS {
    @Autowired
    private SMSCodeService smsCodeService;



    public String getCode(String mail){
        return smsCodeService.getCodeToSMS(mail);
    }
    public boolean send(String content,String mail){
        return smsCodeService.send(content,mail);
    }

}
