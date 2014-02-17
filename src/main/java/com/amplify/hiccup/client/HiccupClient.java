package com.amplify.hiccup.client;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.amplify.hiccup.service.JsonConverter;

public class HiccupClient {

    private static final String METHOD = "method";
    private static final String BODY = "body";

    private final Context context;
    private final JsonConverter jsonConverter;

    public HiccupClient(Context context, JsonConverter jsonConverter) {
        this.context = context;
        this.jsonConverter = jsonConverter;
    }

    public Cursor get(Uri uri) {
        return context.getContentResolver().query(uri, null, null, null, null);
    }

    public Uri post(Uri uri, Object model) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(METHOD, "POST");
        contentValues.put(BODY, jsonConverter.toJson(model));
        return context.getContentResolver().insert(uri, contentValues);
    }
}
