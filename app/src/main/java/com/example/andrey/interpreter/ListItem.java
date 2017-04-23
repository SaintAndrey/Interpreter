package com.example.andrey.interpreter;

/**
 * Структура хранения записей для хранения в БД
 * mNativeText - введенное слово
 * mForeignText - перевод
 * mLangs - пара языков
 * mJSONFile - строка типа json
 * mHistory - находится ли запись в Истории
 * mFavorite - находится ли запись в Избранном
 */

public class ListItem {
    private String mNativeText;
    private String mForeignText;
    private String mLangs;
    private String mJSONFile;
    private boolean mHistory;
    private boolean mFavorite;

    public String getLangs() {
        return mLangs;
    }

    public void setLangs(String langs) {
        mLangs = langs;
    }

    public boolean isFavorite() {
        return mFavorite;
    }

    public void setFavorite(boolean favorite) {
        mFavorite = favorite;
    }

    public String getJSONFile() {
        return mJSONFile;
    }

    public void setJSONFile(String JSONFile) {
        mJSONFile = JSONFile;
    }

    public boolean isHistory() {
        return mHistory;
    }

    public void setHistory(boolean history) {
        mHistory = history;
    }

    public String getForeignText() {
        return mForeignText;
    }

    public void setForeignText(String foreignText) {
        mForeignText = foreignText;
    }

    public String getNativeText() {
        return mNativeText;
    }

    public void setNativeText(String nativeText) {
        mNativeText = nativeText;
    }
}