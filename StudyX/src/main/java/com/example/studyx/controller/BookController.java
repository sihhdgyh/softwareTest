package com.example.studyx.controller;

import com.example.studyx.dao.BookDAO;
import com.example.studyx.dao.UserDAO;
import com.example.studyx.dao.CategoryDAO;
import com.example.studyx.pojo.Book;
import com.example.studyx.pojo.User;
import com.example.studyx.pojo.Category;
import com.example.studyx.result.Result;
import com.example.studyx.service.BookService;
import com.example.studyx.service.UserService;
import com.example.studyx.result.ResultFactory;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;

@RestController
public class BookController {
    @Autowired
    private CategoryDAO categoryDAO;

    @Autowired
    BookService bookService;

    @Autowired
    BookDAO bookDAO;

    //需要写三个接口
    //1、getUserid//
    //1、getBookid//借阅
    @CrossOrigin
    @PostMapping("/api/getbookid")
    public Result getbookid(@RequestBody String isbn){

        List<Book> book=bookDAO.findByIsbn(isbn);

        //System.out.println("111"+isbn);
        //根据书名显示所有内容
        //bookDAO.findAllByBooknameLikeOrAuthorLike(booktest.getBookname(),author);
        if (null!=book){
            System.out.println("书籍搜索成功");
            return new Result(200,"书籍搜索成功",book);
        }
        else
            return new Result(400,"未搜索到书籍",book);
    }

    //1、getIsbn//收藏
    @CrossOrigin
    @GetMapping("/api/search/byauthor")
    public Result searchbyauthor(@RequestParam String author){

       
        //String bookauthor=book.getAuthor();

        //List<Category> ret=new ArrayList<>();
        List<Category> booktest=categoryDAO.findByAuthor(author);

        //根据书名显示所有内容
        //bookDAO.findAllByBooknameLikeOrAuthorLike(booktest.getBookname(),author);
        if (null!=booktest){

            return new Result(200,"书籍搜索成功",booktest);
        }
        else
            return new Result(400,"未搜索到书籍",booktest);
        //bookService.searchByIsbn(book.getIsbn());
    }

    @CrossOrigin
    @GetMapping("/api/search/byname")
    public Result searchbyname(@RequestParam String bookname){

        

        List<Category> booktest=categoryDAO.findByBookname(bookname);

        //根据书名显示所有内容
        //bookDAO.findAllByBooknameLikeOrAuthorLike(booktest.getBookname(),author);
        if (null!=booktest)
            return new Result(200,"书籍搜索成功",booktest);
        else
            return new Result(200,"未搜索到书籍",booktest);
    }

    //@CrossOrigin
    //@GetMapping("/api/books")
    /*public Result listBooks() {
        System.out.println("页面刷新，显示全部书籍");
        return new Result(200,"书籍显示成功",categoryDAO.findByAuthor("齐邦媛"));
    }*/

    @CrossOrigin
    @GetMapping("/api/books")
    public Result listBooks() {
        return ResultFactory.buildSuccessResult(bookService.list());
    }
    /*@GetMapping("/api/books")
    public List<Book> list(@RequestParam String author) {

    }*/

    @CrossOrigin
    @GetMapping("/api/search")
    public Result searchResult(@RequestParam("keywords") String keywords) {
        return ResultFactory.buildSuccessResult(bookService.Search(keywords));
    }

    @CrossOrigin
    @GetMapping("/api/type")
    public Result listBooksByType(@RequestParam int cid) {
        if(cid==0)
            return ResultFactory.buildSuccessResult(bookService.list());
        else if(cid==1)
            return ResultFactory.buildSuccessResult(bookService.listbytype("小说"));
        else if(cid==2)
            return ResultFactory.buildSuccessResult(bookService.listbytype("社会学"));
        else if(cid==3)
            return ResultFactory.buildSuccessResult(bookService.listbytype("心理学"));
        else if(cid==4)
            return ResultFactory.buildSuccessResult(bookService.listbytype("诗歌"));
        else if(cid==5)
            return ResultFactory.buildSuccessResult(bookService.listbytype("童话"));
        else if(cid==6)
            return ResultFactory.buildSuccessResult(bookService.listbytype("传记"));
        return null;
    }
    @CrossOrigin
    @GetMapping("/api/detail")
    public Result listDetail(@RequestParam String bookname) {
       //怎样带着信息跳转到新页面？
        return ResultFactory.buildSuccessResult(bookService.Search(bookname));
    }

    @CrossOrigin
    @PostMapping(value = "/api/user/getbookdetail")
    public Category getbookdetail(@RequestBody String bookid) {
        return bookService.getBookid(bookid);
    }
}
