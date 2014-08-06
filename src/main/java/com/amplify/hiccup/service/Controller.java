package com.amplify.hiccup.service;

import android.content.ContentValues;
import android.net.Uri;

public interface Controller<R> {
    Iterable<R> get(Uri uri);
    Uri post(Uri uri, R model);
}
