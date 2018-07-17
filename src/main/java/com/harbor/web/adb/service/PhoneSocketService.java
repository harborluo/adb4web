package com.harbor.web.adb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by harbor on 7/13/2018.
 */
@Service
public class PhoneSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

//    @SendTo("/phoneSend/captureScreen")
    public void sendCapturedImage(String imageName){
        PhoneSocketMessage<String> message = new PhoneSocketMessage();
        message.setData(imageName);
        messagingTemplate.convertAndSend("/topic/captureScreen",message);
    }


    public void sendConnectedDevice(List<PhoneDevice> devices){
        PhoneSocketMessage<List<PhoneDevice>> message = new PhoneSocketMessage();
        message.setData(devices);
        messagingTemplate.convertAndSend("/topic/devices/list",message);
    }
}
