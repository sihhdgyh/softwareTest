package com.example.studyx.test.unitTest;

import com.example.studyx.pojo.Category;
import com.example.studyx.pojo.User;
import com.example.studyx.service.BookService;
import com.example.studyx.service.SMSCodeService;
import com.example.studyx.service.UserService;
import com.example.studyx.utils.CsvUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
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
    SMSCodeService smsCodeService=new SMSCodeService();
    @Autowired
    BookService bookService=new BookService();
    @Autowired
    UserService userService=new UserService();

    @Before
    public void init(){
        smsCodeService=new SMSCodeService();
        bookService=new BookService();
        userService=new UserService();
    }

    @Test
    @CrossOrigin
    @GetMapping("/api/SMSCode/getCodeToSMSTest")
    public void getCodeToSMSTest(){
        //第一步：先获取csv文件的路径，通过BufferedReader类去读该路径中的文件
        String current=new File("").getAbsolutePath();
        String csvFilePath = current +"\\softwareTesting_Front\\public\\testUsecases\\testFile\\unitTest\\getCodeToSMS.csv";
        String csvfile = current +"\\softwareTesting_Front\\public\\testUsecases\\testFile\\unitTest\\getCodeToSMSResult.csv";
        if(csvFilePath.contains("StudyX\\")){
            csvfile= csvfile.replace("StudyX\\","");
            csvFilePath=csvFilePath.replace("StudyX\\","");
            System.out.println(csvfile);
        }
        System.out.println(csvFilePath);
        System.out.println(csvfile);
        String headDataStr="";
        List<String> dataList = new ArrayList<>();
        if(new File(csvFilePath).exists()){
            List<String> list = CsvUtil.readFromCsv(csvFilePath);
            for (String data : list) {
                System.out.println(data);
                String result=data.split(",")[2];
                System.out.println(result);
                if("mail".equals(result)){
                    headDataStr=data+",actual,result";
                }else if("null".equals(result)){
                    String dataResult=smsCodeService.getCodeToSMS(null);
                    try{

                        Assert.assertEquals(dataResult,data.split(",")[3]);
                        dataList.add(data+","+dataResult+","+"yes");
                    }catch (Exception e){
                        System.out.println(e);
                        dataList.add(data+","+dataResult+","+"no");
                    }
                }else{
                    String dataResult=smsCodeService.getCodeToSMS(result);
                    dataList.add(data+","+dataResult+","+(dataResult.length()==6?"yes":"no"));
                }
            }

        }
        CsvUtil.writeToCsv(headDataStr, dataList, csvfile, false);
    }

    @Test
    @CrossOrigin
    @GetMapping("/api/SMSCode/sendTest")
    public void sendTest(){

        //第一步：先获取csv文件的路径，通过BufferedReader类去读该路径中的文件
        String current=new File("").getAbsolutePath();
        String csvFilePath = current +"\\softwareTesting_Front\\public\\testUsecases\\testFile\\unitTest\\send.csv";
        String csvfile = current +"\\softwareTesting_Front\\public\\testUsecases\\testFile\\unitTest\\sendResult.csv";
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
                        dataResult=smsCodeService.send(null,data.split(",")[3]);
                    }else if("null".equals(data.split(",")[3])){
                        dataResult=smsCodeService.send(data.split(",")[2],null);
                    }else{
                        dataResult=smsCodeService.send(data.split(",")[2],data.split(",")[3]);
                    }
                    try{
                        Assert.assertEquals(dataResult?"true":"false",data.split(",")[4]);
                        dataList.add(data+","+dataResult+","+"yes");
                    }catch (Exception e){
                        System.out.println(e);
                        dataList.add(data+","+dataResult+","+"no");
                    }
                }
            }

        }
        CsvUtil.writeToCsv(headDataStr, dataList, csvfile, false);


    }

    @Test
    @CrossOrigin
    @GetMapping("/api/Book/searchTest")
    public void searchTest(){
        //第一步：先获取csv文件的路径，通过BufferedReader类去读该路径中的文件
        String current=new File("").getAbsolutePath();
        String csvFilePath = current +"\\softwareTesting_Front\\public\\testUsecases\\testFile\\unitTest\\search.csv";
        String csvfile = current +"\\softwareTesting_Front\\public\\testUsecases\\testFile\\unitTest\\searchResult.csv";
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
                List<Category> dataResult=null;
                String result=data.split(",")[2];
                System.out.println(result);
                if("keywords".equals(result)){
                    headDataStr=data+",actual,result";
                }else{
                    dataResult=bookService.Search(data.split(",")[2]);
                    try{
                        Assert.assertEquals(dataResult.size()+"",data.split(",")[3]);
                        dataList.add(data+","+dataResult.size()+","+"yes");
                    }catch (Exception e){
                        System.out.println(e);
                        dataList.add(data+","+dataResult.size()+","+"no");
                    }
                }
            }

        }
        CsvUtil.writeToCsv(headDataStr, dataList, csvfile, false);


    }

    @Test
    @CrossOrigin
    @GetMapping("/api/Book/getBookidTest")
    public void getBookidTest(){
        //第一步：先获取csv文件的路径，通过BufferedReader类去读该路径中的文件
        String current=new File("").getAbsolutePath();
        String csvFilePath = current +"\\softwareTesting_Front\\public\\testUsecases\\testFile\\unitTest\\getBookid.csv";
        String csvfile = current +"\\softwareTesting_Front\\public\\testUsecases\\testFile\\unitTest\\getBookidResult.csv";
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
                Category dataResult=null;
                String result=data.split(",")[2];
                System.out.println(result);
                if("isbn".equals(result)){
                    headDataStr=data+",actual,result";
                }else{
                    dataResult=bookService.getBookid(data.split(",")[2]);
                    if(data.split(",")[3].equals("null")){
                        try{
                            Assert.assertEquals(dataResult,null);
                            dataList.add(data+","+"null"+","+"yes");
                        }catch (Exception e){
                            System.out.println(e);
                            dataList.add(data+","+"null"+","+"no");
                        }
                    }else {
                        try {
                            System.out.println(dataResult.toString().replace(',', ' '));
                            Assert.assertEquals(dataResult.toString().replace(',', ' '), data.split(",")[3]);

                            dataList.add(data + "," + dataResult.toString().replace(',', ' ') + "," + "yes");
                        } catch (Exception e) {
                            System.out.println(e);
                            dataList.add(data + "," + dataResult.toString().replace(',', ' ') + "," + "no");
                        }
                    }
                }
            }

        }
        CsvUtil.writeToCsv(headDataStr, dataList, csvfile, false);


    }


    @Test
    @CrossOrigin
    @GetMapping("/api/User/registerTest")
    public void registerTest(){
        //第一步：先获取csv文件的路径，通过BufferedReader类去读该路径中的文件
        String current=new File("").getAbsolutePath();
        String csvFilePath = current +"\\softwareTesting_Front\\public\\testUsecases\\testFile\\unitTest\\register.csv";
        String csvfile = current +"\\softwareTesting_Front\\public\\testUsecases\\testFile\\unitTest\\registerResult.csv";
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
                    dataResult=userService.register(user);
                    try{
                        Assert.assertEquals(dataResult+"",data.split(",")[5]);
                        dataList.add(data+","+dataResult+","+"yes");
                    }catch (Exception e){
                        System.out.println(e);
                        dataList.add(data+","+dataResult+","+"no");
                    }
                }
            }

        }
        CsvUtil.writeToCsv(headDataStr, dataList, csvfile, false);


    }

    @Test
    @CrossOrigin
    @GetMapping("/api/User/findpasswordTest")
    public void findpasswordTest(){
        //第一步：先获取csv文件的路径，通过BufferedReader类去读该路径中的文件
        String current=new File("").getAbsolutePath();
        String csvFilePath = current +"\\softwareTesting_Front\\public\\testUsecases\\testFile\\unitTest\\findpassword.csv";
        String csvfile = current +"\\softwareTesting_Front\\public\\testUsecases\\testFile\\unitTest\\findpasswordResult.csv";
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
                if("password".equals(result)){
                    headDataStr=data+",actual,result";
                }else{
                    User user=new User();
                    user.setPassword(data.split(",")[2]);
                    user.setMail(data.split(",")[3]);
                    dataResult=userService.findpassword(user);
                    try{
                        Assert.assertEquals(dataResult+"",data.split(",")[4]);
                        dataList.add(data+","+dataResult+","+"yes");
                    }catch (Exception e){
                        System.out.println(e);
                        dataList.add(data+","+dataResult+","+"no");
                    }
                }
            }

        }
        CsvUtil.writeToCsv(headDataStr, dataList, csvfile, false);


    }
}
