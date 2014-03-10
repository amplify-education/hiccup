package com.amplify.hiccup.service;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;

import java.util.Collections;

public class HttpCursorFactory {

    private static final String BODY_COLUMN = "body";

    public Cursor createCursor(Response response) {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{BaseColumns._ID, BODY_COLUMN});
        if (response == null) {
            return matrixCursor;
        }

        Iterable<Object> items = response.getResults();
        if (items == null) {
            items = Collections.emptyList();
        }

        for (Object item : items) {
            addRowToCursor(matrixCursor, response, item);
        }
        return matrixCursor;
    }

    private void addRowToCursor(MatrixCursor matrixCursor, Response response, Object model) {
        int currentSize = matrixCursor.getCount();
        matrixCursor.addRow(new Object[]{
                currentSize + 1,
                response.getBody(model)
        });
    }

}
