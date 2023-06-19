package com.example.studyx.controller;

import com.example.studyx.interfact.SMS;
import com.example.studyx.service.SMSCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class SMSCodeController {
    @Autowired
    SMS s;
    @CrossOrigin
    @PostMapping(value = "/api/sms")
    public String getCode(@RequestBody String mail){
        String code=s.getCode(mail);
        boolean result= s.send(code,mail);
        if(result){
            return code;
        }
        return null;
    }
}
