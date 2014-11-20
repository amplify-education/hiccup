package com.amplify.hiccup.client;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class HiccupClient {

    private final Context context;
    private final RequestAdapter requestAdapter;

    public HiccupClient(Context context, RequestAdapter requestAdapter) {
        this.context = context;
        this.requestAdapter = requestAdapter;
    }

    public Cursor get(Uri uri) {
        return context.getContentResolver().query(uri, null, null, null, null);
    }

    public Uri post(Uri uri, Object object) {
        ContentValues contentValues = requestAdapter.toValues(object);
        return context.getContentResolver().insert(uri, contentValues);
    }
}
