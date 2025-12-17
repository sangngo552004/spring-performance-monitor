package com.sangngo552004.performancemonitor.demo.controller;
import com.sangngo552004.performancemonitor.demo.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/process")
    public String handleSuccess(@RequestParam String id) throws InterruptedException {
        return userService.processUserRequest(id);
    }

    @GetMapping("/create")
    public String handleFailure(@RequestParam String name) {
        try {
            userService.createUser(name);
            return "Creation success!";
        } catch (Exception e) {
            // Log lỗi (đã được Proxy ghi lại Counter)
            return "Creation failed: " + e.getMessage();
        }
    }
}
