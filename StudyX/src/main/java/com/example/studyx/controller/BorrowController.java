package com.example.studyx.controller;


import com.example.studyx.dao.BookDAO;
import com.example.studyx.dao.BorrowDAO;
import com.example.studyx.dao.UserDAO;
import com.example.studyx.pojo.*;
import com.example.studyx.service.BookService;
import com.example.studyx.utils.GetNowTime;
import com.example.studyx.utils.MyGlobal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class BorrowController {
    @Autowired
    BookService bookService;



    @CrossOrigin
    @PostMapping(value = "/api/user/findallborrow")
    public List<Borrow> findallborrow(@RequestBody String id) {

        return bookService.findallborrow(id);
    }

    @CrossOrigin
    @GetMapping ("/api/user/countborrow")
    public Integer[] countborrow(@RequestParam String id){


        return bookService.countborrow(id);
    }



    @CrossOrigin
    @PostMapping(value = "/api/user/setborrow")
    public Borrow setborrow(@RequestBody Borrow borrow) {
        return bookService.setborrow(borrow);
    }

    @CrossOrigin
    @PostMapping(value = "/api/changeborrow")
    public void changeborrow(@RequestBody Map<String, String> datas) {
        bookService.changeborrow(datas);
    }
}
