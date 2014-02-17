package com.amplify.hiccup.client;

import android.content.Context;
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
}
