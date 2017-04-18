package com.example.andrey.interpreter;

/**
 * Created by Andrey on 17/04/2017.
 */

public class TranslatorDbSchema {
    public static final class TranslatorTable {
        public static final String NAME = "translator";

        public static final class Cols {
            public static final String NATIVE = "native_text";
            public static final String FOREIGN = "foreign_text";
            public static final String LANGS = "langs";
            public static final String HISTORY = "history";
            public static final String FAVORITE = "favorite";
            public static final String JSON_FILE = "json";
        }
    }
}
