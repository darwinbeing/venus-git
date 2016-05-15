package com.venus.utils;

import lombok.Cleanup;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by erix-mac on 15/12/13.
 */
public class URLUtils {

    public static String extractURLConext(String webURL){
        return extractURLConext(webURL,"UTF-8");
    }

    @SneakyThrows(IOException.class)
    public static String extractURLConext(String webURL, String charset){
        URL url = new URL(webURL);
        URLConnection con = url.openConnection();

        @Cleanup
        InputStreamReader ins = new InputStreamReader(con.getInputStream(), charset);
        @Cleanup
        BufferedReader in = new BufferedReader(ins);

        StringBuilder content = new StringBuilder();
        String newLine = "";
        while ((newLine = in.readLine()) != null) {
            content.append(newLine);
        }

        return content.toString();
    }
}
