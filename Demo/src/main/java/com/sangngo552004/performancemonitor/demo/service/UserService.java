package com.sangngo552004.performancemonitor.demo.service;

import com.sangngo552004.performancemonitor.annotation.MonitorPerformance;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@MonitorPerformance(metricName = "user_management_service")
public class UserService {
    // Phương thức thành công
    public String processUserRequest(String userId) throws InterruptedException {
        // Mô phỏng độ trễ xử lý
        TimeUnit.MILLISECONDS.sleep(50 + (long) (Math.random() * 50));
        return "Processed: " + userId;
    }

    // Phương thức thất bại (Sử dụng Metrics Fallback)
    // Đánh dấu riêng để tùy chỉnh tên Metric cụ thể hơn
    @MonitorPerformance(metricName = "user_creation.critical")
    public void createUser(String username) {
        if (username.length() < 5) {
            // Tình huống lỗi: sẽ kích hoạt Metrics Fallback (Counter tăng lên)
            throw new IllegalArgumentException("Username quá ngắn.");
        }
        System.out.println("User created: " + username);
    }
}
