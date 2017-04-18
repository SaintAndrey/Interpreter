package com.example.andrey.interpreter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.andrey.interpreter.TranslatorDbSchema.TranslatorTable;

/**
 * Created by Andrey on 17/04/2017.
 */

public class TranslatorBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "words_list.db";

    public TranslatorBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + TranslatorTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                TranslatorTable.Cols.NATIVE + ", " +
                TranslatorTable.Cols.FOREIGN + ", " +
                TranslatorTable.Cols.LANGS + ", " +
                TranslatorTable.Cols.HISTORY + ", " +
                TranslatorTable.Cols.FAVORITE + ", " +
                TranslatorTable.Cols.JSON_FILE +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
