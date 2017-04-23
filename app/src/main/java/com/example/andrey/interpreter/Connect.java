package com.example.andrey.interpreter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Принимет строку в виде url - requestURL
 * Возвращает строку ответа с сервера
 */

public class Connect {

    public static String getFile(String requestURL) {

        try {
            URL url = new URL(requestURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            int rc = httpURLConnection.getResponseCode();

            if (rc == 200) {
                String line = null;
                StringBuilder resultFile = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(httpURLConnection.getInputStream())
                );

                while ((line = bufferedReader.readLine()) != null) {
                    resultFile.append(line + '\n');
                }
                return resultFile.toString();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "No connect";
    }
}