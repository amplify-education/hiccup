package com.amplify.hiccup.service;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;

import java.util.Arrays;
import java.util.Collections;

public class HttpCursorFactory {

    private static final String BODY_COLUMN = "body";

    private final JsonConverter jsonConverter;

    public HttpCursorFactory(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    public Cursor createCursor(Object modelToConvert) {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{BaseColumns._ID, BODY_COLUMN});
        Iterable<Object> modelItems;
        if (modelToConvert == null) {
            modelItems = Collections.emptyList();
        }
        else if (modelToConvert instanceof Iterable) {
            modelItems = (Iterable) modelToConvert;
        }
        else {
            modelItems = Arrays.asList(modelToConvert);
        }

        for (Object model : modelItems) {
            addRowToCursor(matrixCursor, model);
        }
        return matrixCursor;
    }

    private void addRowToCursor(MatrixCursor matrixCursor, Object object) {
        int currentSize = matrixCursor.getCount();
        matrixCursor.addRow(new Object[]{
                currentSize + 1,
                jsonConverter.toJson(object)
        });
    }
}
