package com.amplify.hiccup.service;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public abstract class AbstractController<R> implements Controller {

    private final ContentAdapter contentAdapter;
    private final Class<R> modelClass;

    public AbstractController(ContentAdapter contentAdapter, Class<R> modelClass) {
        this.contentAdapter = contentAdapter;
        this.modelClass = modelClass;
    }

    @Override
    public final Cursor get(Uri uri) {
        Iterable<R> models = handleGet(uri);
        return contentAdapter.toCursor(models);
    }

    @Override
    public final Uri post(Uri uri, ContentValues contentValues) {
        R model = (R) contentAdapter.toModel(contentValues, modelClass);
        return handlePost(uri, model);
    }

    @Override
    public final int put(Uri uri, ContentValues contentValues) {
        R model = (R) contentAdapter.toModel(contentValues, modelClass);
        return handlePut(uri, model);
    }

    protected abstract Iterable<R> handleGet(Uri uri);
    protected abstract Uri handlePost(Uri uri, R model);
    protected abstract int handlePut(Uri uri, R model);
}