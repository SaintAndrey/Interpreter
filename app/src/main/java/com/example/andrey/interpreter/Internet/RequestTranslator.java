package com.example.andrey.interpreter.Internet;

import android.os.AsyncTask;

import com.example.andrey.interpreter.Parser.Parser;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Отправка запроса на сервер Яндекс.Переводчик для получения перевода текста
 */

public class RequestTranslator extends AsyncTask<String, Void, String> {

    private final static String URL =
            "https://translate.yandex.net/api/v1.5/tr.json/translate?key=";
    private final static String KEY =
            "trnsl.1.1.20170326T200601Z.c4375d143950de0a.1d980a245aff85e27afa1f73106b2800b4dad8c6";

    @Override
    protected String doInBackground(String... params) {
        try {

            // Формирование строки запроса
            String requestURL =
                    URL
                    + KEY
                    + "&lang="
                    + params[0]
                    + "&text="
                    + URLEncoder.encode(params[1], "UTF-8");

            // Отправка запроса
            String JSON = Connect.getFile(requestURL);

            // Парсинг файла
            Parser<String> parser = new Parser<String>() {
                @Override
                public String doParse(String file) {
                    if (file == "No connect") return "No Internet";
                    try {
                        JSONObject data = new JSONObject(file);
                        JSONArray text = data.getJSONArray("text");
                        return text.getString(0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return "No parse";
                }
            };
            return URLDecoder.decode(parser.doParse(JSON), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.getStackTrace();
        }

        return "No file";
    }

}
