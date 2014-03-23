package com.amplify.hiccup.client;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class HiccupClient {

    private static final String METHOD = "method";
    private static final String BODY = "body";

    private final Context context;

    public HiccupClient(Context context) {
        this.context = context;
    }

    public Cursor get(Uri uri) {
        return context.getContentResolver().query(uri, null, null, null, null);
    }

    public Uri post(Uri uri, String body) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(METHOD, "POST");
        contentValues.put(BODY, body);
        return context.getContentResolver().insert(uri, contentValues);
    }
}
