package com.harbor.web.adb.schedule;

import com.harbor.web.adb.service.ADBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by harbor on 7/3/2018.
 */
@Component
public class HealthCheckSchedule {

    @Autowired
    ADBService service;

    @Scheduled(fixedRate = 2000)
    public void autoCheckAndroidDebugService(){
         try{
             service.getConnectedDevice();
         }catch (Exception e){

         }
    }
}
