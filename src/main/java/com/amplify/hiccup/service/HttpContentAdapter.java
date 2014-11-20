package com.amplify.hiccup.service;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;

import com.amplify.hiccup.shared.JsonConverter;

import java.util.Collections;

public class HttpContentAdapter implements ContentAdapter<Object> {

    private static final String BODY_COLUMN = "body";

    private final JsonConverter jsonConverter;

    public HttpContentAdapter(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    @Override
    public Cursor toCursor(Iterable<Object> result) {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{BaseColumns._ID, BODY_COLUMN});
        Iterable<Object> items = result;
        if (items == null) {
            items = Collections.emptyList();
        }

        for (Object item : items) {
            addRowToCursor(matrixCursor, item);
        }
        return matrixCursor;
    }

    @Override
    public Object toModel(ContentValues contentValues, Class<?> modelClass) {
        String body = contentValues.getAsString(BODY_COLUMN);
        return jsonConverter.fromJson(body, modelClass);
    }

    private void addRowToCursor(MatrixCursor matrixCursor, Object model) {
        int currentSize = matrixCursor.getCount();
        matrixCursor.addRow(new Object[]{
                currentSize + 1,
                jsonConverter.toJson(model)
        });
    }
}
