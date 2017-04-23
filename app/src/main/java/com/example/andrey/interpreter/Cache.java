package com.example.andrey.interpreter;

import java.util.ArrayList;
import java.util.List;

/**
 * Структура для временного хранения 128-и записей
 */

public class Cache {
    private List<ItemCache> mCaches;

    Cache() {
        mCaches = new ArrayList<>();
    }

    // Добавление записи
    public void addInCashe(String nativeText, String foreignText,
                          String langs, List<Dictionary> dictionaries) {

        if (mCaches.size() > 128) {
            deleteCache();
        }

        mCaches.add(new ItemCache(nativeText, foreignText, langs, dictionaries));
    }

    // Если в массиве есть такой элемент, то возвращает его
    public ItemCache getItemCache(String nativeText, String langs) {
        for (ItemCache line :
                mCaches) {
            if ((nativeText + " " + langs).equals(line.toString())) {
                return line;
            }
        }
        return null;
    }

    // Возвращает true - если в массиве есть подходящий элемент
    public boolean searchInCache(String nativeText, String langs) {
        for (ItemCache line :
                mCaches) {
            if ((nativeText + " " + langs).equals(line.toString())) {
                return true;
            }
        }
        return false;
    }

    // Очищает первые 64 записи в кэше
    // Остальные 64 записи копирует в новый кэш
    private void deleteCache() {
        mCaches = mCaches.subList(64, 127);
    }

    // Внутренний класс для хранении одной записи
    public class ItemCache {
        private String mNativeText; // Строка, которую вводит пользователь
        private String mForeignText; // Перевод введенной строки
        private String mLangs; // Строка с парой кодов языков
        private List<Dictionary> mDictionaries; // Массив записей запроса с Яндекс.Словарь

        // Инициализация записи
        ItemCache(String nativeText, String foreignText,
                  String langs, List<Dictionary> dictionaries) {
            mNativeText = nativeText;
            mForeignText = foreignText;
            mLangs = langs;
            mDictionaries = dictionaries;
        }

        public List<Dictionary> getDictionaries() {
            return mDictionaries;
        }

        public String getForeignText() {
            return mForeignText;
        }

        @Override
        public String toString() {
            return mNativeText + " " + mLangs;
        }
    }
}