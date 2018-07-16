package com.harbor.web.adb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by harbor on 7/13/2018.
 */
@Service
public class PhoneSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

//    @SendTo("/phoneSend/captureScreen")
    public void sendCapturedImage(String imageName){
        PhoneSocketMessage message = new PhoneSocketMessage();
        message.setImage(imageName);
        messagingTemplate.convertAndSend("/topic/captureScreen",message);
    }


}
