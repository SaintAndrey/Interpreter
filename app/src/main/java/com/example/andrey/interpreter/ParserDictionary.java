package com.example.andrey.interpreter;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Отдельная реализация для парсинга запроса от сервера Яндекс.Словарь
 */

public class ParserDictionary implements Parser<List<Dictionary>>{
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

                    //получения из json файла массива результата
                    JSONObject result = arrayTr.getJSONObject(i);

                    //добавление переведенного текста из строки - text
                    dictionary.addTranslatedText(result.getString("text"));
                    try {

                        //получения массива значений из строки - syn
                        JSONArray syn = result.getJSONArray("syn");
                        for (int j = 0; j < syn.length(); j++) {
                            JSONObject resSyn = syn.getJSONObject(j);

                            //добавление всех элементов-синонимов
                            dictionary.addTranslatedText(resSyn.getString("text"));
                        }
                    } catch (JSONException e) {
                    }

                    try {
                        //получение массива значений из строки - mean
                        JSONArray mean = result.getJSONArray("mean");
                        for (int j = 0; j < mean.length(); j++) {
                            JSONObject resMean = mean.getJSONObject(j);
                            //добавление всех элементов-mean в mMeanText
                            dictionary.addMeanText(resMean.getString("text"));
                        }
                    } catch (JSONException e) {
                    }

                    try {
                        //получение массива значений из строки - ex
                        JSONArray ex = result.getJSONArray("ex");
                        for (int j = 0; j < ex.length(); j++) {
                            JSONObject resEx = ex.getJSONObject(j);
                            String exampleKey = resEx.getString("text");
                            JSONArray exTr = resEx.getJSONArray("tr");
                            for (int k = 0; k < exTr.length(); k++) {
                                JSONObject exTrString = exTr.getJSONObject(k);

                                //добавление в Map все пары значений примера - и его перевода
                                dictionary
                                        .addExamples(exampleKey, exTrString.getString("text"));
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
}
