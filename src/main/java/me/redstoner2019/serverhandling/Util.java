package me.redstoner2019.serverhandling;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.redstoner2019.main.data.guis.ConsoleGUI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Date;

public class Util {
    public static void log(String message){
        System.out.println(message);
        ConsoleGUI.area.setText(new Date(System.currentTimeMillis()).toGMTString() + ": " + message + "\n" + ConsoleGUI.area.getText());
    }

    public static void writeStringToFile(String str, File file) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(file);
        byte[] strToBytes = str.getBytes();
        outputStream.write(strToBytes);

        outputStream.close();
    }
    public static String readFile(File path) throws IOException {
        byte[] encoded = Files.readAllBytes(path.toPath());
        return new String(encoded, Charset.defaultCharset());
    }

    public static String prettyJSON(String uglyJsonString) {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Object jsonObject = objectMapper.readValue(uglyJsonString, Object.class);
            String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
            return prettyJson;
        }catch (Exception e){
            return null;
        }
    }
}