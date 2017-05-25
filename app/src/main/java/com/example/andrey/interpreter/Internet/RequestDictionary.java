package com.example.andrey.interpreter.Internet;

import android.os.AsyncTask;
import android.util.Log;

import com.example.andrey.interpreter.Parser.Parser;
import com.example.andrey.interpreter.Parser.ParserDictionary;
import com.example.andrey.interpreter.Structures.Dictionary;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Отправка запроса на сервер Яндекс.Словарь
 */

public class RequestDictionary extends AsyncTask<String, Void, List<Dictionary>> {
    private final static String URL =
            "https://dictionary.yandex.net/api/v1/dicservice.json/lookup?key=";
    private final static String KEY =
            "dict.1.1.20170327T063331Z.90b7d084561f15d4.0d76809120837baf0fdc1fbcd1be41bb49c126e0";
    private String JSON;

    @Override
    protected List<Dictionary> doInBackground(String... params) {

        // Формирование строки запроса
        String requestURL =
                null;
        try {
            requestURL = URL
            + KEY
            + "&lang="
            + params[0]
            + "&text="
            + URLEncoder.encode(params[1], "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // Получение JSON файла
        JSON = Connect.getFile(requestURL);

        Log.d("JSON", JSON);

        // Парсинг файла и заполнение результата в тип List<Dictionary>
        Parser<List<Dictionary>> parser = new ParserDictionary();

        return parser.doParse(JSON);
    }

    public String getJSON() {
        return JSON;
    }
}
