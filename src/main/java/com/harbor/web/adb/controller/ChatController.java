package com.harbor.web.adb.controller;

import com.harbor.web.adb.SocketMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by harbor on 6/29/2018.
 */
@Controller
@EnableScheduling
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/chat")
    public String index() {
        return "chat";
    }

    @MessageMapping("/send")
    @SendTo("/topic/send")
    public SocketMessage send(SocketMessage socketMessage) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        socketMessage.date = df.format(new Date());
        socketMessage.message = "Server: " + socketMessage.message;
        return socketMessage;
    }

    @Scheduled(fixedRate = 1000)
//    @SendTo("/topic/callback")
    public void callback() throws Exception {
        // 发现消息
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        messagingTemplate.convertAndSend("/topic/callback", df.format(new Date()));
//        return "callback";
    }

}
