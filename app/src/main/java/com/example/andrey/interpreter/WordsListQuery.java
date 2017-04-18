package com.example.andrey.interpreter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.andrey.interpreter.TranslatorDbSchema.TranslatorTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrey on 17/04/2017.
 */

public class WordsListQuery {
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private List<ListItem> mItemsWord;

    public WordsListQuery(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new TranslatorBaseHelper(mContext).getWritableDatabase();

    }

    public ListItem getWord(String text) {
        WordCursorWrapper cursor = queryWords(
                TranslatorTable.Cols.NATIVE + " = ?",
                new String[] { text }
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getWord();
        } finally {
            cursor.close();
        }
    }

    public List<ListItem> getWords(String whereClause) {
        mItemsWord = new ArrayList<>();

        WordCursorWrapper cursor = queryWords(
                TranslatorTable.Cols.FAVORITE + " = ?",
                new String[] { Integer.toString(10) });

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                mItemsWord.add(cursor.getWord());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return mItemsWord;
    }

    public List<ListItem> getWords() {
        mItemsWord = new ArrayList<>();

        WordCursorWrapper cursor = queryWords(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                mItemsWord.add(cursor.getWord());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return mItemsWord;
    }

    public void addItemWord(ListItem word) {
        ContentValues values = getContentvalues(word);

        mDatabase.insert(TranslatorTable.NAME, null, values);
    }

    public void updateList(ListItem word) {
        String text = word.getNativeText();
        ContentValues values = getContentvalues(word);

        mDatabase.update(TranslatorTable.NAME, values,
                TranslatorTable.Cols.NATIVE + " = ?",
                new String[] { text });
    }

    private static ContentValues getContentvalues(ListItem word) {
        ContentValues values = new ContentValues();
        values.put(TranslatorTable.Cols.NATIVE, word.getNativeText());
        Log.d("db", "Put: " + word.getNativeText());
        values.put(TranslatorTable.Cols.FOREIGN, word.getForeignText());
        Log.d("db", "Put: " + word.getForeignText());
        values.put(TranslatorTable.Cols.LANGS, word.getLangs());
        Log.d("db", "Put: " + word.getLangs());
        values.put(TranslatorTable.Cols.HISTORY, (word.isHistory() ? 1 : 0));
        Log.d("db", "Put: " + (word.isHistory() ? 1 : 0));
        values.put(TranslatorTable.Cols.FAVORITE, (word.isFavorite() ? 1 : 0));
        Log.d("db", "Put: " + (word.isFavorite() ? 1 : 0));
        values.put(TranslatorTable.Cols.JSON_FILE, word.getJSONFile());
        Log.d("db", "Put: " + word.getJSONFile());

        return values;
    }

    private WordCursorWrapper queryWords(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                TranslatorTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new WordCursorWrapper(cursor);
    }
}
