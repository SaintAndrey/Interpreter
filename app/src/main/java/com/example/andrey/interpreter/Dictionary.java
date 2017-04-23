package com.example.andrey.interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс для хранения результата из запроса Яндекс.Словарь
 * mTranslatedText - перевод и синонимы
 * mMeanText - лист подразумивающих слов
 * mExamples - примеры
 */

public class Dictionary {

    private List<String> mTranslatedText
            = new ArrayList<>();
    private List<String> mMeanText
            = new ArrayList<>();
    private Map<String, String> mExamples
            = new HashMap<>();

    public void addTranslatedText(String text) {
        mTranslatedText.add(text);
    }

    public void addMeanText(String text) {
        mMeanText.add(text);
    }

    public void addExamples(String key, String value) {
        mExamples.put(key, value);
    }

    public Map<String, String> getExamples() {
        return mExamples;
    }

    public List<String> getMeansText() {
        return mMeanText;
    }

    public List<String> getTranslatedText() {
        return mTranslatedText;
    }

}