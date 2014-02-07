package com.amplify.hiccup.service;

import android.database.Cursor;
import android.net.Uri;

public interface Controller {
    Cursor get(Uri uri);
}
