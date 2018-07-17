package com.harbor.web.adb.service;

import net.sourceforge.tess4j.util.LoggHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;
import se.vidstige.jadb.RemoteFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by harbor on 6/29/2018.
 */
@Service
public class ADBService {

    @Value("${android.screen.shot.dir}")
    private String screenShotDir;

    @Autowired
    PhoneSocketService phoneSocketService;

    private Queue<String> imageFileQueue = new LinkedList<String>();

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public List<PhoneDevice> getConnectedDevice() throws Exception{

        JadbConnection connection = new JadbConnection();

        List<JadbDevice> devices = connection.getDevices();

        List<PhoneDevice> phoneDeviceList = new ArrayList<>();

        for(JadbDevice device: devices){
            PhoneDevice phoneDevice = new PhoneDevice();
            phoneDevice.setSerialNo(device.getSerial());
            InputStream in = device.executeShell("wm","size");
            String log = inputStream2String(in);

            String info[] = log.replaceAll("^Physical size: |\\n$","").split("x");

            phoneDevice.setScreenWidth(Integer.parseInt(info[0]));
            phoneDevice.setScreenHeight(Integer.parseInt(info[1]));

            in.close();

            phoneDeviceList.add(phoneDevice);


        }
        return phoneDeviceList;
    }

//    private String lastScreenFile = null;

    @Scheduled(fixedRate = 5000)
    public synchronized void captureScreen(){

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
        String file = "capture_screen_"+dateFormat.format(new Date())+".png";

        String imageFullPath = "/sdcard/screen_cap.png";
        JadbConnection connection = new JadbConnection();
        try {

            if(connection.getAnyDevice()==null){
                return;
            }

            InputStream inputStream = connection.getAnyDevice().executeShell("screencap", "-p", imageFullPath);

            String log = inputStream2String(inputStream);

            RemoteFile remoteFile = new RemoteFile(imageFullPath);
            File localFile = new File(screenShotDir + "/" + file);

            if(localFile.exists()==false){
                localFile.createNewFile();
            }

            OutputStream out = new FileOutputStream(localFile);
            connection.getAnyDevice().pull(remoteFile, out);

           out.flush();

           out.close();

           //remove capture screen file on phone
            connection.getAnyDevice().executeShell("rm", "-f", imageFullPath);

            phoneSocketService.sendCapturedImage(file);

            imageFileQueue.add(file);

        }catch(Exception e){

        }
    }

    @Scheduled(cron = "0/2 * * * * *")
    public void cleanScreenShot(){

        int MAX_SIZE = 3;

        if(this.imageFileQueue.size()<MAX_SIZE){
            return;
        }

        while(imageFileQueue.size()>MAX_SIZE){
            String file = imageFileQueue.poll();
            new File(screenShotDir+"/"+file).delete();
        }

    }

    public boolean tapScreen(String x, String y){
        JadbConnection connection = new JadbConnection();
        try {
            connection.getAnyDevice().executeShell("input", "tap", x, y);
            return true;
        } catch (IOException e) {
            return false;
        } catch (JadbException e) {
            return false;
        }
    }

    /**
     * start -n com.m37.dtszjwd.sy37/MainActivity
     * @param command
     * @param args
     */
    public void executeShell(String command, String... args){
        JadbConnection connection = new JadbConnection();
        try {
            InputStream inputStream = connection.getAnyDevice().executeShell(command, args);
            String log = inputStream2String(inputStream);
            logger.info("Execute shell command {}, logs is {}", command, log);
        } catch (IOException e) {
            logger.error("IOException: ", e);
        } catch (JadbException e) {
            logger.error("JadbException: ", e);
        }catch (Exception e){
            logger.error("Exception: ", e);
        }

    }

    private String inputStream2String(InputStream inputStream) throws Exception{
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        String log = result.toString(StandardCharsets.UTF_8.name());

        return log;
    }

}
