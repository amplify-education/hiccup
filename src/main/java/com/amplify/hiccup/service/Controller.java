package com.amplify.hiccup.service;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public interface Controller {
    Cursor get(Uri uri);
    Uri post(Uri uri, ContentValues model);
}
