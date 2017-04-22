package com.example.andrey.interpreter;

import android.os.AsyncTask;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Отправка запроса на сервер Яндекс.Переводчик для получения поддерживаемых языков
 */

public class RequestLangs extends AsyncTask<Void, Void, Map<String, String>> {

    private final static String URL = "https://translate.yandex.net/api/v1.5/tr.json/getLangs?key=";
    private final static String KEY =
            "trnsl.1.1.20170326T200601Z.c4375d143950de0a.1d980a245aff85e27afa1f73106b2800b4dad8c6";

    @Override
    protected Map<String, String> doInBackground(Void... params) {

        // Отправка запроса
        String JSON = Connect.getFile(URL + KEY + "&ui=ru");

        // Парсинг файла
        Parser<Map<String,String>> parser = new Parser<Map<String, String>>() {
            @Override
            public Map<String, String> doParse(String file) {
                Map<String, String> mapLangs = new HashMap<>();
                try {
                    JSONObject data = new JSONObject(file);
                    JSONObject resLangs = data.getJSONObject("langs");

                    Iterator<String> iteratorLangs = resLangs.keys();
                    while (iteratorLangs.hasNext()) {
                        String key = iteratorLangs.next();
                        mapLangs.put(resLangs.getString(key), key);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return mapLangs;
            }
        };

        return parser.doParse(JSON);
    }
}
