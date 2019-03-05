package com.spoonexample.sqlitenoteapp1;

import android.provider.BaseColumns;

public class Note {

    private Note() {
    }

    // base columns provides a id column for our table.
    public static final class NoteEntry implements BaseColumns {
        public static final String TABLE_NAME = "noteList";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_BODY = "body";
        public static final String COLUMN_TIMESTAMP = "timestamp";

    }
}
