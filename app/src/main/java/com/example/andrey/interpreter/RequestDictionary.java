package com.example.andrey.interpreter;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Отправка запроса на сервер Яндекс.Словарь
 */

public class RequestDictionary extends AsyncTask<String, Void, List<Dictionary>> {
    private final static String URL =
            "https://dictionary.yandex.net/api/v1/dicservice.json/lookup?key=";
    private final static String KEY =
            "dict.1.1.20170327T063331Z.90b7d084561f15d4.0d76809120837baf0fdc1fbcd1be41bb49c126e0";

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
        String JSON = Connect.getFile(requestURL);

        // Парсинг файла и заполнение результата в тип List<Dictionary>
        Parser<List<Dictionary>> parser = new Parser<List<Dictionary>>() {
                @Override
                public List<Dictionary> doParse(String file) {
                    List<Dictionary> dictionaries = new ArrayList<>();
                    JSONObject data = null;
                    try {
                        data = new JSONObject(file);
                        JSONArray text = data.getJSONArray("def");
                        for (int n = 0; n < text.length(); n++) {

                            JSONObject res = text.getJSONObject(n);
                            JSONArray arrayTr = res.getJSONArray("tr");
                            for (int i = 0; i < arrayTr.length(); i++) {
                                Dictionary dictionary = new Dictionary();
                                JSONObject result = arrayTr.getJSONObject(i);
                                dictionary.addTranslatedText(result.getString("text"));
                                Log.d("dict", "Text: " + result.getString("text"));
                                try {
                                    JSONArray syn = result.getJSONArray("syn");
                                    for (int j = 0; j < syn.length(); j++) {
                                        JSONObject resSyn = syn.getJSONObject(j);
                                        dictionary.addTranslatedText(resSyn.getString("text"));
                                        Log.d("dict", "Text: " + resSyn.getString("text"));
                                    }
                                } catch (JSONException e) {
                                }

                                try {
                                    JSONArray mean = result.getJSONArray("mean");
                                    for (int j = 0; j < mean.length(); j++) {
                                        JSONObject resMean = mean.getJSONObject(j);
                                        dictionary.addMeanText(resMean.getString("text"));
                                        Log.d("dict", "Mean: " + resMean.getString("text"));
                                    }
                                } catch (JSONException e) {
                                }

                                try {
                                    JSONArray ex = result.getJSONArray("ex");
                                    for (int j = 0; j < ex.length(); j++) {
                                        JSONObject resEx = ex.getJSONObject(j);
                                        String exampleKey = resEx.getString("text");
                                        JSONArray exTr = resEx.getJSONArray("tr");
                                        for (int k = 0; k < exTr.length(); k++) {
                                            JSONObject exTrString = exTr.getJSONObject(k);
                                            dictionary
                                                    .addExamples(exampleKey, exTrString.getString("text"));
                                            Log.d("dict", "Examples: " + exampleKey + "-" + exTrString.getString("text"));
                                        }
                                    }
                                } catch (JSONException e) {
                                }

                                dictionaries.add(dictionary);

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return dictionaries;
                }
            };

        return parser.doParse(JSON);
    }
}
