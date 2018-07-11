package com.harbor.web.adb.controller;

import com.harbor.web.adb.service.ADBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by harbor on 7/5/2018.
 */
@RestController
@RequestMapping("/app")
public class AndroidAppRestController {

    @Autowired
    ADBService service;


    /**
     * adb shell dumpsys package | grep -E "com.m37|dkmodel" | grep -i activity
     * @return
     */
    @GetMapping("/game/list")
    public List<Map<String,String>> listGames(){

        List<Map<String,String>> list = new ArrayList<>();

        Map<String,String> app = new HashMap<>();
        app.put("label","boss");
        app.put("name","com.m37.dtszjwd.sy37/demo.MainActivity");
        list.add(app);

        app = new HashMap<>();
        app.put("label","Arsenal");
        app.put("name","dkmodel.cxo.cxc/com.bly.dkplat.PluginFirstRunActivity");
        list.add(app);

        app = new HashMap<>();
        app.put("label","Buddy");
        app.put("name","dkmodel.nqw.qyd/com.bly.dkplat.PluginFirstRunActivity");
        list.add(app);

        app = new HashMap<>();
        app.put("label","Barcelona");
        app.put("name","dkmodel.kdv.xow/com.bly.dkplat.PluginFirstRunActivity");
        list.add(app);

        app = new HashMap<>();
        app.put("label","Juventus");
        app.put("name","dkmodel.yem.pmh/com.bly.dkplat.PluginFirstRunActivity");
        list.add(app);

        return list;
    }

    /**
     * @param appName
     * @return
     */
    @PostMapping("/game/start")
    public Map<String,String> startGameClone(@RequestParam(name = "appName") String appName){
        Map<String,String> result = new HashMap<>();
        result.put("success","true");
        result.put("message","App start "+appName+" OK");

        service.executeShell("am","start", "-n", appName);

        return result;
    }
}
