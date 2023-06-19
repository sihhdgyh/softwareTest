package com.example.studyx.interfact;

import com.example.studyx.dao.AdminDAO;
import com.example.studyx.dao.UserDAO;
import com.example.studyx.pojo.Admin;
import com.example.studyx.pojo.User;
import com.example.studyx.service.AdminService;
import com.example.studyx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginRegister {
    @Autowired
    UserService userService;

    @Autowired
    AdminService adminService;

    @Autowired
    AdminDAO adminDAO;

    @Autowired
    UserDAO userDAO;

    public Admin findByAdminname(String adminname){
        return adminDAO.findByAdminname(adminname);
    }

    public Admin getByAdminnameAndPassword(String adminname,String password){
        return adminDAO.getByAdminnameAndPassword(adminname,password);
    }

    public User findByMail(String mail){
        return userDAO.findByMail(mail);
    }

    public int register(User user) {
        return userService.register(user);
    }
    public int findpassword(User user) {
        return userService.findpassword(user);
    }

    public User get(String mail,String password) {
        return userService.get(mail,password);
    }
    public int register(Admin admin) {
        return adminService.register(admin);
    }
}
