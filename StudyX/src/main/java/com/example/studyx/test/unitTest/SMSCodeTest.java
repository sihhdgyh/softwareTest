package com.example.studyx.test.unitTest;

import com.example.studyx.service.SMSCodeService;
import com.example.studyx.utils.CsvUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@RestController
public class SMSCodeTest {
    @Autowired
    SMSCodeService smsCodeService;

    @CrossOrigin
    @GetMapping("/api/SMSCode/getCodeToSMSTest")
    public String getCodeToSMSTest(){
        //第一步：先获取csv文件的路径，通过BufferedReader类去读该路径中的文件
        String current=new File("").getAbsolutePath();
        String csvFilePath = current +"\\testFile\\origin\\getCodeToSMS.csv";
        String csvfile = current +"\\testFile\\result\\getCodeToSMSResult.csv";
        String headDataStr="";
        List<String> dataList = new ArrayList<>();
        if(new File(csvFilePath).exists()){
            List<String> list = CsvUtil.readFromCsv(csvFilePath);
            for (String data : list) {
                System.out.println(data);
                String result=data.split(",")[1];
                System.out.println(result);
                if("mail".equals(result)){
                    headDataStr=data+",result,pass";
                }else if("null".equals(result)){
                    String dataResult=smsCodeService.getCodeToSMS(null);
                    dataList.add(data+","+dataResult+","+(dataResult.equals(data.split(",")[2])?"yes":"no"));
                }else{
                    String dataResult=smsCodeService.getCodeToSMS(result);
                    dataList.add(data+","+dataResult+","+(dataResult.length()==6?"yes":"no"));
                }
            }

        }
        CsvUtil.writeToCsv(headDataStr, dataList, csvfile, false);
        return "ok";
    }
}
