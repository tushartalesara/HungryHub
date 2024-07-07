package com.dev.controllers;

import com.dev.dao.UserJdbcDao;
import com.dev.models.User;
import com.dev.services.EmailService;
import com.dev.services.KeyGenerator;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;

import java.util.Random;

import static java.lang.Math.random;
// import java.util.List;

@Controller
public class UserController {

    private UserJdbcDao userJdbcDao;
    private EmailService emailService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserJdbcDao userJdbcDao, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userJdbcDao = userJdbcDao;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String registerUser(Model model) {
        model.addAttribute("user", new User());
        return "site/register";
    }

    @RequestMapping("/users")
    public String getAllUsers(Model model) {
        model.addAttribute("users", userJdbcDao.getAllUsers());
        return "users";
    }

    @RequestMapping("/users/{id}")
    public String getUserById(@PathVariable("id") long id, Model model) {
        model.addAttribute("user", userJdbcDao.getUserById(id));
        return "user";
    }

    @GetMapping(value = {"/signin","/login"})
    public String login() {
        return "site/login";
    }

    @RequestMapping("/forgotpassword")
    public String forgotPassword(Model model) {
        return "site/forgotpassword";
    }

    @PostMapping("/send_otp/")
    @ResponseBody
    public String send_otp(@RequestParam("email") String email, HttpSession session) {
        User user = userJdbcDao.getUserByUsername(email);
        if (user == null) return "No user found with this email";
        long otp = KeyGenerator.generateKey()%899999+100000;
        session.setAttribute("otp", otp);
        session.setAttribute("email", email);
        System.out.println("otp="+otp);
        Context context = new Context();
        context.setVariable("otp", otp);
        context.setVariable("date", new java.util.Date());
        context.setVariable("name", user.getFirstName());
        context.setVariable("text", "Your OTP for password reset is:");
        if (emailService.sendEmail(email, "OTP for password reset", "site/otp", context) == 0) return "success";
        return "error";
    }

    @PostMapping("/change_password")
    @ResponseBody
    public String change_password(@RequestParam("otp") long otp, @RequestParam("np") String password, HttpSession session) {
        if (otp == (long) session.getAttribute("otp")) {
            User user = userJdbcDao.getUserByUsername((String) session.getAttribute("email"));
            user.setUserPassword(password);
            userJdbcDao.updateUser(user);
            return "success";
        }
        return "error";
    }

    @PostMapping("/register/email")
    @ResponseBody
    public String registerUserEmail(@RequestParam("email") String email, HttpSession session) {
        User user = userJdbcDao.getUserByUsername(email);
        if (user != null) return "User already exists";
        long otp = KeyGenerator.generateKey()%899999+100000;
        session.setAttribute("otp", otp);
        session.setAttribute("email", email);
        System.out.println("otp="+otp);
        Context context = new Context();
        context.setVariable("otp", otp);
        context.setVariable("date", new java.util.Date());
        context.setVariable("name", "Foodie");
        context.setVariable("text", "Your OTP for registration is:");
        if (emailService.sendEmail(email, "OTP for registration", "site/otp", context) == 0) return "success";
        return "error";
    }

    @PostMapping("/register")
    @ResponseBody
    public String registerUser(@RequestParam("otp") long otp, @RequestParam("np") String password, @RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName, @RequestParam("phone") long phoneNumber, HttpSession session) {
        if (otp == (long) session.getAttribute("otp")) {
            try{
                User user = new User();
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setPhoneNumber(phoneNumber);
                user.setEmail((String) session.getAttribute("email"));
                user.setUserPassword(passwordEncoder.encode(password));
                userJdbcDao.createUser(user);
                return "success";
            }
            catch (Exception e){
                return "error";
            }
        }
        else if (otp != (long) session.getAttribute("otp")) return "Invalid OTP";
        return "error";

    }
}
