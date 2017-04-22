package com.example.andrey.interpreter;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.andrey.interpreter.TranslatorDbSchema.TranslatorTable;

/**
 * Курсор для БД
 */

public class WordCursorWrapper extends CursorWrapper {

    public WordCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public ListItem getWord() {
        String native_text = getString(getColumnIndex(TranslatorTable.Cols.NATIVE));
        String foreign_text = getString(getColumnIndex(TranslatorTable.Cols.FOREIGN));
        String langs_text = getString(getColumnIndex(TranslatorTable.Cols.LANGS));
        String history = getString(getColumnIndex(TranslatorTable.Cols.HISTORY));
        String favorite = getString(getColumnIndex(TranslatorTable.Cols.FAVORITE));
        String json = getString(getColumnIndex(TranslatorTable.Cols.JSON_FILE));

        ListItem word = new ListItem();
        word.setNativeText(native_text);
        word.setForeignText(foreign_text);
        word.setLangs(langs_text);
        word.setHistory(history.equals("Yes") ? true : false);
        word.setFavorite(favorite.equals("Yes") ? true : false);
        word.setJSONFile(json);

        return word;
    }
}
