package com.harbor.web.adb.service;

import org.springframework.stereotype.Service;
import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;
import se.vidstige.jadb.RemoteFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by harbor on 6/29/2018.
 */
@Service
public class ADBService {

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

    private String lastScreenFile = null;

    public synchronized boolean captureScreen(String filePath){

        String imageFullPath = "/sdcard/screen_cap.png";
        JadbConnection connection = new JadbConnection();
        try {
            InputStream inputStream = connection.getAnyDevice().executeShell("screencap", "-p", imageFullPath);

            String log = inputStream2String(inputStream);

            RemoteFile remoteFile = new RemoteFile(imageFullPath);
            File localFile = new File(filePath);

            if(localFile.exists()==false){
                localFile.createNewFile();
            }

            OutputStream out = new FileOutputStream(localFile);
            connection.getAnyDevice().pull(remoteFile, out);

           out.flush();

           out.close();

           //remove capture screen file on phone
            connection.getAnyDevice().executeShell("rm", "-f", imageFullPath);
            return true;

        }catch(Exception e){
            return false;
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
