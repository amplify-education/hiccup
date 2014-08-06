package com.amplify.hiccup.service;

import android.content.ContentValues;
import android.database.Cursor;

public interface ContentAdapter<R> {
    Cursor createCursor(Iterable<R> result);
    R toModel(ContentValues contentValues, Class<? extends R> modelClass);
}
