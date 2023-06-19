package com.example.studyx.service;

import com.example.studyx.dao.BookDAO;
import com.example.studyx.dao.BorrowDAO;
import com.example.studyx.dao.CategoryDAO;
import com.example.studyx.dao.UserDAO;
import com.example.studyx.pojo.Book;
import com.example.studyx.pojo.Borrow;
import com.example.studyx.pojo.Category;
import com.example.studyx.pojo.User;
import com.example.studyx.result.ResultFactory;
import com.example.studyx.utils.GetNowTime;
import com.example.studyx.utils.MyGlobal;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookService {
    @Autowired
    CategoryDAO categoryDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    BorrowDAO borrowDAO;

    @Autowired
    BookDAO bookDAO;

   /* public List<Category> list() {
        List<Category> books;
        String key = "booklist";
       // Object bookCache = redisService.get(key);

        if (bookCache == null) {
            Sort sort = new Sort(Sort.Direction.DESC, "id");
            books = bookDAO.findAll(sort);
            redisService.set(key, books);
        } else {
            books = CastUtils.objectConvertToList(bookCache, Book.class);
        }
        return books;
    }*/

    public List<Category> list() {
        //Sort sort = new Sort(Sort.Direction.DESC, "isbn");
        return categoryDAO.findAll();
    }

    public List<Category> listbytype(String type) {
        //Sort sort = new Sort(Sort.Direction.DESC, "isbn");
        return categoryDAO.findByType(type);
    }

    public List<Category> Search(String keywords) {
        if ("".equals(keywords)) {
            return categoryDAO.findAll();
        } else {
            return categoryDAO.findAllByBooknameLikeOrAuthorLike('%' + keywords + '%', '%' + keywords + '%');
        }
    }

    public List<Borrow> findallborrow(String id) {
        Integer userid = Integer.valueOf(id);
        User user = userDAO.getById(userid);
        user=userDAO.getById(MyGlobal.getUserid());
        List<Borrow> borrows = borrowDAO.findByUserid(MyGlobal.getUserid());
        LocalDate nowTime = LocalDate.now();
        for (Borrow borrow : borrows) {
            if (borrow.getStatus().equals("借阅中")) {
                LocalDate tmptime = LocalDate.parse(borrow.getStarttime());
                int dayy = (int) (nowTime.toEpochDay() - tmptime.toEpochDay());
                int a = 30 - dayy;
                if (a < 0 && user.getStatus().equals("normal")) {
                    user.setIntegration(String.valueOf(Integer.valueOf(user.getIntegration()) - 20));
                    if (Integer.valueOf(user.getIntegration()) < 0) {
                        user.setStatus("banned");
                    }
                    userDAO.save(user);
                }
                borrow.setDuring(String.valueOf(a) + "天");//更新during日期
            }
        }
        return borrows;
    }

    public Integer[] countborrow(String id){
        Integer userid = Integer.parseInt(id);
//        User user = userDAO.getById(userid);
        //find all books borrowed by a certain person
        List<Borrow> borrows = borrowDAO.findByUserid(MyGlobal.getUserid());
//        List<Integer> l=new ArrayList<>();
        Integer[] ret_value=new Integer[12];
        for(int i=0;i<12;i++) ret_value[i]=0;
        Map<String,Integer> ret=new HashMap<>();
        for(Borrow borrow:borrows){
            System.out.println(borrow.getStarttime());
            String[] date=borrow.getStarttime().split("-");
            System.out.println(date.length);
            System.out.println(date[0]);
            System.out.println(date[1]);
            System.out.println(date[2]);
            ret_value[Integer.parseInt(date[1])]+=1;
            //ret.put(date[1],ret.get(Integer.toString(Integer.parseInt(date[1])+1)));
        }

        return ret_value;

//        List<Integer> returnvalue=new ArrayList<>();
//        int num=0;
//        for(Integer value:ret.values()){
//            returnvalue.set(num++, value);
//        }
//        return returnvalue;
    }

    public Borrow setborrow(Borrow borrow) {
        try {
            String createTime = GetNowTime.gettime().toString();//得到当前时间
            if (borrow.getStatus().equals("申请中")) {
                Integer userid = borrow.getUserid();
                User user = userDAO.getById(userid);
                user=userDAO.getById(MyGlobal.getUserid());
                if (user.getStatus().equals("banned")||(Integer.valueOf(user.getIntegration())<20))//被封禁或者积分小于20不可借
                    return null;
                borrow.setStarttime("---");
                borrow.setReturntime("---");
                borrow.setDuring("---");
            }
            borrowDAO.save(borrow);
            return borrow;
        } catch (Exception e) {
            return null;
        }
    }

    public void changeborrow(Map<String, String> datas) {
        String createTime = GetNowTime.gettime().toString();//得到当前时间
        Integer userid = Integer.valueOf(datas.get("userid").toString());
        Integer bookid = Integer.valueOf(datas.get("bookid").toString());
        String status = datas.get("status").toString();
        Borrow borrow = borrowDAO.findByUseridAndBookid(MyGlobal.getUserid(), bookid);
        Book book=bookDAO.findByBookid(bookid);
        if (status.equals("借阅中")) {
            borrow.setStarttime(createTime);
            borrow.setReturntime("---");//设置归还日期
            borrow.setDuring("30天");//设置为一个月
            book.setBorrowstatus("不可借");
            bookDAO.save(book);
        } else if (status.equals("已结束")) {
            borrow.setReturntime(createTime);//设置归还日期
            borrow.setDuring("---");//设置为一个月
            book.setBorrowstatus("可借");
            bookDAO.save(book);
        } else if (status.equals("未借阅")) {
            borrow.setReturntime("---");//设置归还日期
            borrow.setStarttime("---");//设置归还日期
            borrow.setDuring("---");//设置为一个月
        }
        borrow.setStatus(status);
        borrowDAO.save(borrow);
    }

    public Category getBookid(String bookid) {
        try {
            Category book=categoryDAO.findByIsbn(bookid);
            System.out.println(book);
            return book;
        }catch (Exception e){
            System.out.println(e);
            return null;
        }
    }
}
