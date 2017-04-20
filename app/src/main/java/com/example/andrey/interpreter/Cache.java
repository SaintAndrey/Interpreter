package com.example.andrey.interpreter;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by Andrey on 20/04/2017.
 */

public class Cache {
    private List<ItemCache> mCaches;
    private ItemCache mItemCache;

    Cache() {
        mCaches = new ArrayList<>();
    }

    public void addInCashe(String nativeText, String foreignText,
                          String langs, List<Dictionary> dictionaries) {

        if (mCaches.size() > 128) {
            deleteCache();
        }

        mCaches.add(new ItemCache(nativeText, foreignText, langs, dictionaries));
    }

    public ItemCache getItemCache(String nativeText, String langs) {
        for (ItemCache line :
                mCaches) {
            if ((nativeText + " " + langs).equals(line.toString())) {
                return line;
            }
        }
        return null;
    }

    public boolean searchInCache(String nativeText, String langs) {
        for (ItemCache line :
                mCaches) {
            Log.d("cache", nativeText + " " + langs + " - " + line.toString());
            if ((nativeText + " " + langs).equals(line.toString())) {
                return true;
            }
        }
        return false;
    }

    private void deleteCache() {
        mCaches = mCaches.subList(0, 63);
    }

    public class ItemCache {
        private String mNativeText;
        private String mForeignText;
        private String mLangs;
        private List<Dictionary> mDictionaries;

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
