package com.amplify.hiccup.client;

import android.content.ContentValues;

import com.amplify.hiccup.shared.JsonConverter;

public class HttpRequestAdapter implements RequestAdapter {

    private static final String BODY = "body";

    private final JsonConverter jsonConverter;

    public HttpRequestAdapter(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    @Override
    public ContentValues toValues(Object model) {
        ContentValues contentValues = new ContentValues();
        String body = jsonConverter.toJson(model);
        contentValues.put(BODY, body);
        return contentValues;
    }
}
