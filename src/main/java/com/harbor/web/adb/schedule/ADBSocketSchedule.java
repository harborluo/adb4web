package com.harbor.web.adb.schedule;

import com.harbor.web.adb.service.ADBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by harbor on 7/20/2018.
 */
@Component
public class ADBSocketSchedule {

    @Autowired
    ADBService service;

    @Value("${android.screen.shot.dir}")
    private String screenShotDir;

    private Queue<String> imageFileQueue = new LinkedList<String>();

    @Scheduled(cron = "0/5 * * * * *")
    public void autoCaptureScreen(){
        String imageFile = service.captureScreen();
        if(imageFile==null){
            return;
        }
        imageFileQueue.add(imageFile);
    }

    @Scheduled(cron = "0/2 * * * * *")
    public void refreshConnectedDevice(){

        try {
            service.getConnectedDevice();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Scheduled(cron = "0/30 * * * * *")
    public void cleanScreenShot(){

        int MAX_SIZE = 20;

        if(this.imageFileQueue.size()<MAX_SIZE){
            return;
        }

        while(imageFileQueue.size()>MAX_SIZE){
            String file = imageFileQueue.poll();
            new File(screenShotDir+"/"+file).delete();
        }

    }
}
