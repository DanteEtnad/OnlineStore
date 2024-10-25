package org.example.controller;

import org.example.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/notifications")
    public List<String> getNotifications() {
        return emailService.getNotifications();
    }

    @GetMapping("/notifications/{customerId}")
    public SseEmitter streamNotificationsForCustomer(@PathVariable Long customerId) {
        SseEmitter emitter = new SseEmitter(300000L); // 5分钟超时

        new Thread(() -> {
            try {
                List<String> lastNotifications = new ArrayList<>(); // 初始化为空
                long lastUpdateTime = System.currentTimeMillis();

                while (true) {
                    // 获取当前通知
                    List<String> currentNotifications = emailService.getNotificationsForCustomer(customerId);

                    // 获取增量通知
                    List<String> newNotifications = getNewNotifications(lastNotifications, currentNotifications);

                    // 如果有新增通知，发送
                    if (!newNotifications.isEmpty()) {
                        emitter.send(newNotifications);
                        lastNotifications = new ArrayList<>(currentNotifications);
                        lastUpdateTime = System.currentTimeMillis();
                    }

                    // 每隔5秒检查一次
                    Thread.sleep(5000);

                    // 超过5分钟没有变化，关闭连接
                    if (System.currentTimeMillis() - lastUpdateTime > 300000) {
                        emitter.complete();
                        break;
                    }
                }
            } catch (IOException | InterruptedException e) {
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }

    // 返回新增的通知
    private List<String> getNewNotifications(List<String> lastNotifications, List<String> currentNotifications) {
        if (lastNotifications == null || lastNotifications.isEmpty()) {
            return new ArrayList<>(currentNotifications); // 如果之前为空，返回所有当前通知
        }
        // 找出新增的通知
        List<String> newNotifications = new ArrayList<>();
        for (int i = lastNotifications.size(); i < currentNotifications.size(); i++) {
            newNotifications.add(currentNotifications.get(i));
        }
        return newNotifications;
    }

}
