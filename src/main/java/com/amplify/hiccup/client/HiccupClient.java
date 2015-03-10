package com.amplify.hiccup.client;

import android.content.*;
import android.database.Cursor;
import android.net.Uri;

public class HiccupClient {

    private final Context context;

    public HiccupClient(Context context) {
        this.context = context;
    }

    public Cursor get(Uri uri) {
        return context.getContentResolver().query(uri, null, null, null, null);
    }

    public Uri post(Uri uri, ContentValues contentValues) {
        return context.getContentResolver().insert(uri, contentValues);
    }

    public int put(Uri uri, ContentValues contentValues) {
        return context.getContentResolver().update(uri, contentValues, null, null);
    }

    public int delete(Uri uri) {
        return context.getContentResolver().delete(uri, null, null);
    }

    public BatchRequest newBatch() {
        return new BatchRequest(context);
    }

}
