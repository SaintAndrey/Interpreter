package com.example.andrey.interpreter;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.andrey.interpreter.TranslatorDbSchema.TranslatorTable;

/**
 * Created by Andrey on 18/04/2017.
 */

public class WordCursorWrapper extends CursorWrapper {

    public WordCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public ListItem getWord() {
        String native_text = getString(getColumnIndex(TranslatorTable.Cols.NATIVE));
        String foreign_text = getString(getColumnIndex(TranslatorTable.Cols.FOREIGN));
        String langs_text = getString(getColumnIndex(TranslatorTable.Cols.LANGS));
        int history = getInt(getColumnIndex(TranslatorTable.Cols.HISTORY));
        int favorite = getInt(getColumnIndex(TranslatorTable.Cols.FAVORITE));
        String json = getString(getColumnIndex(TranslatorTable.Cols.JSON_FILE));

        ListItem word = new ListItem();
        word.setNativeText(native_text);
        word.setForeignText(foreign_text);
        word.setLangs(langs_text);
        word.setHistory(history != 0);
        word.setFavorite(favorite != 0);
        word.setJSONFile(json);

        return word;
    }
}
