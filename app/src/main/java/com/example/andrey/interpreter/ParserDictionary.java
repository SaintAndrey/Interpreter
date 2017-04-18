package com.example.andrey.interpreter;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrey on 18/04/2017.
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
}
