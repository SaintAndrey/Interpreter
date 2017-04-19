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

    public ListItem getWord(String text, String langs) {
        WordCursorWrapper cursor = queryWords(
                TranslatorTable.Cols.NATIVE + " = ? AND " + TranslatorTable.Cols.LANGS + " = ?",
                new String[] { text , langs}
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
                whereClause,
                new String[] { "Yes" });

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

    public void updateItemWord(ListItem word) {
        String text = word.getNativeText();
        String langs = word.getLangs();
        ContentValues values = getContentvalues(word);

        mDatabase.update(TranslatorTable.NAME, values,
                TranslatorTable.Cols.NATIVE + " = ? AND " + TranslatorTable.Cols.LANGS + " = ?",
                new String[] { text , langs}
        );
    }

    public void deleteItemWord(String text) {
        mDatabase.delete(TranslatorTable.NAME,
                TranslatorTable.Cols.NATIVE + " = '" + text + "'",
                null);
    }

    private static ContentValues getContentvalues(ListItem word) {
        ContentValues values = new ContentValues();
        values.put(TranslatorTable.Cols.NATIVE, word.getNativeText());
        values.put(TranslatorTable.Cols.FOREIGN, word.getForeignText());
        values.put(TranslatorTable.Cols.LANGS, word.getLangs());
        values.put(TranslatorTable.Cols.HISTORY, (word.isHistory() ? "Yes" : "No"));
        values.put(TranslatorTable.Cols.FAVORITE, (word.isFavorite() ? "Yes" : "No"));
        values.put(TranslatorTable.Cols.JSON_FILE, word.getJSONFile());

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
