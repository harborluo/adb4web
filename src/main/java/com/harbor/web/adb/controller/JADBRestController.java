package com.harbor.web.adb.controller;

import com.harbor.web.adb.service.ADBService;
import com.harbor.web.adb.service.PhoneDevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.*;
import se.vidstige.jadb.JadbDevice;

import javax.annotation.Resource;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by harbor on 6/29/2018.
 */
@RestController
@RequestMapping("/devices")
public class JADBRestController {

    @Value("${android.screen.shot.dir}")
    private String screenShotDir;

    @Autowired
    ADBService adbService;

    @GetMapping("/list")
    public List<PhoneDevice> getAllDevices(){
        try {

            File dir = new File(screenShotDir);
            if(dir.exists()==false){
                dir.mkdir();
            }

            return adbService.getConnectedDevice();
        }catch(Exception e){
            return null;
        }
    }

    @GetMapping("/capture/screen/{serialNumber}")
    public Map<String,String> captureScreen(@PathVariable String serialNumber){
        Map<String,String> result = new HashMap<>();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");

        String file = "capture_screen_"+dateFormat.format(new Date())+".png";

        boolean success = adbService.captureScreen(screenShotDir + "/" + file);

        result.put("success",success + "");
        result.put("imagePath",success ? file : "");
        return result;

    }

    @GetMapping("/tap/screen/{serialNumber}/{x}/{y}")
    public Map<String,String> tapScreen(@PathVariable String serialNumber,
                                        @PathVariable String x,
                                        @PathVariable String y){

        adbService.tapScreen(x,y);
        try{
            Thread.sleep(2000);
        }catch(Exception e){
            e.printStackTrace();
        }

        return captureScreen(serialNumber);
    }

}
