package com.amplify.hiccup.service;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;

public class HttpCursorFactory {

    private static final String BODY_COLUMN = "body";

    private JsonConverter jsonConverter;

    public HttpCursorFactory(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    public Cursor from(Object object) {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{BaseColumns._ID, BODY_COLUMN});
        matrixCursor.addRow(new Object[]{
                1,
                jsonConverter.toJson(object)
        });
        return matrixCursor;
    }
}
