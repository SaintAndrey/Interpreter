package com.example.andrey.interpreter.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.andrey.interpreter.Structures.ListItem;
import com.example.andrey.interpreter.Db.TranslatorDbSchema.TranslatorTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Методы управления БД
 */

public class WordsListQuery {
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private List<ListItem> mItemsWord;

    public WordsListQuery(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new TranslatorBaseHelper(mContext).getWritableDatabase();

    }

    // Получение элемента типа ListItem по тексту и языку
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

    // Получение массива записей по Истории и Избранным
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
    

    // Добавление записи в БД
    public void addItemWord(ListItem word) {
        ContentValues values = getContentValues(word);

        mDatabase.insert(TranslatorTable.NAME, null, values);
    }

    // Изменение записи в БД
    public void updateItemWord(ListItem word) {
        String text = word.getNativeText();
        String langs = word.getLangs();
        ContentValues values = getContentValues(word);

        mDatabase.update(TranslatorTable.NAME, values,
                TranslatorTable.Cols.NATIVE + " = ? AND " + TranslatorTable.Cols.LANGS + " = ?",
                new String[] { text , langs}
        );
    }

    // Удаление записи из Истории
    public void deleteItemWordFromHistory(String text, String langs) {
        ListItem item = getWord(text, langs);
        if (!item.isFavorite()) {
            mDatabase.delete(TranslatorTable.NAME,
                    TranslatorTable.Cols.NATIVE + " = '" + text + "' AND " +
                            TranslatorTable.Cols.LANGS + " = '" + langs + "'",
                    null);
        } else {
            item.setHistory(false);
            ContentValues value = getContentValues(item);
            mDatabase.update(TranslatorTable.NAME, value,
                    TranslatorTable.Cols.NATIVE + " = ? AND " + TranslatorTable.Cols.LANGS + " = ?",
                    new String[] { text, langs});
        }
    }

    // Удаление записи из Избранного
    public void deleteItemWordFromFavorite(String text, String langs) {
        ListItem item = getWord(text, langs);
        if (!item.isHistory()) {
            mDatabase.delete(TranslatorTable.NAME,
                    TranslatorTable.Cols.NATIVE + " = '" + text + "' AND " +
                            TranslatorTable.Cols.LANGS + " = '" + langs + "'",
                    null);
        } else {
            item.setFavorite(false);
            ContentValues value = getContentValues(item);
            mDatabase.update(TranslatorTable.NAME, value,
                    TranslatorTable.Cols.NATIVE + " = ? AND " + TranslatorTable.Cols.LANGS + " = ?",
                    new String[] { text, langs});
        }
    }

    // Удаление всех записей из Истории
    public void deleteAllHistory() {
        mDatabase.delete(TranslatorTable.NAME,
                TranslatorTable.Cols.FAVORITE + " = 'No'", null);

        List<ListItem> items = getWords(TranslatorTable.Cols.HISTORY + " = ?");

        for (int i = 0; i < items.size(); i++) {
            items.get(i).setHistory(false);
            ContentValues values = getContentValues(items.get(i));
            mDatabase.update(TranslatorTable.NAME, values,
                    TranslatorTable.Cols.NATIVE + " = ? AND " + TranslatorTable.Cols.LANGS + " = ?",
                     new String[] { items.get(i).getNativeText(), items.get(i).getLangs()});
        }
    }

    // Удаление всех записей из Избранного
    public void deleteAllFavorite() {
        mDatabase.delete(TranslatorTable.NAME,
                TranslatorTable.Cols.HISTORY + " = 'No'", null);

        List<ListItem> items = getWords(TranslatorTable.Cols.FAVORITE + " = ?");

        for (int i = 0; i < items.size(); i++) {
            items.get(i).setFavorite(false);
            ContentValues values = getContentValues(items.get(i));
            mDatabase.update(TranslatorTable.NAME, values,
                    TranslatorTable.Cols.NATIVE + " = ? AND " + TranslatorTable.Cols.LANGS + " = ?",
                    new String[] { items.get(i).getNativeText(), items.get(i).getLangs()});
        }
    }

    // Формирование записи из БД
    private static ContentValues getContentValues(ListItem word) {
        ContentValues values = new ContentValues();
        values.put(TranslatorTable.Cols.NATIVE, word.getNativeText());
        values.put(TranslatorTable.Cols.FOREIGN, word.getForeignText());
        values.put(TranslatorTable.Cols.LANGS, word.getLangs());
        values.put(TranslatorTable.Cols.HISTORY, (word.isHistory() ? "Yes" : "No"));
        values.put(TranslatorTable.Cols.FAVORITE, (word.isFavorite() ? "Yes" : "No"));
        values.put(TranslatorTable.Cols.JSON_FILE, word.getJSONFile());

        return values;
    }

    // Поиск по БД через курсор
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
