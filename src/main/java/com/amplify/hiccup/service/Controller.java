package com.amplify.hiccup.service;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public interface Controller {
    Cursor get(Uri uri);
    Uri post(Uri uri, ContentValues contentValues);
    int put(Uri uri, ContentValues contentValues);
    int delete(Uri uri);
    int patch(Uri uri, ContentValues[] contentValues);
}
