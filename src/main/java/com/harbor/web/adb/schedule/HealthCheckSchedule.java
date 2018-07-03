package com.harbor.web.adb.schedule;

import com.harbor.web.adb.service.ADBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
             Runtime rt = Runtime.getRuntime();
             try {
                 Process process = rt.exec("taskkill /IM adb.exe /F");
                 BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
                 String line = null;
                 while ((line = input.readLine()) != null) {
                     System.out.println(line);
                 }
                 process = rt.exec("adb start-server");
                 input = new BufferedReader(new InputStreamReader(process.getInputStream()));
                 while ((line = input.readLine()) != null) {
                     System.out.println(line);
                 }
             } catch (IOException e1) {
                 e1.printStackTrace();
             }
         }
    }
}
