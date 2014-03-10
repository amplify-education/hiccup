package com.amplify.hiccup.service;

import android.content.ContentValues;
import android.net.Uri;

public interface Controller {
    Response get(Uri uri);
    Uri post(Uri uri, ContentValues contentValues);
}
