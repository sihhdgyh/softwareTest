package com.example.studyx.test.integrationTest;

import com.example.studyx.interfact.LoginRegister;
import com.example.studyx.interfact.SMS;
import com.example.studyx.pojo.User;
import com.example.studyx.service.UserService;
import com.example.studyx.utils.CsvUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
public class moduleTest {
    @Autowired
    SMS sms=new SMS();
    @Autowired
    LoginRegister loginRegister=new LoginRegister();

    @Test
    @CrossOrigin
    @GetMapping("/api/HLD_003_INT_001")
    public void HLD_003_INT_001(){

        //第一步：先获取csv文件的路径，通过BufferedReader类去读该路径中的文件
        String current=new File("").getAbsolutePath();
        String csvFilePath = current +"\\softwareTesting_Front\\public\\testUsecases\\testFile\\integrationTest\\HLD_003_INT_001.csv";
        String csvfile = current +"\\softwareTesting_Front\\public\\testUsecases\\testFile\\integrationTest\\HLD_003_INT_001Result.csv";
        if(csvFilePath.contains("StudyX\\")){
            csvfile= csvfile.replace("StudyX\\","");
            csvFilePath=csvFilePath.replace("StudyX\\","");
            System.out.println(csvfile);
        }

        String headDataStr="";
        List<String> dataList = new ArrayList<>();
        if(new File(csvFilePath).exists()){
            List<String> list = CsvUtil.readFromCsv(csvFilePath);
            for (String data : list) {
                boolean dataResult=false;
                String result=data.split(",")[2];
                System.out.println(result);
                if("code".equals(result)){
                    headDataStr=data+",actual,result";
                }else{
                    if("null".equals(result)){
                        dataResult=sms.send(null,data.split(",")[3]);
                    }else if("null".equals(data.split(",")[3])){
                        dataResult=sms.send(data.split(",")[2],null);
                    }else{
                        dataResult=sms.send(data.split(",")[2],data.split(",")[3]);
                    }
                    if((dataResult?"true":"false").equals(data.split(",")[4])){
                        dataList.add(data+","+dataResult+","+"yes");
                    }else{
                        dataList.add(data+","+dataResult+","+"no");
                    }

                }
            }

        }
        CsvUtil.writeToCsv(headDataStr, dataList, csvfile, false);
        return;

    }


    @CrossOrigin
    @GetMapping("/api/HLD_001_INT_001")
    public void HLD_001_INT_001(){
        //第一步：先获取csv文件的路径，通过BufferedReader类去读该路径中的文件
        String current=new File("").getAbsolutePath();
        String csvFilePath = current +"\\softwareTesting_Front\\public\\testUsecases\\testFile\\integrationTest\\HLD_001_INT_001.csv";
        String csvfile = current +"\\softwareTesting_Front\\public\\testUsecases\\testFile\\integrationTest\\HLD_001_INT_001Result.csv";
        if(csvFilePath.contains("StudyX\\")){
            csvfile= csvfile.replace("StudyX\\","");
            csvFilePath=csvFilePath.replace("StudyX\\","");
            System.out.println(csvfile);
        }

        String headDataStr="";
        List<String> dataList = new ArrayList<>();
        if(new File(csvFilePath).exists()){
            List<String> list = CsvUtil.readFromCsv(csvFilePath);
            for (String data : list) {
                int dataResult=0;
                String result=data.split(",")[2];
                System.out.println(result);
                if("username".equals(result)){
                    headDataStr=data+",actual,result";
                }else{
                    User user=new User();
                    user.setUsername(data.split(",")[2]);
                    user.setPassword(data.split(",")[3]);
                    user.setMail(data.split(",")[4]);
                    dataResult=loginRegister.register(user);
                    if((dataResult+"").equals(data.split(",")[5])){
                        dataList.add(data+","+dataResult+","+"yes");
                    }else{
                        dataList.add(data+","+dataResult+","+"no");
                    }
                }
            }

        }
        CsvUtil.writeToCsv(headDataStr, dataList, csvfile, false);
        return;

    }
}
